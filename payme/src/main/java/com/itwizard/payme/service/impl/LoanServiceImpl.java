package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.Loan;
import com.itwizard.payme.domain.LoanApplication;
import com.itwizard.payme.domain.LoanProduct;
import com.itwizard.payme.domain.User;
import com.itwizard.payme.domain.enums.LoanApplicationStatus;
import com.itwizard.payme.domain.enums.LoanStatus;
import com.itwizard.payme.dto.LoanEligibilityResponse;
import com.itwizard.payme.dto.LoanProductOption;
import com.itwizard.payme.dto.request.LoanApplicationRequest;
import com.itwizard.payme.dto.request.LoanProductRequest;
import com.itwizard.payme.dto.response.LoanApplicationResponse;
import com.itwizard.payme.exception.BadRequestException;
import com.itwizard.payme.exception.ResourceNotFoundException;
import com.itwizard.payme.repository.LoanApplicationRepository;
import com.itwizard.payme.domain.LoanEligibility;
import com.itwizard.payme.repository.LoanEligibilityRepository;
import com.itwizard.payme.repository.LoanProductRepository;
import com.itwizard.payme.repository.LoanRepository;
import com.itwizard.payme.repository.UserRepository;
import com.itwizard.payme.service.LoanScoringService;
import com.itwizard.payme.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanProductRepository loanProductRepository;
    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final LoanScoringService loanScoringService;
    private final LoanEligibilityRepository loanEligibilityRepository;
    private final com.itwizard.payme.repository.WalletRepository walletRepository;
    private final com.itwizard.payme.repository.TransactionRepository transactionRepository;

    @Override
    @Transactional
    public LoanEligibilityResponse checkProductEligibility(UUID userId, UUID productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LoanProduct product = loanProductRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));

        // 1. Check if user already has an active loan
        if (!loanRepository.findByUserAndStatus(user, LoanStatus.ACTIVE).isEmpty()) {
            return LoanEligibilityResponse.builder()
                    .success(false)
                    .message("You already have an active loan.")
                    .build();
        }

        // 2. Perform scoring
        BigDecimal maxLimit = loanScoringService.calculateMaxEligibleAmount(user, product);

        String statusMessage;
        boolean isEligible;
        if (maxLimit.compareTo(product.getMinAmount()) < 0) {
            statusMessage = "Your scoring does not meet the minimum requirements for this product.";
            maxLimit = BigDecimal.ZERO;
            isEligible = false;
        } else {
            statusMessage = "Eligible for loan product: " + product.getName();
            isEligible = true;
        }

        // 3. Persist result
        LoanEligibility eligibility = loanEligibilityRepository.findByUserAndProduct(user, product)
                .orElse(LoanEligibility.builder()
                        .user(user)
                        .product(product)
                        .build());

        eligibility.setMaxEligibleAmount(maxLimit);
        eligibility.setStatusMessage(statusMessage);
        eligibility.setChecked(true);

        loanEligibilityRepository.save(eligibility);

        // 4. Return simple success/failure status
        return LoanEligibilityResponse.builder()
                .success(isEligible)
                .message(statusMessage)
                .build();
    }

    @Override
    @Transactional
    public LoanApplicationResponse applyForLoan(UUID userId, LoanApplicationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LoanProduct product = loanProductRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan product not found"));

        // 1. Double check active loans
        if (!loanRepository.findByUserAndStatus(user, LoanStatus.ACTIVE).isEmpty()) {
            throw new BadRequestException("You already have an active loan.");
        }

        // 2. Validate tenor
        if (request.getTenorMonths() < product.getMinTenorMonths()
                || request.getTenorMonths() > product.getMaxTenorMonths()) {
            throw new BadRequestException("Requested tenor is out of product range ("
                    + product.getMinTenorMonths() + " - " + product.getMaxTenorMonths() + " months).");
        }

        // 3. Perform scoring
        BigDecimal maxLimit = loanScoringService.calculateMaxEligibleAmount(user, product);

        if (maxLimit.compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("You are not eligible for a loan due to poor credit score.");
        }

        if (request.getRequestedAmount().compareTo(maxLimit) > 0) {
            throw new BadRequestException("Requested amount exceeds your eligible limit of " + maxLimit);
        }

        if (request.getRequestedAmount().compareTo(product.getMinAmount()) < 0) {
            throw new BadRequestException("Requested amount is below product minimum of " + product.getMinAmount());
        }

        // 4. Create Application
        LoanApplication application = new LoanApplication();
        application.setUser(user);
        application.setProduct(product);
        application.setRequestedAmount(request.getRequestedAmount());
        application.setMaxEligibleAmount(maxLimit);
        application.setTenorMonths(request.getTenorMonths());
        application.setStatus(LoanApplicationStatus.APPROVED);
        application.setDisbursedAt(java.time.LocalDateTime.now());

        LoanApplication savedApplication = loanApplicationRepository.save(application);

        // 5. Create Active Loan
        Loan loan = new Loan();
        loan.setApplication(savedApplication);
        loan.setUser(user);

        // Find user's wallet
        com.itwizard.payme.domain.Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user"));
        loan.setWallet(wallet);

        loan.setPrincipalAmount(request.getRequestedAmount());
        loan.setRemainingBalance(request.getRequestedAmount());
        loan.setInterestRate(product.getInterestRateMonthly());
        loan.setPenaltyRate(product.getPenaltyRateDaily());
        loan.setStatus(LoanStatus.ACTIVE);
        LocalDateTime now = java.time.LocalDateTime.now();
        loan.setDisbursedAt(now);
        loan.setStartDate(now);
        loan.setEndDate(now.plusMonths(request.getTenorMonths()));

        loanRepository.save(loan);

        // 6. Credit Wallet
        wallet.setBalance(wallet.getBalance().add(request.getRequestedAmount()));
        walletRepository.save(wallet);

        // 7. Create Transaction Record
        com.itwizard.payme.domain.Transaction transaction = new com.itwizard.payme.domain.Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(request.getRequestedAmount());
        transaction.setType(com.itwizard.payme.domain.enums.TransactionType.LOAN_DISBURSEMENT);
        transaction.setStatus(com.itwizard.payme.domain.enums.TransactionStatus.COMPLETED);
        transaction.setDescription("Loan disbursement for application: " + savedApplication.getId());
        transaction.setCreatedAt(java.time.LocalDateTime.now());

        transactionRepository.save(transaction);

        // 8. Deduct eligibility limit
        // Retrieve the eligibility record again to ensure we have the latest one
        LoanEligibility eligibility = loanEligibilityRepository.findByUserAndProduct(user, product).orElse(null);
        if (eligibility != null) {
            BigDecimal newLimit = eligibility.getMaxEligibleAmount().subtract(request.getRequestedAmount());
            if (newLimit.compareTo(BigDecimal.ZERO) < 0) {
                newLimit = BigDecimal.ZERO;
            }
            eligibility.setMaxEligibleAmount(newLimit);
            eligibility.setStatusMessage("Active Loan: " + request.getRequestedAmount());
            loanEligibilityRepository.save(eligibility);
        }

        return LoanApplicationResponse.builder()
                .applicationId(savedApplication.getId())
                .userId(user.getId())
                .productId(product.getId())
                .requestedAmount(savedApplication.getRequestedAmount())
                .maxEligibleAmount(savedApplication.getMaxEligibleAmount())
                .tenorMonths(savedApplication.getTenorMonths())
                .status(savedApplication.getStatus())
                .message("Loan successfully disbursed. Amount " + request.getRequestedAmount()
                        + " added to your wallet.")
                .createdAt(savedApplication.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public LoanProduct createLoanProduct(LoanProductRequest request) {
        if (request.getMinAmount().compareTo(request.getMaxAmount()) > 0) {
            throw new BadRequestException("Minimum amount cannot be greater than maximum amount.");
        }
        if (request.getMinTenorMonths() > request.getMaxTenorMonths()) {
            throw new BadRequestException("Minimum tenor cannot be greater than maximum tenor.");
        }

        LoanProduct product = getLoanProduct(request);

        return loanProductRepository.save(product);
    }

    private static LoanProduct getLoanProduct(LoanProductRequest request) {
        LoanProduct product = new LoanProduct();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setInterestRateMonthly(request.getInterestRateMonthly());
        product.setPenaltyRateDaily(request.getPenaltyRateDaily());
        product.setMinAmount(request.getMinAmount());
        product.setMaxAmount(request.getMaxAmount());
        product.setMinTenorMonths(request.getMinTenorMonths());
        product.setMaxTenorMonths(request.getMaxTenorMonths());
        product.setScoringMultiplier(request.getScoringMultiplier());
        return product;
    }

    @Override
    public List<LoanProductOption> getAllLoanProducts(UUID userId) {
        // Get all loan products and current eligibility statuses
        List<LoanProduct> products = loanProductRepository.findAll();
        List<LoanEligibility> checks = loanEligibilityRepository.findByUserId(userId);

        // Map products to options with check status
        return products.stream()
                .map(product -> {
                    Optional<LoanEligibility> eligibilityCheck = checks.stream()
                            .filter(c -> c.getProduct().getId().equals(product.getId()))
                            .findFirst();

                    BigDecimal maxLimit = eligibilityCheck.map(LoanEligibility::getMaxEligibleAmount)
                            .orElse(BigDecimal.ZERO);
                    boolean isChecked = eligibilityCheck.map(LoanEligibility::isChecked).orElse(false);
                    String statusMessage = eligibilityCheck.map(LoanEligibility::getStatusMessage)
                            .orElse("Not Checked");

                    return LoanProductOption.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .description(product.getDescription())
                            .minAmount(product.getMinAmount())
                            .maxAmount(product.getMaxAmount())
                            .maxEligibleAmount(maxLimit)
                            .interestRateMonthly(product.getInterestRateMonthly())
                            .penaltyRateDaily(product.getPenaltyRateDaily())
                            .minTenorMonths(product.getMinTenorMonths())
                            .maxTenorMonths(product.getMaxTenorMonths())
                            .statusMessage(statusMessage)
                            .isChecked(isChecked)
                            .build();
                })
                .toList();
    }
}

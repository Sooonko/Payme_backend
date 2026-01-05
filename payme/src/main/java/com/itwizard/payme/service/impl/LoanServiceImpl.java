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
import com.itwizard.payme.repository.LoanProductRepository;
import com.itwizard.payme.repository.LoanRepository;
import com.itwizard.payme.repository.UserRepository;
import com.itwizard.payme.service.LoanScoringService;
import com.itwizard.payme.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanProductRepository loanProductRepository;
    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final LoanScoringService loanScoringService;

    @Override
    public LoanEligibilityResponse checkEligibility(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1. Check if user already has an active loan
        List<Loan> activeLoans = loanRepository.findByUserAndStatus(user, LoanStatus.ACTIVE);
        if (!activeLoans.isEmpty()) {
            return LoanEligibilityResponse.builder()
                    .eligible(false)
                    .message("You already have an active loan. Please close it before applying for a new one.")
                    .build();
        }

        // 2. Get all loan products
        List<LoanProduct> products = loanProductRepository.findAll();
        if (products.isEmpty()) {
            return LoanEligibilityResponse.builder()
                    .eligible(false)
                    .message("No loan products are currently available.")
                    .build();
        }

        // 3. Calculate limit for each product
        List<LoanProductOption> options = products.stream()
                .map(product -> {
                    BigDecimal maxLimit = loanScoringService.calculateMaxEligibleAmount(user, product);

                    String statusMessage;
                    BigDecimal finalLimit = maxLimit;

                    if (maxLimit.compareTo(product.getMinAmount()) < 0) {
                        statusMessage = "Your scoring does not meet the minimum requirements for this product.";
                        finalLimit = BigDecimal.ZERO;
                    } else {
                        statusMessage = "Eligible";
                    }

                    return LoanProductOption.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .maxEligibleAmount(finalLimit)
                            .interestRateMonthly(product.getInterestRateMonthly())
                            .penaltyRateDaily(product.getPenaltyRateDaily())
                            .minTenorMonths(product.getMinTenorMonths())
                            .maxTenorMonths(product.getMaxTenorMonths())
                            .statusMessage(statusMessage)
                            .build();
                })
                .toList();

        boolean anyEligible = options.stream()
                .anyMatch(opt -> opt.getMaxEligibleAmount().compareTo(BigDecimal.ZERO) > 0);

        return LoanEligibilityResponse.builder()
                .eligible(anyEligible)
                .message(anyEligible ? "Wait! You have loan options available."
                        : "Currently, you don't qualify for any loan products.")
                .options(options)
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
        application.setStatus(LoanApplicationStatus.PENDING);

        LoanApplication savedApplication = loanApplicationRepository.save(application);

        return LoanApplicationResponse.builder()
                .applicationId(savedApplication.getId())
                .userId(user.getId())
                .productId(product.getId())
                .requestedAmount(savedApplication.getRequestedAmount())
                .maxEligibleAmount(savedApplication.getMaxEligibleAmount())
                .tenorMonths(savedApplication.getTenorMonths())
                .status(savedApplication.getStatus())
                .message("Loan application submitted successfully. It is currently under review.")
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
        return product;
    }

    @Override
    public List<LoanProduct> getAllLoanProducts() {
        return loanProductRepository.findAll();
    }
}

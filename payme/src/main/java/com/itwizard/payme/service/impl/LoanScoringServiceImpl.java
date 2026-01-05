package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.LoanProduct;
import com.itwizard.payme.domain.User;
import com.itwizard.payme.domain.Wallet;
import com.itwizard.payme.repository.TransactionRepository;
import com.itwizard.payme.repository.WalletRepository;
import com.itwizard.payme.service.LoanScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LoanScoringServiceImpl implements LoanScoringService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    @Override
    public BigDecimal calculateMaxEligibleAmount(User user, LoanProduct product) {
        // --- 1. MOCK EXTERNAL SCORING (Зээлийн Мэдээллийн Сан - ЗМС) ---
        // In a real implementation, this would be a REST call to the Credit Bureau API.
        // For this example, we use fixed constant values.

        boolean hasBadCreditHistory = false; // Mock: User has no negative history
        BigDecimal externalCreditLimit = new BigDecimal("5000000"); // Mock: ZMS says user can take up to 5M
        BigDecimal currentDebtInOtherBanks = new BigDecimal("1000000"); // Mock: User has 1M debt elsewhere

        // If user has bad credit history, they get 0 limit immediately
        if (hasBadCreditHistory) {
            return BigDecimal.ZERO;
        }

        // --- 2. INTERNAL SCORING FALLBACK/AUGMENTATION ---
        BigDecimal baseAmount = product.getMinAmount();

        // A. Wallet Balance Factor (50% of current balance)
        BigDecimal currentBalance = walletRepository.findByUserId(user.getId())
                .map(Wallet::getBalance)
                .orElse(BigDecimal.ZERO);
        BigDecimal balanceBonus = currentBalance.multiply(new BigDecimal("0.5"));

        // B. Transaction Volume Factor (10% of volume in last 3 months)
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minus(3, ChronoUnit.MONTHS);
        BigDecimal totalVolume = transactionRepository.calculateTotalVolume(user.getId(), threeMonthsAgo);
        if (totalVolume == null)
            totalVolume = BigDecimal.ZERO;
        BigDecimal volumeBonus = totalVolume.multiply(new BigDecimal("0.1"));

        // C. Account Age Factor
        long accountAgeMonths = ChronoUnit.MONTHS.between(user.getCreatedAt(), LocalDateTime.now());
        BigDecimal ageMultiplier = BigDecimal.ONE
                .add(new BigDecimal("0.05").multiply(new BigDecimal(accountAgeMonths)));
        // Cap age multiplier at 2.0
        if (ageMultiplier.compareTo(new BigDecimal("2.0")) > 0) {
            ageMultiplier = new BigDecimal("2.0");
        }

        // --- 3. FINAL CALCULATION (Hybrid Approach) ---
        // We take the internal calculation and adjust it by the external limit
        BigDecimal internalLimit = baseAmount.add(balanceBonus).add(volumeBonus).multiply(ageMultiplier);

        // Final limit = Min(InternalLimit, ExternalLimit - CurrentDebt)
        BigDecimal availableExternalLimit = externalCreditLimit.subtract(currentDebtInOtherBanks);
        BigDecimal calculatedLimit = internalLimit.min(availableExternalLimit)
                .setScale(0, RoundingMode.HALF_UP);

        // Cap by Product Max
        if (calculatedLimit.compareTo(product.getMaxAmount()) > 0) {
            calculatedLimit = product.getMaxAmount();
        }

        // Ensure at least Min if they were eligible at all
        if (calculatedLimit.compareTo(BigDecimal.ZERO) > 0 && calculatedLimit.compareTo(product.getMinAmount()) < 0) {
            calculatedLimit = product.getMinAmount();
        }

        return calculatedLimit;
    }
}

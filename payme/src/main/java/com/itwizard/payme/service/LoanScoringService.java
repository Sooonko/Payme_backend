package com.itwizard.payme.service;

import com.itwizard.payme.domain.LoanProduct;
import com.itwizard.payme.domain.User;

import java.math.BigDecimal;

public interface LoanScoringService {
    BigDecimal calculateMaxEligibleAmount(User user, LoanProduct product);
}

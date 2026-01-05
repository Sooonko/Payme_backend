package com.itwizard.payme.service;

import com.itwizard.payme.domain.LoanProduct;
import com.itwizard.payme.dto.LoanEligibilityResponse;
import com.itwizard.payme.dto.request.LoanApplicationRequest;
import com.itwizard.payme.dto.request.LoanProductRequest;
import com.itwizard.payme.dto.response.LoanApplicationResponse;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    LoanEligibilityResponse checkEligibility(UUID userId);

    LoanApplicationResponse applyForLoan(UUID userId, LoanApplicationRequest request);

    LoanProduct createLoanProduct(LoanProductRequest request);

    List<LoanProduct> getAllLoanProducts();
}

package com.itwizard.payme.service;

import com.itwizard.payme.domain.LoanProduct;
import com.itwizard.payme.dto.LoanEligibilityResponse;
import com.itwizard.payme.dto.LoanProductOption;
import com.itwizard.payme.dto.request.LoanApplicationRequest;
import com.itwizard.payme.dto.request.LoanProductRequest;
import com.itwizard.payme.dto.response.LoanApplicationResponse;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    LoanEligibilityResponse checkProductEligibility(UUID userId, UUID productId);

    LoanApplicationResponse applyForLoan(UUID userId, LoanApplicationRequest request);

    LoanProduct createLoanProduct(LoanProductRequest request);

    List<LoanProductOption> getAllLoanProducts(UUID userId);
}

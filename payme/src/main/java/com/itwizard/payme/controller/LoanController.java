package com.itwizard.payme.controller;

import com.itwizard.payme.domain.LoanProduct;
import com.itwizard.payme.dto.LoanEligibilityResponse;
import com.itwizard.payme.dto.LoanProductOption;
import com.itwizard.payme.dto.request.LoanApplicationRequest;
import com.itwizard.payme.dto.request.LoanProductRequest;
import com.itwizard.payme.dto.response.LoanApplicationResponse;
import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.security.CurrentUser;
import com.itwizard.payme.security.UserPrincipal;
import com.itwizard.payme.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/eligibility")
    public ResponseEntity<StandardResponse<LoanEligibilityResponse>> checkEligibility(
            @CurrentUser UserPrincipal currentUser,
            @RequestParam UUID productId) {
        return ResponseEntity
                .ok(StandardResponse.success(loanService.checkProductEligibility(currentUser.getId(), productId)));
    }

    @PostMapping("/apply")
    public ResponseEntity<StandardResponse<LoanApplicationResponse>> applyForLoan(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(StandardResponse.success(
                loanService.applyForLoan(currentUser.getId(), request),
                "Loan application submitted successfully"));
    }

    @PostMapping("/products")
    public ResponseEntity<StandardResponse<LoanProduct>> createLoanProduct(
            @Valid @RequestBody LoanProductRequest request) {
        return ResponseEntity.ok(StandardResponse.success(
                loanService.createLoanProduct(request),
                "Loan product created successfully"));
    }

    @GetMapping("/products")
    public ResponseEntity<StandardResponse<List<LoanProductOption>>> getAllLoanProducts(
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(StandardResponse.success(loanService.getAllLoanProducts(currentUser.getId())));
    }
}

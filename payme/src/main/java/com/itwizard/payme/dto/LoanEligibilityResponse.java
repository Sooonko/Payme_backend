package com.itwizard.payme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEligibilityResponse {
    private boolean eligible;
    private String message;
    private List<LoanProductOption> options;
}

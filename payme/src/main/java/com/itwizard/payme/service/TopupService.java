package com.itwizard.payme.service;

import com.itwizard.payme.dto.request.TopupRequest;
import com.itwizard.payme.dto.response.TopupResponse;

import java.util.List;
import java.util.UUID;

public interface TopupService {
    TopupResponse initiateTopup(UUID userId, TopupRequest request);

    TopupResponse confirmTopup(UUID userId, Long transactionId);

    List<TopupResponse> getTopupHistory(UUID userId);
}

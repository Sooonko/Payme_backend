package com.itwizard.payme.service.impl;

import com.itwizard.payme.dto.response.QPayInvoiceResponse;

import com.itwizard.payme.service.QPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class QPayServiceImpl implements QPayService {

    @Value("${qpay.url}")
    private String qpayUrl;

    @Value("${qpay.username}")
    private String username;

    @Value("${qpay.password}")
    private String password;

    private final RestTemplate restTemplate;

    @Override
    public String getAccessToken() {
        // TODO: Implement QPay token retrieval
        log.info("Getting QPay access token...");
        return "mock_token";
    }

    @Override
    public QPayInvoiceResponse createInvoice(BigDecimal amount, String description) {
        // TODO: Implement QPay invoice creation
        log.info("Creating QPay invoice for amount: {}", amount);
        return QPayInvoiceResponse.builder()
                .invoiceId("MOCK_INVOICE_" + System.currentTimeMillis())
                .qrText("MOCK_QR_TEXT")
                .build();
    }

    @Override
    public boolean checkPayment(String invoiceId) {
        // TODO: Implement QPay payment check
        log.info("Checking QPay payment for invoice: {}", invoiceId);
        return true; // Mock success
    }
}

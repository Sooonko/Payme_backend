package com.itwizard.payme.service;

import com.itwizard.payme.dto.response.QPayInvoiceResponse;

import java.math.BigDecimal;

public interface QPayService {
    String getAccessToken();

    QPayInvoiceResponse createInvoice(BigDecimal amount, String description);

    boolean checkPayment(String invoiceId);
}

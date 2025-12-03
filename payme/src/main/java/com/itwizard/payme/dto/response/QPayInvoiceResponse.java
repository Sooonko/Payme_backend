package com.itwizard.payme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QPayInvoiceResponse {
    private String invoiceId;
    private String qrText;
    private String qrImage;
    private String qPayShortUrl;
}

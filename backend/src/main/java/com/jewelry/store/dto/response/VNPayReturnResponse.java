package com.jewelry.store.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VNPayReturnResponse {

    private String orderCode;
    private String transactionCode;
    private String responseCode;
    private boolean isSuccess;
    private BigDecimal amount;
    private String bankCode;
    private String payDate;
    private String message;
}

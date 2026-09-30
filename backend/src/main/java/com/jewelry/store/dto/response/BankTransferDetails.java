package com.jewelry.store.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankTransferDetails {

    private String bankId;
    private String accountNo;
    private String accountName;
    private BigDecimal amount;
    private String orderCode;
    private String transferContent;
    private String qrCodeUrl;
}

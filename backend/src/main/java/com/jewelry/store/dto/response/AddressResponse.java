package com.jewelry.store.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;
    private String receiverName;
    private String phone;
    private String streetAddress;
    private String ward;
    private String district;
    private String province;
    private String postalCode;
    private boolean isDefault;
    private String fullAddress;
    private LocalDateTime createdAt;
}

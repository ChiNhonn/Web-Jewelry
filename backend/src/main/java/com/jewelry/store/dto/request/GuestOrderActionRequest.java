package com.jewelry.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GuestOrderActionRequest {

    @NotBlank(message = "Thiếu mã xác nhận đơn")
    private String accessToken;

    /** Bắt buộc true khi đổi sang thanh toán khi nhận hàng. */
    private Boolean confirmed;
}

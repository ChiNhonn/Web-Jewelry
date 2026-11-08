package com.jewelry.store.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GuestOrderLineRequest {

    @NotNull(message = "Thiếu mã phiên bản sản phẩm")
    private Long variantId;

    @NotNull(message = "Thiếu số lượng")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer quantity;
}

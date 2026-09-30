package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.ProductStatus;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVariantRequest {

    private String sizeOption;
    private String materialType;

    @PositiveOrZero(message = "Giá gốc phải lớn hơn hoặc bằng 0")
    private BigDecimal originalPrice;

    @PositiveOrZero(message = "Giá bán phải lớn hơn hoặc bằng 0")
    private BigDecimal salePrice;

    private ProductStatus status;
}

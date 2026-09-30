package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductVariantRequest {

    @NotBlank(message = "Mã SKU biến thể không được để trống")
    private String sku;

    private String sizeOption; // Size 6, 7, 8 hoặc 45cm
    private String materialType; // Vàng Trắng 18K, Vàng Hồng 18K, Platinum

    @NotNull(message = "Giá gốc không được để trống")
    @PositiveOrZero(message = "Giá gốc phải lớn hơn hoặc bằng 0")
    private BigDecimal originalPrice;

    @NotNull(message = "Giá bán không được để trống")
    @PositiveOrZero(message = "Giá bán phải lớn hơn hoặc bằng 0")
    private BigDecimal salePrice;

    @NotNull(message = "Số lượng nhập kho ban đầu không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho ban đầu không được âm")
    @Builder.Default
    private Integer initialStock = 0;

    @Min(value = 0, message = "Ngưỡng cảnh báo tồn kho không được âm")
    @Builder.Default
    private Integer lowStockThreshold = 5;

    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;
}

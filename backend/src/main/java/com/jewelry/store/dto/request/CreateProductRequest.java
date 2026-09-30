package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

    @NotNull(message = "Danh mục sản phẩm không được để trống")
    private Long categoryId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200, message = "Tên sản phẩm không được vượt quá 200 ký tự")
    private String name;

    @NotBlank(message = "Mã SKU gốc không được để trống")
    @Size(max = 50, message = "Mã SKU gốc không được vượt quá 50 ký tự")
    private String baseSku;

    private String description;
    private String shortDescription;
    private String materialOverview;

    @Builder.Default
    private String brand = "AURA JEWELRY";

    @Builder.Default
    private boolean isFeatured = false;

    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @NotEmpty(message = "Sản phẩm phải có ít nhất một biến thể (size, chất liệu, giá)")
    @Valid
    @Builder.Default
    private List<CreateProductVariantRequest> variants = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<CreateProductImageRequest> images = new ArrayList<>();
}

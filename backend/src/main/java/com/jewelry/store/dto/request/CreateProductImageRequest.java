package com.jewelry.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductImageRequest {

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    private String imageUrl;

    private String publicId;

    @Builder.Default
    private boolean isThumbnail = false;

    @Builder.Default
    private int sortOrder = 0;
}

package com.jewelry.store.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateThresholdRequest {

    @Min(value = 0, message = "Ngưỡng cảnh báo tồn kho không được âm")
    private int lowStockThreshold;
}

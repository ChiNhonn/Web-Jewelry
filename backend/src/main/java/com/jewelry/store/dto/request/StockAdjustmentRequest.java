package com.jewelry.store.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentRequest {

    public enum AdjustmentType {
        ADD,    // Nhập thêm hàng vào kho (Restock)
        DEDUCT, // Xuất hàng / Hủy hàng hao hụt
        SET     // Điều chỉnh số lượng thực tế sau kiểm kê (Inventory Audit)
    }

    @NotNull(message = "Variant ID không được để trống")
    private Long variantId;

    @NotNull(message = "Loại điều chỉnh (ADD, DEDUCT, SET) không được để trống")
    private AdjustmentType adjustmentType;

    @Min(value = 0, message = "Số lượng điều chỉnh không thể âm")
    private int quantity;

    private String reason; // Lý do điều chỉnh (VD: "Nhập xưởng hoàn thiện", "Hàng mẫu trưng bày bị trầy xước",...)
}

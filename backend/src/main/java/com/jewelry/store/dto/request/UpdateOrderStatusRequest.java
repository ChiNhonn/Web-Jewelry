package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private OrderStatus newStatus;

    @Size(max = 500, message = "Ghi chú cập nhật tối đa 500 ký tự")
    private String note;
}

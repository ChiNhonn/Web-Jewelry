package com.jewelry.store.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelOrderRequest {

    @Size(max = 500, message = "Lý do hủy đơn tối đa 500 ký tự")
    private String cancellationReason;
}

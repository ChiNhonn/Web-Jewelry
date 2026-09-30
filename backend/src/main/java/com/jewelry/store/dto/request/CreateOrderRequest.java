package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.PaymentMethodType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    private Long shippingAddressId;

    @Valid
    private ShippingAddressDto shippingAddress;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethodType paymentMethod;

    @Size(max = 50, message = "Mã giảm giá tối đa 50 ký tự")
    private String couponCode;

    @Size(max = 500, message = "Ghi chú đơn hàng tối đa 500 ký tự")
    private String customerNote;
}

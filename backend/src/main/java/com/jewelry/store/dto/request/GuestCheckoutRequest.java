package com.jewelry.store.dto.request;

import com.jewelry.store.entity.enums.PaymentMethodType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class GuestCheckoutRequest {

    @Valid
    @NotNull(message = "Vui lòng nhập địa chỉ giao hàng")
    private ShippingAddressDto shippingAddress;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    private PaymentMethodType paymentMethod;

    @Size(max = 50)
    private String couponCode;

    @Size(max = 500)
    private String customerNote;

    @NotEmpty(message = "Giỏ hàng đang trống")
    @Valid
    private List<GuestOrderLineRequest> items;
}

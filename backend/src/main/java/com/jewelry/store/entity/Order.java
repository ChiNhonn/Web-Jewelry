package com.jewelry.store.entity;

import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.PaymentMethodType;
import com.jewelry.store.entity.enums.PaymentStatus;
import com.jewelry.store.util.StoreText;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal merchandiseTotal;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethodText;

    @Column(name = "payment_status", nullable = false, length = 50)
    private String paymentStatusText;

    @Column(name = "order_status", nullable = false, length = 50)
    private String orderStatusText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "coupon_code", nullable = false, length = 50)
    @Builder.Default
    private String couponCode = "";

    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal finalAmount = BigDecimal.ZERO;

    @Column(name = "promotion_discount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal promotionDiscount = BigDecimal.ZERO;

    @Column(name = "coupon_discount", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal couponDiscount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderDetail> details = new ArrayList<>();

    public String getOrderCode() {
        return id == null ? null : String.valueOf(id);
    }

    public String getGuestAccessToken() {
        return getOrderCode();
    }

    public BigDecimal getTotalAmount() {
        return finalAmount == null ? merchandiseTotal : finalAmount;
    }

    public OrderStatus getOrderStatus() {
        return StoreText.orderStatus(orderStatusText);
    }

    public void setOrderStatus(OrderStatus status) {
        this.orderStatusText = StoreText.orderStatusText(status);
    }

    public PaymentStatus getPaymentStatus() {
        return StoreText.paymentStatus(paymentStatusText);
    }

    public void setPaymentStatus(PaymentStatus status) {
        this.paymentStatusText = StoreText.paymentStatusText(status);
    }

    public PaymentMethodType getPaymentMethod() {
        return StoreText.paymentMethod(paymentMethodText);
    }

    public void setPaymentMethod(PaymentMethodType method) {
        this.paymentMethodText = StoreText.paymentMethodText(method);
    }
}

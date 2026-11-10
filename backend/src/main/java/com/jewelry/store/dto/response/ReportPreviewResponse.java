package com.jewelry.store.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReportPreviewResponse {
    private String type;
    private String from;
    private String to;
    private long orderCount;
    private BigDecimal totalAmount;
    private List<OrderRow> orders;
    private List<ProductRow> products;

    @Getter
    @Builder
    public static class OrderRow {
        private String orderCode;
        private String customerName;
        private String phone;
        private String orderStatus;
        private String paymentMethod;
        private String paymentStatus;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class ProductRow {
        private String productName;
        private int quantity;
        private BigDecimal revenue;
    }
}

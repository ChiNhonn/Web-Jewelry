package com.jewelry.store.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySalesResponse {

    private Long categoryId;
    private String categoryName;
    private BigDecimal totalRevenue;
    private long orderCount;
    private double percentage; // Tỷ trọng % trên tổng doanh thu
}

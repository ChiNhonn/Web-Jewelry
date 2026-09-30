package com.jewelry.store.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueChartDataPoint {

    private String date; // YYYY-MM-DD
    private BigDecimal revenue;
    private long orderCount;
}

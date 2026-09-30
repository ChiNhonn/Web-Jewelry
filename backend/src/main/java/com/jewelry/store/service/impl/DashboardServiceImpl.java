package com.jewelry.store.service.impl;

import com.jewelry.store.dto.response.*;
import com.jewelry.store.entity.Inventory;
import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.ProductImage;
import com.jewelry.store.entity.ProductVariant;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.mapper.OrderMapper;
import com.jewelry.store.repository.*;
import com.jewelry.store.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;

    private static final List<OrderStatus> REVENUE_STATUSES = List.of(
            OrderStatus.CONFIRMED,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPING,
            OrderStatus.DELIVERED,
            OrderStatus.COMPLETED
    );

    @Override
    @Transactional(readOnly = true)
    public DashboardOverviewResponse getOverview() {
        log.info("Calculating executive dashboard overview metrics");

        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();

        YearMonth currentYearMonth = YearMonth.from(today);
        LocalDateTime currentMonthStart = currentYearMonth.atDay(1).atStartOfDay();
        LocalDateTime nextMonthStart = currentYearMonth.plusMonths(1).atDay(1).atStartOfDay();

        YearMonth previousYearMonth = currentYearMonth.minusMonths(1);
        LocalDateTime previousMonthStart = previousYearMonth.atDay(1).atStartOfDay();

        // 1. Doanh thu
        BigDecimal totalRevenue = orderRepository.sumTotalRevenueByStatuses(REVENUE_STATUSES);
        BigDecimal todayRevenue = orderRepository.sumRevenueBetweenDates(REVENUE_STATUSES, todayStart, tomorrowStart);
        BigDecimal monthRevenue = orderRepository.sumRevenueBetweenDates(REVENUE_STATUSES, currentMonthStart, nextMonthStart);
        BigDecimal previousMonthRevenue = orderRepository.sumRevenueBetweenDates(REVENUE_STATUSES, previousMonthStart, currentMonthStart);

        double growthPercent = 0.0;
        if (previousMonthRevenue != null && previousMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diff = monthRevenue.subtract(previousMonthRevenue);
            growthPercent = diff.multiply(BigDecimal.valueOf(100))
                    .divide(previousMonthRevenue, 1, RoundingMode.HALF_UP)
                    .doubleValue();
        } else if (monthRevenue != null && monthRevenue.compareTo(BigDecimal.ZERO) > 0) {
            growthPercent = 100.0;
        }

        // 2. Đơn hàng
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByOrderStatus(OrderStatus.PENDING);
        long shippingOrders = orderRepository.countByOrderStatus(OrderStatus.SHIPPING);
        long completedOrders = orderRepository.countByOrderStatus(OrderStatus.DELIVERED)
                + orderRepository.countByOrderStatus(OrderStatus.COMPLETED);
        long cancelledOrders = orderRepository.countByOrderStatus(OrderStatus.CANCELLED);

        // 3. Khách hàng & Kho hàng
        long totalCustomers = userRepository.count();
        long newCustomersThisMonth = userRepository.countByCreatedAtGreaterThanEqual(currentMonthStart);
        long totalProducts = productRepository.count();
        long outOfStockCount = inventoryRepository.countOutOfStock();
        long lowStockCount = inventoryRepository.countLowStock();

        return DashboardOverviewResponse.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .monthRevenue(monthRevenue != null ? monthRevenue : BigDecimal.ZERO)
                .revenueGrowthPercent(growthPercent)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .shippingOrders(shippingOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .totalCustomers(totalCustomers)
                .newCustomersThisMonth(newCustomersThisMonth)
                .totalProducts(totalProducts)
                .outOfStockCount(outOfStockCount)
                .lowStockCount(lowStockCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueChartDataPoint> getRevenueAnalytics(int days) {
        if (days <= 0) days = 7;
        if (days > 90) days = 90;

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> rawData = orderRepository.getDailyRevenueAnalytics(REVENUE_STATUSES, startDateTime, endDateTime);

        // Đưa dữ liệu thô vào Map để map liên tục không bị khuyết ngày 0 đơn
        Map<String, RevenueChartDataPoint> dataMap = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (rawData != null) {
            for (Object[] row : rawData) {
                if (row[0] != null) {
                    String dateStr = row[0].toString();
                    BigDecimal rev = (BigDecimal) row[1];
                    Long cnt = (Long) row[2];
                    dataMap.put(dateStr, RevenueChartDataPoint.builder()
                            .date(dateStr)
                            .revenue(rev != null ? rev : BigDecimal.ZERO)
                            .orderCount(cnt != null ? cnt : 0L)
                            .build());
                }
            }
        }

        List<RevenueChartDataPoint> chartPoints = new ArrayList<>();
        LocalDate cur = startDate;
        while (!cur.isAfter(endDate)) {
            String dateKey = cur.format(dtf);
            RevenueChartDataPoint point = dataMap.getOrDefault(dateKey, RevenueChartDataPoint.builder()
                    .date(dateKey)
                    .revenue(BigDecimal.ZERO)
                    .orderCount(0L)
                    .build());
            chartPoints.add(point);
            cur = cur.plusDays(1);
        }

        return chartPoints;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopSellingProductResponse> getTopSellingProducts(int limit) {
        if (limit <= 0) limit = 5;
        if (limit > 20) limit = 20;

        List<Object[]> rawList = orderRepository.getTopSellingProducts(REVENUE_STATUSES, PageRequest.of(0, limit));
        List<TopSellingProductResponse> result = new ArrayList<>();

        if (rawList != null) {
            for (Object[] row : rawList) {
                Long productId = (Long) row[0];
                String productName = (String) row[1];
                String sku = (String) row[2];
                Long unitsSold = row[3] instanceof Number ? ((Number) row[3]).longValue() : 0L;
                BigDecimal revenue = row[4] instanceof BigDecimal ? (BigDecimal) row[4] : BigDecimal.ZERO;

                String thumbnailUrl = null;
                Optional<com.jewelry.store.entity.Product> productOpt = productRepository.findById(productId);
                if (productOpt.isPresent() && productOpt.get().getImages() != null && !productOpt.get().getImages().isEmpty()) {
                    thumbnailUrl = productOpt.get().getImages().stream()
                            .filter(ProductImage::isThumbnail)
                            .findFirst()
                            .map(ProductImage::getImageUrl)
                            .orElse(productOpt.get().getImages().get(0).getImageUrl());
                }

                result.add(TopSellingProductResponse.builder()
                        .productId(productId)
                        .productName(productName)
                        .sku(sku)
                        .unitsSold(unitsSold)
                        .revenue(revenue)
                        .thumbnailUrl(thumbnailUrl)
                        .build());
            }
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySalesResponse> getCategorySales() {
        List<Object[]> rawList = orderRepository.getCategorySalesBreakdown(REVENUE_STATUSES);
        List<CategorySalesResponse> result = new ArrayList<>();

        BigDecimal totalSales = BigDecimal.ZERO;
        if (rawList != null) {
            for (Object[] row : rawList) {
                BigDecimal rev = row[2] instanceof BigDecimal ? (BigDecimal) row[2] : BigDecimal.ZERO;
                totalSales = totalSales.add(rev);
            }
        }

        if (rawList != null) {
            for (Object[] row : rawList) {
                Long catId = (Long) row[0];
                String catName = (String) row[1];
                BigDecimal rev = row[2] instanceof BigDecimal ? (BigDecimal) row[2] : BigDecimal.ZERO;
                Long orderCount = row[3] instanceof Number ? ((Number) row[3]).longValue() : 0L;

                double percentage = 0.0;
                if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
                    percentage = rev.multiply(BigDecimal.valueOf(100))
                            .divide(totalSales, 1, RoundingMode.HALF_UP)
                            .doubleValue();
                }

                result.add(CategorySalesResponse.builder()
                        .categoryId(catId)
                        .categoryName(catName)
                        .totalRevenue(rev)
                        .orderCount(orderCount)
                        .percentage(percentage)
                        .build());
            }
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getRecentOrders(int limit) {
        if (limit <= 0) limit = 10;
        if (limit > 50) limit = 50;

        List<Order> orders = orderRepository.findRecentOrders(PageRequest.of(0, limit));
        return orders.stream()
                .map(orderMapper::toOrderSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockAlertResponse> getLowStockAlerts(int limit) {
        if (limit <= 0) limit = 10;
        if (limit > 50) limit = 50;

        List<Inventory> inventories = inventoryRepository.findLowStockInventories(PageRequest.of(0, limit));
        List<LowStockAlertResponse> result = new ArrayList<>();

        for (Inventory inv : inventories) {
            ProductVariant variant = inv.getVariant();
            String productName = "";
            Long productId = null;

            if (variant != null && variant.getProduct() != null) {
                productName = variant.getProduct().getName();
                productId = variant.getProduct().getId();
            }

            result.add(LowStockAlertResponse.builder()
                    .variantId(variant != null ? variant.getId() : null)
                    .productId(productId)
                    .productName(productName)
                    .sku(variant != null ? variant.getSku() : "")
                    .sizeOption(variant != null ? variant.getSizeOption() : "")
                    .materialType(variant != null ? variant.getMaterialType() : "")
                    .quantity(inv.getQuantity())
                    .reservedQuantity(inv.getReservedQuantity())
                    .availableQuantity(inv.getAvailableQuantity())
                    .lowStockThreshold(inv.getLowStockThreshold())
                    .build());
        }

        return result;
    }
}

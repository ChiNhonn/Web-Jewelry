package com.jewelry.store.service.impl;

import com.jewelry.store.dto.response.*;
import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.OrderDetail;
import com.jewelry.store.entity.Product;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.repository.OrderDetailRepository;
import com.jewelry.store.repository.OrderRepository;
import com.jewelry.store.repository.ProductRepository;
import com.jewelry.store.repository.UserRepository;
import com.jewelry.store.service.DashboardService;
import com.jewelry.store.util.StoreText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardOverviewResponse getOverview() {
        List<Order> orders = orderRepository.findAll();
        List<Product> products = productRepository.findAll();
        BigDecimal revenue = orders.stream().filter(this::delivered).map(Order::getFinalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDate today = LocalDate.now();
        BigDecimal todayRevenue = orders.stream()
                .filter(this::delivered)
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().toLocalDate().equals(today))
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal monthRevenue = orders.stream()
                .filter(this::delivered)
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().getMonth() == today.getMonth() && order.getCreatedAt().getYear() == today.getYear())
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long customers = userRepository.findAll().stream().filter(user -> !user.isGuest()).count();
        long low = products.stream().filter(product -> product.getQuantity() != null && product.getQuantity() > 0 && product.getQuantity() <= 5).count();
        long out = products.stream().filter(product -> product.getQuantity() == null || product.getQuantity() <= 0).count();
        return DashboardOverviewResponse.builder()
                .totalRevenue(revenue)
                .todayRevenue(todayRevenue)
                .monthRevenue(monthRevenue)
                .revenueGrowthPercent(0)
                .totalOrders(orders.size())
                .pendingOrders(orders.stream().filter(order -> order.getOrderStatus() == OrderStatus.PENDING).count())
                .shippingOrders(orders.stream().filter(order -> order.getOrderStatus() == OrderStatus.SHIPPING).count())
                .completedOrders(orders.stream().filter(this::delivered).count())
                .cancelledOrders(orders.stream().filter(order -> order.getOrderStatus() == OrderStatus.CANCELLED).count())
                .totalCustomers(customers)
                .newCustomersThisMonth(customers)
                .totalProducts(products.size())
                .outOfStockCount(out)
                .lowStockCount(low)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevenueChartDataPoint> getRevenueAnalytics(int days) {
        int span = days <= 0 ? 7 : days;
        Map<LocalDate, RevenueChartDataPoint> points = new LinkedHashMap<>();
        LocalDate start = LocalDate.now().minusDays(span - 1L);
        for (int i = 0; i < span; i++) {
            LocalDate date = start.plusDays(i);
            points.put(date, RevenueChartDataPoint.builder().date(date.toString()).revenue(BigDecimal.ZERO).orderCount(0).build());
        }
        for (Order order : orderRepository.findAll()) {
            if (!delivered(order) || order.getCreatedAt() == null) {
                continue;
            }
            RevenueChartDataPoint point = points.get(order.getCreatedAt().toLocalDate());
            if (point == null) {
                continue;
            }
            point.setRevenue(point.getRevenue().add(order.getFinalAmount()));
            point.setOrderCount(point.getOrderCount() + 1);
        }
        return new ArrayList<>(points.values());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopSellingProductResponse> getTopSellingProducts(int limit) {
        Map<Long, long[]> sold = new LinkedHashMap<>();
        Map<Long, BigDecimal> revenue = new LinkedHashMap<>();
        for (OrderDetail detail : orderDetailRepository.findAll()) {
            if (detail.getProductId() == null || detail.getOrder() == null || !delivered(detail.getOrder())) {
                continue;
            }
            sold.computeIfAbsent(detail.getProductId(), key -> new long[1])[0] += detail.getQuantity();
            revenue.merge(detail.getProductId(), detail.getSubtotal(), BigDecimal::add);
        }
        return sold.entrySet().stream()
                .sorted((left, right) -> Long.compare(right.getValue()[0], left.getValue()[0]))
                .limit(limit <= 0 ? 5 : limit)
                .map(entry -> {
                    Product product = productRepository.findById(entry.getKey()).orElse(null);
                    return TopSellingProductResponse.builder()
                            .productId(entry.getKey())
                            .productName(product == null ? "Sản phẩm" : product.getName())
                            .sku("SP-" + entry.getKey())
                            .unitsSold(entry.getValue()[0])
                            .revenue(revenue.getOrDefault(entry.getKey(), BigDecimal.ZERO))
                            .thumbnailUrl(product == null ? null : StoreText.imageUrl(product.getImageName()))
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySalesResponse> getCategorySales() {
        Map<Long, BigDecimal> revenue = new LinkedHashMap<>();
        Map<Long, Long> counts = new LinkedHashMap<>();
        Map<Long, String> names = new LinkedHashMap<>();
        for (OrderDetail detail : orderDetailRepository.findAll()) {
            if (detail.getProductId() == null || detail.getOrder() == null || !delivered(detail.getOrder())) {
                continue;
            }
            productRepository.findById(detail.getProductId()).ifPresent(product -> {
                Long categoryId = product.getCategory().getId();
                names.put(categoryId, product.getCategory().getName());
                revenue.merge(categoryId, detail.getSubtotal(), BigDecimal::add);
                counts.merge(categoryId, 1L, Long::sum);
            });
        }
        BigDecimal total = revenue.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return revenue.entrySet().stream().map(entry -> CategorySalesResponse.builder()
                .categoryId(entry.getKey())
                .categoryName(names.get(entry.getKey()))
                .totalRevenue(entry.getValue())
                .orderCount(counts.getOrDefault(entry.getKey(), 0L))
                .percentage(total.signum() == 0 ? 0 : entry.getValue().multiply(BigDecimal.valueOf(100)).divide(total, 1, java.math.RoundingMode.HALF_UP).doubleValue())
                .build()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getRecentOrders(int limit) {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit <= 0 ? 5 : limit)
                .map(order -> OrderSummaryResponse.builder()
                        .id(order.getId())
                        .orderCode(order.getOrderCode())
                        .userId(order.getUser().getId())
                        .customerName(order.getReceiverName())
                        .customerPhone(order.getPhone())
                        .orderStatus(order.getOrderStatus())
                        .paymentStatus(order.getPaymentStatus())
                        .paymentMethod(order.getPaymentMethod())
                        .totalAmount(order.getFinalAmount())
                        .createdAt(order.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LowStockAlertResponse> getLowStockAlerts(int limit) {
        return productRepository.findAll().stream()
                .filter(product -> product.getQuantity() != null && product.getQuantity() <= 5)
                .limit(limit <= 0 ? 8 : limit)
                .map(product -> LowStockAlertResponse.builder()
                        .variantId(product.getId())
                        .productId(product.getId())
                        .productName(product.getName())
                        .sku("SP-" + product.getId())
                        .sizeOption(product.getSize())
                        .materialType(product.getMaterial())
                        .quantity(product.getQuantity())
                        .reservedQuantity(0)
                        .availableQuantity(product.getQuantity())
                        .lowStockThreshold(5)
                        .build())
                .toList();
    }

    private boolean delivered(Order order) {
        return order.getOrderStatus() == OrderStatus.DELIVERED;
    }
}

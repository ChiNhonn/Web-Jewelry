package com.jewelry.store.service;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CancelOrderRequest;
import com.jewelry.store.dto.request.CreateOrderRequest;
import com.jewelry.store.dto.request.UpdateOrderStatusRequest;
import com.jewelry.store.dto.response.OrderResponse;
import com.jewelry.store.dto.response.OrderSummaryResponse;
import com.jewelry.store.entity.enums.OrderStatus;

public interface OrderService {

    OrderResponse createOrder(Long userId, CreateOrderRequest request);

    PageResponse<OrderSummaryResponse> getUserOrders(Long userId, OrderStatus status, int page, int size);

    OrderResponse getOrderByCode(Long userId, String orderCode, boolean isAdmin);

    OrderResponse cancelOrder(Long userId, String orderCode, CancelOrderRequest request, boolean isAdmin);

    PageResponse<OrderSummaryResponse> getAllOrdersForAdmin(OrderStatus status, String keyword, int page, int size);

    OrderResponse updateOrderStatusByAdmin(String orderCode, UpdateOrderStatusRequest request);
}

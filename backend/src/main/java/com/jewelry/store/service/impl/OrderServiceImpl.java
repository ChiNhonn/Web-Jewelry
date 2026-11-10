package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.*;
import com.jewelry.store.dto.response.OrderItemResponse;
import com.jewelry.store.dto.response.OrderResponse;
import com.jewelry.store.dto.response.OrderSummaryResponse;
import com.jewelry.store.entity.*;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.PaymentMethodType;
import com.jewelry.store.entity.enums.PaymentStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.*;
import com.jewelry.store.service.CouponService;
import com.jewelry.store.service.OrderService;
import com.jewelry.store.service.PriceBook;
import com.jewelry.store.util.StoreText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final CustomerNotificationRepository notificationRepository;
    private final PriceBook priceBook;
    private final CouponService couponService;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tài khoản"));
        List<CartItem> cart = cartItemRepository.findByUserId(userId);
        if (cart.isEmpty()) {
            throw new BadRequestException("Giỏ hàng đang trống");
        }
        List<Line> lines = cart.stream()
                .map(item -> new Line(item.getProduct().getId(), item.getQuantity()))
                .toList();
        ShippingAddressDto address = request.getShippingAddress();
        if (address == null) {
            throw new BadRequestException("Vui lòng nhập địa chỉ giao hàng");
        }
        Order order = place(user, address.getReceiverName(), address.getPhone(), joinAddress(address), request.getPaymentMethod(), request.getCouponCode(), lines);
        cartItemRepository.deleteByUserId(userId);
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse createGuestOrder(GuestCheckoutRequest request) {
        User guest = userRepository.findByUsername("__guest__")
                .orElseThrow(() -> new BadRequestException("Chưa có tài khoản khách vãng lai"));
        List<Line> lines = request.getItems().stream()
                .map(item -> new Line(item.getVariantId(), item.getQuantity()))
                .toList();
        ShippingAddressDto address = request.getShippingAddress();
        Order order = place(guest, address.getReceiverName(), address.getPhone(), joinAddress(address), request.getPaymentMethod(), request.getCouponCode(), lines);
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse abandonGuestOrder(String orderCode, GuestOrderActionRequest request) {
        Order order = requireGuestOrder(orderCode, request.getAccessToken());
        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Đơn đã thanh toán, không thể hủy lúc này");
        }
        OrderResponse snapshot = toResponse(order);
        for (OrderDetail detail : orderDetailRepository.findByOrderId(order.getId())) {
            if (detail.getProductId() != null) {
                productRepository.findById(detail.getProductId()).ifPresent(product -> {
                    product.setQuantity(product.getQuantity() + detail.getQuantity());
                    productRepository.save(product);
                });
            }
        }
        rollbackCoupon(order);
        orderDetailRepository.deleteAll(orderDetailRepository.findByOrderId(order.getId()));
        orderRepository.delete(order);
        return snapshot;
    }

    @Override
    @Transactional
    public OrderResponse switchGuestOrderToCod(String orderCode, GuestOrderActionRequest request) {
        if (!Boolean.TRUE.equals(request.getConfirmed())) {
            throw new BadRequestException("Cần xác nhận trước khi đổi sang thanh toán khi nhận hàng");
        }
        Order order = requireGuestOrder(orderCode, request.getAccessToken());
        order.setPaymentMethod(PaymentMethodType.COD);
        order.setPaymentStatus(PaymentStatus.PENDING);
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getUserOrders(Long userId, OrderStatus status, int page, int size) {
        List<OrderSummaryResponse> all = orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(order -> status == null || order.getOrderStatus() == status)
                .map(this::toSummary)
                .toList();
        return pageOf(all, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(Long userId, String orderCode, boolean isAdmin) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        if (!isAdmin && (userId == null || !order.getUser().getId().equals(userId))) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng");
        }
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, String orderCode, CancelOrderRequest request, boolean isAdmin) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Không có quyền hủy đơn này");
        }
        if (!isAdmin && order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Chỉ hủy được đơn đang chờ xác nhận");
        }
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            return toResponse(order);
        }
        restoreStock(order);
        rollbackCoupon(order);
        order.setOrderStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getAllOrdersForAdmin(OrderStatus status, String keyword, int page, int size) {
        String needle = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<OrderSummaryResponse> all = orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .filter(order -> status == null || order.getOrderStatus() == status)
                .filter(order -> needle.isEmpty()
                        || order.getReceiverName().toLowerCase(Locale.ROOT).contains(needle)
                        || order.getOrderCode().contains(needle)
                        || (order.getPhone() != null && order.getPhone().contains(needle)))
                .map(this::toSummary)
                .toList();
        return pageOf(all, page, size);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatusByAdmin(String orderCode, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        OrderStatus previous = order.getOrderStatus();
        if (previous == OrderStatus.CANCELLED) {
            throw new BadRequestException("Đơn đã hủy không thể đổi trạng thái.");
        }
        order.setOrderStatus(request.getNewStatus());
        if (request.getNewStatus() == OrderStatus.DELIVERED || request.getNewStatus() == OrderStatus.COMPLETED) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
        }
        orderRepository.save(order);
        if (previous != OrderStatus.DELIVERED && (request.getNewStatus() == OrderStatus.DELIVERED || request.getNewStatus() == OrderStatus.COMPLETED)
                && !order.getUser().isGuest()) {
            notificationRepository.save(CustomerNotification.builder()
                    .user(order.getUser())
                    .title("Đơn hàng #" + order.getId() + " đã được giao thành công!")
                    .message("Đơn hàng đã giao. Bạn có thể đánh giá sản phẩm.")
                    .orderId(order.getId())
                    .read(false)
                    .type("ORDER_DELIVERED")
                    .actionType("REVIEW_ORDER")
                    .createdAt(LocalDateTime.now())
                    .build());
        }
        return toResponse(order);
    }

    private Order place(User user, String receiver, String phone, String address, PaymentMethodType method, String couponCode, List<Line> lines) {
        if (lines.isEmpty()) {
            throw new BadRequestException("Giỏ hàng đang trống");
        }
        List<OrderDetail> details = new ArrayList<>();
        BigDecimal merchandise = BigDecimal.ZERO;
        BigDecimal payable = BigDecimal.ZERO;
        BigDecimal promotionDiscount = BigDecimal.ZERO;
        boolean promoted = false;
        for (Line line : lines) {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new BadRequestException("Không tìm thấy sản phẩm trong giỏ"));
            if (product.getQuantity() < line.quantity()) {
                throw new BadRequestException("\"" + product.getName() + "\" không đủ hàng");
            }
            PriceBook.Quote quote = priceBook.quote(product);
            BigDecimal lineSale = quote.sale().multiply(BigDecimal.valueOf(line.quantity()));
            BigDecimal lineOriginal = quote.original().multiply(BigDecimal.valueOf(line.quantity()));
            merchandise = merchandise.add(lineOriginal);
            payable = payable.add(lineSale);
            promotionDiscount = promotionDiscount.add(lineOriginal.subtract(lineSale));
            promoted = promoted || quote.onPromotion();
            details.add(OrderDetail.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(line.quantity())
                    .unitPrice(quote.sale())
                    .originalUnitPrice(quote.original())
                    .subtotal(lineSale)
                    .promotionDiscount(lineOriginal.subtract(lineSale))
                    .promotionName(quote.promotionName())
                    .build());
            product.setQuantity(product.getQuantity() - line.quantity());
            productRepository.save(product);
        }
        BigDecimal couponDiscount = BigDecimal.ZERO;
        String savedCode = "";
        if (couponCode != null && !couponCode.isBlank()) {
            if (promoted) {
                throw new BadRequestException("Mã giảm giá không dùng cùng sản phẩm đang khuyến mãi");
            }
            var coupon = couponService.validateCoupon(couponCode, payable);
            if (!coupon.isValid()) {
                throw new BadRequestException(coupon.getMessage());
            }
            couponDiscount = coupon.getCalculatedDiscount();
            savedCode = coupon.getCode();
            couponRepository.findByCodeIgnoreCase(savedCode).ifPresent(entity -> {
                entity.setUsedCount(entity.getUsedCount() + 1);
                couponRepository.save(entity);
            });
        }
        BigDecimal finalAmount = payable.subtract(couponDiscount).max(BigDecimal.ZERO);
        Order order = Order.builder()
                .user(user)
                .receiverName(receiver)
                .phone(phone)
                .shippingAddress(address)
                .merchandiseTotal(merchandise)
                .discountAmount(promotionDiscount.add(couponDiscount))
                .couponCode(savedCode)
                .finalAmount(finalAmount)
                .promotionDiscount(promotionDiscount)
                .couponDiscount(couponDiscount)
                .createdAt(LocalDateTime.now())
                .build();
        if (method == PaymentMethodType.BANK_TRANSFER) {
            throw new BadRequestException("Cửa hàng không nhận chuyển khoản VietQR.");
        }
        order.setPaymentMethod(method == null ? PaymentMethodType.COD : method);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);
        for (OrderDetail detail : details) {
            detail.setOrder(order);
        }
        orderDetailRepository.saveAll(details);
        order.setDetails(details);
        return order;
    }

    private Order requireGuestOrder(String orderCode, String accessToken) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        if (!order.getUser().isGuest()) {
            throw new BadRequestException("Đơn này không phải đơn khách");
        }
        if (accessToken != null && !accessToken.isBlank() && !accessToken.equals(order.getOrderCode())) {
            throw new BadRequestException("Mã xác nhận đơn không đúng");
        }
        return order;
    }

    private void restoreStock(Order order) {
        for (OrderDetail detail : orderDetailRepository.findByOrderId(order.getId())) {
            if (detail.getProductId() == null) {
                continue;
            }
            productRepository.findById(detail.getProductId()).ifPresent(product -> {
                product.setQuantity(product.getQuantity() + detail.getQuantity());
                productRepository.save(product);
            });
        }
    }

    private void rollbackCoupon(Order order) {
        if (order.getCouponCode() == null || order.getCouponCode().isBlank()) {
            return;
        }
        couponRepository.findByCodeIgnoreCase(order.getCouponCode()).ifPresent(coupon -> {
            coupon.setUsedCount(Math.max(0, coupon.getUsedCount() - 1));
            couponRepository.save(coupon);
        });
    }

    private OrderResponse toResponse(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        List<OrderItemResponse> items = details.stream().map(detail -> OrderItemResponse.builder()
                .id(detail.getId())
                .variantId(detail.getProductId())
                .productId(detail.getProductId())
                .productName(detail.getProductName())
                .productSlug(detail.getProductId() == null ? null : StoreText.productSlug(detail.getProductId()))
                .sku(detail.getProductId() == null ? null : "SP-" + detail.getProductId())
                .unitPrice(detail.getUnitPrice())
                .quantity(detail.getQuantity())
                .subtotal(detail.getSubtotal())
                .thumbnailUrl(detail.getProductId() == null ? null : productRepository.findById(detail.getProductId()).map(product -> StoreText.imageUrl(product.getImageName())).orElse(null))
                .build()).toList();
        int quantity = details.stream().mapToInt(OrderDetail::getQuantity).sum();
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .customerName(order.getReceiverName())
                .customerEmail(order.getUser().getEmail())
                .customerPhone(order.getPhone())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .subtotal(order.getMerchandiseTotal())
                .discountAmount(order.getDiscountAmount())
                .shippingFee(BigDecimal.ZERO)
                .totalAmount(order.getFinalAmount())
                .couponCode(order.getCouponCode())
                .shippingAddressSnapshot(order.getShippingAddress())
                .guestAccessToken(order.getGuestAccessToken())
                .items(items)
                .totalQuantity(quantity)
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderSummaryResponse toSummary(Order order) {
        List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
        OrderDetail first = details.isEmpty() ? null : details.get(0);
        return OrderSummaryResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .customerName(order.getReceiverName())
                .customerPhone(order.getPhone())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .totalAmount(order.getFinalAmount())
                .itemCount(details.stream().mapToInt(OrderDetail::getQuantity).sum())
                .firstItemName(first == null ? null : first.getProductName())
                .firstItemThumbnail(first == null || first.getProductId() == null ? null : productRepository.findById(first.getProductId()).map(product -> StoreText.imageUrl(product.getImageName())).orElse(null))
                .createdAt(order.getCreatedAt())
                .build();
    }

    private <T> PageResponse<T> pageOf(List<T> all, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : size;
        int from = Math.min(safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        int pages = (int) Math.ceil(all.size() / (double) safeSize);
        return PageResponse.<T>builder()
                .content(all.subList(from, to))
                .page(safePage)
                .size(safeSize)
                .totalElements(all.size())
                .totalPages(Math.max(pages, 1))
                .first(safePage == 0)
                .last(safePage >= pages - 1)
                .build();
    }

    private String joinAddress(ShippingAddressDto address) {
        return address.getStreetAddress() + ", " + address.getWard() + ", " + address.getDistrict() + ", " + address.getProvince();
    }

    private record Line(Long productId, int quantity) {
    }
}

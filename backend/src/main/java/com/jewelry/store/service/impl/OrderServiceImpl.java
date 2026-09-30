package com.jewelry.store.service.impl;

import com.jewelry.store.dto.common.PageResponse;
import com.jewelry.store.dto.request.CancelOrderRequest;
import com.jewelry.store.dto.request.CreateOrderRequest;
import com.jewelry.store.dto.request.UpdateOrderStatusRequest;
import com.jewelry.store.dto.response.OrderResponse;
import com.jewelry.store.dto.response.OrderSummaryResponse;
import com.jewelry.store.entity.*;
import com.jewelry.store.entity.enums.DiscountType;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.PaymentMethodType;
import com.jewelry.store.entity.enums.PaymentStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.mapper.OrderMapper;
import com.jewelry.store.repository.*;
import com.jewelry.store.service.OrderService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CouponRepository couponRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderMapper orderMapper;

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("10000000");
    private static final BigDecimal STANDARD_SHIPPING_FEE = new BigDecimal("50000");
    private static final String ALPHANUMERIC = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        log.info("Starting checkout process for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        Cart cart = cartRepository.findByUserIdWithDetails(userId)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy giỏ hàng của bạn"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm trước khi thanh toán");
        }

        // 1. Phân giải địa chỉ giao hàng
        String shippingAddressSnapshot = resolveShippingAddress(userId, request);

        // 2. Tính toán Zero-Trust giá sản phẩm & Trừ kho với Optimistic Locking
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getVariant();
            if (variant == null) {
                throw new BadRequestException("Dữ liệu sản phẩm trong giỏ hàng không hợp lệ");
            }

            Inventory inventory = variant.getInventory();
            if (inventory == null) {
                throw new BadRequestException("Không tìm thấy thông tin kho hàng cho sản phẩm: " + variant.getSku());
            }

            // Kiểm tra tồn kho trước khi trừ
            if (inventory.getAvailableQuantity() < cartItem.getQuantity()) {
                String productName = variant.getProduct() != null ? variant.getProduct().getName() : variant.getSku();
                throw new BadRequestException("Sản phẩm '" + productName + " (SKU: " + variant.getSku() +
                        ")' không đủ số lượng tồn kho (Khả dụng: " + inventory.getAvailableQuantity() +
                        ", Yêu cầu: " + cartItem.getQuantity() + ")");
            }

            // Trừ kho nguyên tử - Kích hoạt Optimistic Locking qua @Version
            inventory.deductStock(cartItem.getQuantity());
            inventoryRepository.save(inventory);

            // Tính giá phía máy chủ (Zero-Trust)
            BigDecimal unitPrice = variant.getSalePrice();
            BigDecimal itemSubtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);

            // Ghép chuỗi thuộc tính biến thể (VD: "Vàng Trắng 18K • Size 16")
            StringBuilder attrBuilder = new StringBuilder();
            if (variant.getMaterialType() != null && !variant.getMaterialType().isBlank()) {
                attrBuilder.append(variant.getMaterialType());
            }
            if (variant.getSizeOption() != null && !variant.getSizeOption().isBlank()) {
                if (attrBuilder.length() > 0) attrBuilder.append(" • Size ");
                else attrBuilder.append("Size ");
                attrBuilder.append(variant.getSizeOption());
            }

            OrderItem orderItem = OrderItem.builder()
                    .variant(variant)
                    .productName(variant.getProduct() != null ? variant.getProduct().getName() : variant.getSku())
                    .sku(variant.getSku())
                    .variantAttributes(attrBuilder.toString())
                    .unitPrice(unitPrice)
                    .quantity(cartItem.getQuantity())
                    .subtotal(itemSubtotal)
                    .build();

            orderItems.add(orderItem);
        }

        // 3. Xử lý mã giảm giá (Coupon)
        Coupon appliedCoupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            appliedCoupon = couponRepository.findByCodeIgnoreCase(request.getCouponCode().trim())
                    .orElseThrow(() -> new BadRequestException("Mã giảm giá '" + request.getCouponCode() + "' không tồn tại"));

            if (!appliedCoupon.isValidForUse(subtotal)) {
                throw new BadRequestException("Mã giảm giá không hợp lệ hoặc đơn hàng chưa đủ điều kiện áp dụng");
            }

            if (appliedCoupon.getDiscountType() == DiscountType.PERCENTAGE) {
                discountAmount = subtotal.multiply(appliedCoupon.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                if (appliedCoupon.getMaxDiscount() != null && discountAmount.compareTo(appliedCoupon.getMaxDiscount()) > 0) {
                    discountAmount = appliedCoupon.getMaxDiscount();
                }
            } else if (appliedCoupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                discountAmount = appliedCoupon.getDiscountValue();
                if (discountAmount.compareTo(subtotal) > 0) {
                    discountAmount = subtotal;
                }
            }

            appliedCoupon.setUsedCount(appliedCoupon.getUsedCount() + 1);
            couponRepository.save(appliedCoupon);
        }

        // 4. Tính phí vận chuyển (Miễn phí từ 10 triệu đồng trở lên)
        BigDecimal shippingFee = BigDecimal.ZERO;
        if (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) < 0) {
            shippingFee = STANDARD_SHIPPING_FEE;
        }

        // 5. Tổng tiền thanh toán
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(shippingFee);

        // 6. Sinh mã đơn hàng duy nhất (VD: JLW-20261001-A9F8X)
        String orderCode = generateUniqueOrderCode();

        // 7. Tạo Order thực thể
        Order order = Order.builder()
                .orderCode(orderCode)
                .user(user)
                .coupon(appliedCoupon)
                .subtotal(subtotal)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .shippingAddressSnapshot(shippingAddressSnapshot)
                .customerNote(request.getCustomerNote())
                .build();

        for (OrderItem item : orderItems) {
            order.addOrderItem(item);
        }

        // 8. Tạo Payment thực thể
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .amount(totalAmount)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        // 9. Dọn sạch giỏ hàng của khách hàng sau khi đặt thành công
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        log.info("Order successfully created! OrderCode: {}, TotalAmount: {}", orderCode, totalAmount);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getUserOrders(Long userId, OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage;

        if (status != null) {
            orderPage = orderRepository.findByUserIdAndOrderStatus(userId, status, pageable);
        } else {
            orderPage = orderRepository.findByUserId(userId, pageable);
        }

        Page<OrderSummaryResponse> summaryPage = orderPage.map(orderMapper::toOrderSummaryResponse);
        return PageResponse.from(summaryPage);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(Long userId, String orderCode, boolean isAdmin) {
        Order order;
        if (isAdmin) {
            order = orderRepository.findWithDetailsByOrderCode(orderCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + orderCode));
        } else {
            order = orderRepository.findWithDetailsByOrderCodeAndUserId(orderCode, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại hoặc không thuộc quyền sở hữu của bạn"));
        }
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, String orderCode, CancelOrderRequest request, boolean isAdmin) {
        Order order;
        if (isAdmin) {
            order = orderRepository.findWithDetailsByOrderCode(orderCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderCode));
        } else {
            order = orderRepository.findWithDetailsByOrderCodeAndUserId(orderCode, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại hoặc không thuộc quyền sở hữu của bạn"));
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Đơn hàng này đã được hủy trước đó");
        }

        // Khách hàng chỉ được phép hủy đơn khi trạng thái còn là PENDING
        if (!isAdmin && order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Đơn hàng đã được xác nhận/xử lý nên không thể hủy trực tiếp. Vui lòng liên hệ hotline hỗ trợ");
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPING ||
            order.getOrderStatus() == OrderStatus.DELIVERED ||
            order.getOrderStatus() == OrderStatus.COMPLETED) {
            throw new BadRequestException("Không thể hủy đơn hàng đang giao hoặc đã hoàn tất");
        }

        // 1. Hoàn lại tồn kho cho tất cả sản phẩm trong đơn
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getVariant() != null && item.getVariant().getInventory() != null) {
                    Inventory inventory = item.getVariant().getInventory();
                    inventory.restoreStock(item.getQuantity());
                    inventoryRepository.save(inventory);
                }
            }
        }

        // 2. Hoàn lại lượt sử dụng mã giảm giá nếu có
        if (order.getCoupon() != null) {
            Coupon coupon = order.getCoupon();
            coupon.setUsedCount(Math.max(0, coupon.getUsedCount() - 1));
            couponRepository.save(coupon);
        }

        // 3. Cập nhật trạng thái
        order.setOrderStatus(OrderStatus.CANCELLED);
        if (request != null && request.getCancellationReason() != null && !request.getCancellationReason().isBlank()) {
            String note = order.getCustomerNote() != null ? order.getCustomerNote() + " | [Lý do hủy: " + request.getCancellationReason() + "]"
                    : "[Lý do hủy: " + request.getCancellationReason() + "]";
            order.setCustomerNote(note);
        }

        Order saved = orderRepository.save(order);
        log.info("Order {} was cancelled successfully (Cancelled by {} ID: {})", orderCode, isAdmin ? "ADMIN" : "CUSTOMER", userId);
        return orderMapper.toOrderResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getAllOrdersForAdmin(OrderStatus status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Order> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("orderStatus"), status));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate codePredicate = cb.like(cb.lower(root.get("orderCode")), searchPattern);
                Predicate addressPredicate = cb.like(cb.lower(root.get("shippingAddressSnapshot")), searchPattern);
                predicates.add(cb.or(codePredicate, addressPredicate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        Page<OrderSummaryResponse> summaryPage = orderPage.map(orderMapper::toOrderSummaryResponse);
        return PageResponse.from(summaryPage);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatusByAdmin(String orderCode, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findWithDetailsByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderCode));

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = request.getNewStatus();

        if (currentStatus == newStatus) {
            return orderMapper.toOrderResponse(order);
        }

        // Nếu chuyển sang CANCELLED, hoàn lại tồn kho
        if (newStatus == OrderStatus.CANCELLED && currentStatus != OrderStatus.CANCELLED) {
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    if (item.getVariant() != null && item.getVariant().getInventory() != null) {
                        Inventory inventory = item.getVariant().getInventory();
                        inventory.restoreStock(item.getQuantity());
                        inventoryRepository.save(inventory);
                    }
                }
            }
            if (order.getCoupon() != null) {
                Coupon coupon = order.getCoupon();
                coupon.setUsedCount(Math.max(0, coupon.getUsedCount() - 1));
                couponRepository.save(coupon);
            }
        }

        // Nếu chuyển sang DELIVERED hoặc COMPLETED với phương thức COD
        if ((newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.COMPLETED) &&
            order.getPayment() != null && order.getPayment().getPaymentMethod() == PaymentMethodType.COD) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            order.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
            order.getPayment().setPaidAt(LocalDateTime.now());
        }

        order.setOrderStatus(newStatus);
        if (request.getNote() != null && !request.getNote().isBlank()) {
            String note = order.getCustomerNote() != null
                    ? order.getCustomerNote() + " | [Cập nhật: " + request.getNote() + "]"
                    : "[Cập nhật: " + request.getNote() + "]";
            order.setCustomerNote(note);
        }

        Order saved = orderRepository.save(order);
        log.info("Admin updated order {} status from {} to {}", orderCode, currentStatus, newStatus);
        return orderMapper.toOrderResponse(saved);
    }

    private String resolveShippingAddress(Long userId, CreateOrderRequest request) {
        if (request.getShippingAddressId() != null) {
            Address address = addressRepository.findByIdAndUserId(request.getShippingAddressId(), userId)
                    .orElseThrow(() -> new BadRequestException("Địa chỉ giao hàng đã chọn không hợp lệ hoặc không thuộc về bạn"));
            return address.getReceiverName() + " (" + address.getPhone() + ") - " + address.getFullAddress();
        }

        if (request.getShippingAddress() != null) {
            return request.getShippingAddress().toFullAddressString();
        }

        // Kiểm tra xem user có địa chỉ mặc định hay không
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(addr -> addr.getReceiverName() + " (" + addr.getPhone() + ") - " + addr.getFullAddress())
                .orElseThrow(() -> new BadRequestException("Vui lòng nhập địa chỉ giao hàng hoặc chọn từ sổ địa chỉ đã lưu"));
    }

    private String generateUniqueOrderCode() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String code;
        int attempts = 0;
        do {
            StringBuilder sb = new StringBuilder("JLW-").append(datePrefix).append("-");
            for (int i = 0; i < 5; i++) {
                sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
            }
            code = sb.toString();
            attempts++;
            if (attempts > 10) {
                code = "JLW-" + datePrefix + "-" + System.currentTimeMillis() % 100000;
                break;
            }
        } while (orderRepository.existsByOrderCode(code));

        return code;
    }
}

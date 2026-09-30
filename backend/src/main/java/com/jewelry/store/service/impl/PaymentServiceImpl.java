package com.jewelry.store.service.impl;

import com.jewelry.store.config.PaymentProperties;
import com.jewelry.store.dto.response.BankTransferDetails;
import com.jewelry.store.dto.response.PaymentInitiateResponse;
import com.jewelry.store.dto.response.VNPayReturnResponse;
import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.Payment;
import com.jewelry.store.entity.enums.OrderStatus;
import com.jewelry.store.entity.enums.PaymentMethodType;
import com.jewelry.store.entity.enums.PaymentStatus;
import com.jewelry.store.exception.BadRequestException;
import com.jewelry.store.exception.ResourceNotFoundException;
import com.jewelry.store.repository.OrderRepository;
import com.jewelry.store.repository.PaymentRepository;
import com.jewelry.store.service.PaymentService;
import com.jewelry.store.util.VNPayUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentProperties paymentProperties;

    @Override
    @Transactional
    public PaymentInitiateResponse initiatePayment(Long userId, String orderCode, HttpServletRequest request) {
        log.info("Initiating payment for orderCode: {}, userId: {}", orderCode, userId);

        Order order = orderRepository.findByOrderCodeAndUserId(orderCode, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại hoặc không thuộc quyền sở hữu của bạn"));

        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Đơn hàng này đã được thanh toán thành công trước đó");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Đơn hàng này đã bị hủy, không thể thực hiện thanh toán");
        }

        Payment payment = order.getPayment();
        PaymentMethodType paymentMethod = payment != null ? payment.getPaymentMethod() : PaymentMethodType.COD;

        switch (paymentMethod) {
            case VNPAY -> {
                String paymentUrl = buildVNPayUrl(order, request);
                return PaymentInitiateResponse.builder()
                        .orderCode(orderCode)
                        .paymentMethod(PaymentMethodType.VNPAY)
                        .paymentStatus(order.getPaymentStatus())
                        .amount(order.getTotalAmount())
                        .paymentUrl(paymentUrl)
                        .message("Đã tạo liên kết thanh toán VNPay Sandbox thành công. Vui lòng chuyển hướng sang cổng thanh toán")
                        .build();
            }
            case BANK_TRANSFER -> {
                BankTransferDetails bankDetails = buildVietQrDetails(order);
                return PaymentInitiateResponse.builder()
                        .orderCode(orderCode)
                        .paymentMethod(PaymentMethodType.BANK_TRANSFER)
                        .paymentStatus(order.getPaymentStatus())
                        .amount(order.getTotalAmount())
                        .bankTransfer(bankDetails)
                        .message("Vui lòng quét mã VietQR hoặc chuyển khoản ngân hàng theo thông tin bên dưới")
                        .build();
            }
            case COD -> {
                return PaymentInitiateResponse.builder()
                        .orderCode(orderCode)
                        .paymentMethod(PaymentMethodType.COD)
                        .paymentStatus(order.getPaymentStatus())
                        .amount(order.getTotalAmount())
                        .message("Đơn hàng áp dụng phương thức Thanh toán khi nhận hàng (COD). Nhân viên sẽ liên hệ để xác nhận và giao hàng")
                        .build();
            }
            default -> throw new BadRequestException("Phương thức thanh toán chưa được hỗ trợ");
        }
    }

    @Override
    @Transactional
    public VNPayReturnResponse processVNPayReturn(Map<String, String> queryParams) {
        log.info("Processing VNPay return callback with params: {}", queryParams);

        Map<String, String> fields = new HashMap<>(queryParams);
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String hashSecret = paymentProperties.getVnpay().getHashSecret();
        String signValue = VNPayUtils.hashAllFields(fields, hashSecret);

        if (signValue == null || !signValue.equalsIgnoreCase(vnp_SecureHash)) {
            log.error("VNPay return checksum verification failed! SignValue: {}, VnpSecureHash: {}", signValue, vnp_SecureHash);
            return VNPayReturnResponse.builder()
                    .orderCode(fields.get("vnp_TxnRef"))
                    .isSuccess(false)
                    .message("Chữ ký bảo mật không hợp lệ (Invalid Checksum)")
                    .build();
        }

        String orderCode = fields.get("vnp_TxnRef");
        String responseCode = fields.get("vnp_ResponseCode");
        String transactionNo = fields.get("vnp_TransactionNo");
        String bankCode = fields.get("vnp_BankCode");
        String payDate = fields.get("vnp_PayDate");
        String amountStr = fields.get("vnp_Amount");

        BigDecimal amount = BigDecimal.ZERO;
        if (amountStr != null) {
            amount = new BigDecimal(amountStr).divide(new BigDecimal(100));
        }

        boolean isSuccess = "00".equals(responseCode);

        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            Payment payment = order.getPayment();

            if (isSuccess) {
                order.setPaymentStatus(PaymentStatus.COMPLETED);
                if (order.getOrderStatus() == OrderStatus.PENDING) {
                    order.setOrderStatus(OrderStatus.CONFIRMED);
                }
                if (payment != null) {
                    payment.setPaymentStatus(PaymentStatus.COMPLETED);
                    payment.setTransactionCode(transactionNo);
                    payment.setPaidAt(LocalDateTime.now());
                }
                orderRepository.save(order);
                log.info("VNPay payment SUCCESS for order: {}, transaction: {}", orderCode, transactionNo);
            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
                if (payment != null) {
                    payment.setPaymentStatus(PaymentStatus.FAILED);
                }
                orderRepository.save(order);
                log.warn("VNPay payment FAILED for order: {}, responseCode: {}", orderCode, responseCode);
            }
        }

        String message = isSuccess
                ? "Giao dịch thanh toán qua VNPay thành công!"
                : "Giao dịch không thành công hoặc bị hủy bởi người dùng (Mã lỗi: " + responseCode + ")";

        return VNPayReturnResponse.builder()
                .orderCode(orderCode)
                .transactionCode(transactionNo)
                .responseCode(responseCode)
                .isSuccess(isSuccess)
                .amount(amount)
                .bankCode(bankCode)
                .payDate(payDate)
                .message(message)
                .build();
    }

    @Override
    @Transactional
    public Map<String, String> processVNPayIpn(Map<String, String> queryParams) {
        log.info("Processing VNPay IPN callback: {}", queryParams);
        Map<String, String> response = new HashMap<>();

        Map<String, String> fields = new HashMap<>(queryParams);
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String hashSecret = paymentProperties.getVnpay().getHashSecret();
        String signValue = VNPayUtils.hashAllFields(fields, hashSecret);

        if (signValue == null || !signValue.equalsIgnoreCase(vnp_SecureHash)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid Checksum");
            return response;
        }

        String orderCode = fields.get("vnp_TxnRef");
        Optional<Order> orderOpt = orderRepository.findByOrderCode(orderCode);
        if (orderOpt.isEmpty()) {
            response.put("RspCode", "01");
            response.put("Message", "Order not Found");
            return response;
        }

        Order order = orderOpt.get();

        String amountStr = fields.get("vnp_Amount");
        BigDecimal vnpAmount = new BigDecimal(amountStr).divide(new BigDecimal(100));
        if (order.getTotalAmount().compareTo(vnpAmount) != 0) {
            response.put("RspCode", "04");
            response.put("Message", "Invalid Amount");
            return response;
        }

        if (order.getPaymentStatus() == PaymentStatus.COMPLETED) {
            response.put("RspCode", "02");
            response.put("Message", "Order already confirmed");
            return response;
        }

        String responseCode = fields.get("vnp_ResponseCode");
        String transactionNo = fields.get("vnp_TransactionNo");

        if ("00".equals(responseCode)) {
            order.setPaymentStatus(PaymentStatus.COMPLETED);
            if (order.getOrderStatus() == OrderStatus.PENDING) {
                order.setOrderStatus(OrderStatus.CONFIRMED);
            }
            if (order.getPayment() != null) {
                order.getPayment().setPaymentStatus(PaymentStatus.COMPLETED);
                order.getPayment().setTransactionCode(transactionNo);
                order.getPayment().setPaidAt(LocalDateTime.now());
            }
            orderRepository.save(order);
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
            if (order.getPayment() != null) {
                order.getPayment().setPaymentStatus(PaymentStatus.FAILED);
            }
            orderRepository.save(order);
        }

        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentInitiateResponse getPaymentDetails(Long userId, String orderCode) {
        Order order;
        if (userId != null) {
            order = orderRepository.findByOrderCodeAndUserId(orderCode, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderCode));
        } else {
            order = orderRepository.findByOrderCode(orderCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderCode));
        }

        Payment payment = order.getPayment();
        PaymentMethodType paymentMethod = payment != null ? payment.getPaymentMethod() : PaymentMethodType.COD;

        BankTransferDetails bankDetails = null;
        if (paymentMethod == PaymentMethodType.BANK_TRANSFER) {
            bankDetails = buildVietQrDetails(order);
        }

        return PaymentInitiateResponse.builder()
                .orderCode(order.getOrderCode())
                .paymentMethod(paymentMethod)
                .paymentStatus(order.getPaymentStatus())
                .amount(order.getTotalAmount())
                .bankTransfer(bankDetails)
                .message("Trạng thái thanh toán hiện tại: " + order.getPaymentStatus().name())
                .build();
    }

    private String buildVNPayUrl(Order order, HttpServletRequest request) {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", paymentProperties.getVnpay().getTmnCode());

        long amount = order.getTotalAmount().multiply(new BigDecimal(100)).longValue();
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", order.getOrderCode());
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang trang suc " + order.getOrderCode());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", paymentProperties.getVnpay().getReturnUrl());
        vnp_Params.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String vnp_SecureHash = VNPayUtils.hmacSHA512(paymentProperties.getVnpay().getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return paymentProperties.getVnpay().getPayUrl() + "?" + query.toString();
    }

    private BankTransferDetails buildVietQrDetails(Order order) {
        String bankId = paymentProperties.getVietqr().getBankId();
        String accountNo = paymentProperties.getVietqr().getAccountNo();
        String accountName = paymentProperties.getVietqr().getAccountName();
        String transferContent = "JLW " + order.getOrderCode();
        long amount = order.getTotalAmount().longValue();

        String qrCodeUrl = String.format(
                "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
                bankId,
                accountNo,
                amount,
                URLEncoder.encode(transferContent, StandardCharsets.UTF_8),
                URLEncoder.encode(accountName, StandardCharsets.UTF_8)
        );

        return BankTransferDetails.builder()
                .bankId(bankId)
                .accountNo(accountNo)
                .accountName(accountName)
                .amount(order.getTotalAmount())
                .orderCode(order.getOrderCode())
                .transferContent(transferContent)
                .qrCodeUrl(qrCodeUrl)
                .build();
    }
}

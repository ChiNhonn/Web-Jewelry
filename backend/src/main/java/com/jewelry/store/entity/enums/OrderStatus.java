package com.jewelry.store.entity.enums;

public enum OrderStatus {
    PENDING,        // Đơn hàng mới tạo, chờ xác nhận
    CONFIRMED,      // Đã xác nhận đơn hàng
    PROCESSING,     // Đang đóng gói & chuẩn bị trang sức
    SHIPPING,       // Đang vận chuyển
    DELIVERED,      // Đã giao thành công
    COMPLETED,      // Khách nhận & hoàn tất giao dịch
    CANCELLED,      // Đơn bị hủy
    RETURNED,       // Trả hàng
    REFUNDED        // Đã hoàn tiền
}

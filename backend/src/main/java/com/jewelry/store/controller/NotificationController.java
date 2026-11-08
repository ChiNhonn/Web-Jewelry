package com.jewelry.store.controller;

import com.jewelry.store.dto.common.ApiResponse;
import com.jewelry.store.entity.CustomerNotification;
import com.jewelry.store.repository.CustomerNotificationRepository;
import com.jewelry.store.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final CustomerNotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> list(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<Map<String, Object>> data = notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toView)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        CustomerNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new com.jewelry.store.exception.ResourceNotFoundException("Không tìm thấy thông báo"));
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new com.jewelry.store.exception.BadRequestException("Không có quyền với thông báo này");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.ok(ApiResponse.empty("Đã đọc thông báo"));
    }

    private Map<String, Object> toView(CustomerNotification notification) {
        return Map.of(
                "id", notification.getId(),
                "title", notification.getTitle(),
                "message", notification.getMessage(),
                "orderCode", notification.getOrderCode() == null ? "" : notification.getOrderCode(),
                "read", notification.isRead(),
                "createdAt", notification.getCreatedAt().toString()
        );
    }
}

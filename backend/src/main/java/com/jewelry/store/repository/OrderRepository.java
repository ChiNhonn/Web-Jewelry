package com.jewelry.store.repository;

import com.jewelry.store.entity.Order;
import com.jewelry.store.util.StoreText;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    default Optional<Order> findByOrderCode(String orderCode) {
        Long id = StoreText.orderId(orderCode);
        return id == null ? Optional.empty() : findById(id);
    }

    default Optional<Order> findByOrderCodeAndUserId(String orderCode, Long userId) {
        return findByOrderCode(orderCode).filter(order -> order.getUser().getId().equals(userId));
    }
}

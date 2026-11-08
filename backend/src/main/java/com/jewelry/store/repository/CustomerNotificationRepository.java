package com.jewelry.store.repository;

import com.jewelry.store.entity.CustomerNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification, Long> {

    List<CustomerNotification> findByUserIdOrderByCreatedAtDesc(Long userId);
}

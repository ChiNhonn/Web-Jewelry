package com.jewelry.store.repository;

import com.jewelry.store.entity.Order;
import com.jewelry.store.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByOrderCode(String orderCode);

    Optional<Order> findByOrderCodeAndUserId(String orderCode, Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus, Pageable pageable);

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.variant v " +
           "LEFT JOIN FETCH v.product p " +
           "LEFT JOIN FETCH o.payment pay " +
           "LEFT JOIN FETCH o.user u " +
           "WHERE o.orderCode = :orderCode")
    Optional<Order> findWithDetailsByOrderCode(@Param("orderCode") String orderCode);

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.variant v " +
           "LEFT JOIN FETCH v.product p " +
           "LEFT JOIN FETCH o.payment pay " +
           "LEFT JOIN FETCH o.user u " +
           "WHERE o.orderCode = :orderCode AND o.user.id = :userId")
    Optional<Order> findWithDetailsByOrderCodeAndUserId(@Param("orderCode") String orderCode, @Param("userId") Long userId);

    boolean existsByOrderCode(String orderCode);
}

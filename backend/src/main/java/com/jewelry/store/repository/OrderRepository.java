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

    long countByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderStatus IN :statuses")
    java.math.BigDecimal sumTotalRevenueByStatuses(@Param("statuses") java.util.List<OrderStatus> statuses);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.orderStatus IN :statuses AND o.createdAt >= :startDate AND o.createdAt < :endDate")
    java.math.BigDecimal sumRevenueBetweenDates(
            @Param("statuses") java.util.List<OrderStatus> statuses,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT FUNCTION('DATE', o.createdAt), COALESCE(SUM(o.totalAmount), 0), COUNT(o) " +
           "FROM Order o " +
           "WHERE o.orderStatus IN :statuses AND o.createdAt >= :startDate AND o.createdAt <= :endDate " +
           "GROUP BY FUNCTION('DATE', o.createdAt) " +
           "ORDER BY FUNCTION('DATE', o.createdAt) ASC")
    java.util.List<Object[]> getDailyRevenueAnalytics(
            @Param("statuses") java.util.List<OrderStatus> statuses,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT p.id, p.name, oi.sku, SUM(oi.quantity), SUM(oi.subtotal) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.variant v " +
           "JOIN v.product p " +
           "WHERE o.orderStatus IN :statuses " +
           "GROUP BY p.id, p.name, oi.sku " +
           "ORDER BY SUM(oi.quantity) DESC")
    java.util.List<Object[]> getTopSellingProducts(@Param("statuses") java.util.List<OrderStatus> statuses, Pageable pageable);

    @Query("SELECT c.id, c.name, COALESCE(SUM(oi.subtotal), 0), COUNT(DISTINCT o.id) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.variant v " +
           "JOIN v.product p " +
           "JOIN p.category c " +
           "WHERE o.orderStatus IN :statuses " +
           "GROUP BY c.id, c.name " +
           "ORDER BY SUM(oi.subtotal) DESC")
    java.util.List<Object[]> getCategorySalesBreakdown(@Param("statuses") java.util.List<OrderStatus> statuses);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user u LEFT JOIN FETCH o.orderItems oi ORDER BY o.createdAt DESC")
    java.util.List<Order> findRecentOrders(Pageable pageable);
}

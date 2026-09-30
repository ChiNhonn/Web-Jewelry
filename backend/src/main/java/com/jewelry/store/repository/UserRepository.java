package com.jewelry.store.repository;

import com.jewelry.store.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.email = :identifier OR u.username = :identifier")
    Optional<User> findByUsernameOrEmailWithRoles(@Param("identifier") String identifier);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    long countByCreatedAtGreaterThanEqual(java.time.LocalDateTime date);

    @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles r " +
            "WHERE (:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:role IS NULL OR r.name = :role)",
            countQuery = "SELECT COUNT(DISTINCT u) FROM User u LEFT JOIN u.roles r " +
            "WHERE (:keyword IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:role IS NULL OR r.name = :role)")
    org.springframework.data.domain.Page<User> findAllUsersWithFilter(
            @Param("keyword") String keyword,
            @Param("status") com.jewelry.store.entity.enums.UserStatus status,
            @Param("role") com.jewelry.store.entity.enums.RoleType role,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH u.addresses WHERE u.id = :id")
    Optional<User> findDetailByIdWithRolesAndAddresses(@Param("id") Long id);
}

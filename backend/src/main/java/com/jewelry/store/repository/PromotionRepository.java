package com.jewelry.store.repository;

import com.jewelry.store.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByActiveTrue();

    Optional<Promotion> findByCode(String code);
}

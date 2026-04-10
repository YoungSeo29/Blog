package com.example.blog.repository;

import com.example.blog.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    boolean existsByUserId(Long userId);
    List<Coupon> findByUserId(Long userId);
}

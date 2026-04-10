package com.example.blog.service;

import com.example.blog.domain.Coupon;
import com.example.blog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {

    private static final int MAX_COUPON_COUNT = 100;

    private final CouponRepository couponRepository;

    public void issue(Long userId) {

        if(couponRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 쿠폰을 받았습니다.");
        }

        if(couponRepository.count() >= MAX_COUPON_COUNT) {
            throw new IllegalArgumentException("쿠폰이 모두 소진되었습니다.");
        }

        couponRepository.save(Coupon.builder()
                .userId(userId)
                .build());

    }

    public List<Coupon> findByUserId(Long userId) {
        return couponRepository.findByUserId(userId);
    }

    public long getRemainingCount() {
        return MAX_COUPON_COUNT - couponRepository.count();
    }

}

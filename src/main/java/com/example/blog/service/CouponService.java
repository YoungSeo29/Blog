package com.example.blog.service;

import com.example.blog.domain.Coupon;
import com.example.blog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {

    private static final int MAX_COUPON_COUNT = 100;

    private final CouponRepository couponRepository;

    // 시도1 (Synchronized) - 한 번에 하나의 스레드만 락 획득, 진입 -> 응답시간 늦어짐
    // 시도2 (비관적락) - countWithLock()으로 조회시 select for update 실행. 다른 트랜잭션 대기.
    @Transactional
    public void issue(Long userId) {

        if(couponRepository.existsByUserId(userId)) {
            System.out.println("이미 쿠폰을 발급받음");
            throw new IllegalStateException("이미 쿠폰을 받았습니다.");
        }

        if(couponRepository.countWithLock() >= MAX_COUPON_COUNT) {
            System.out.println("쿠폰이 모두 소진됨");
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
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

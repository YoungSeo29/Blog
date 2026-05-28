package com.example.blog.service;

import com.example.blog.domain.Coupon;
import com.example.blog.domain.CouponPolicy;
import com.example.blog.repository.CouponPolicyRepository;
import com.example.blog.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private static final Long COUPON_POLICY_ID = 1L;  // 쿠폰 정책

    // 시도1 (Synchronized) - 한 번에 하나의 스레드만 락 획득, 진입 -> 응답시간 늦어짐
    // 시도2 (비관적락) - countWithLock()으로 조회시 select for update 실행. 다른 트랜잭션 대기.
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void issue(Long userId) {

        if(couponRepository.existsByUserId(userId)) {
            System.out.println("이미 쿠폰을 발급받음");
            throw new IllegalStateException("이미 쿠폰을 받았습니다.");
        }
        /*
        CouponPolicy policy = couponPolicyRepository.findByIdWithLock(COUPON_POLICY_ID)
                .orElseThrow( () -> new IllegalStateException("쿠폰 정책이 존재하지 않습니다."));

        // 발급된 쿠폰 수량 증가 +1
        policy.increaseIssuedCount();
         */

        int updated = couponPolicyRepository.increaseIssuedCoundIfAvailable(COUPON_POLICY_ID);

        if (updated == 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다");
        }

        couponRepository.save(Coupon.builder()
                .userId(userId)
                .build());
    }

    public List<Coupon> findByUserId(Long userId) {
        return couponRepository.findByUserId(userId);
    }

    public long getRemainingCount() {
        CouponPolicy policy = couponPolicyRepository.findById(COUPON_POLICY_ID)
                .orElseThrow(() -> new IllegalStateException("쿠폰 정책이 존재하지 않습니다."));
        return policy.getTotalCount() - policy.getIssuedCount();
    }

}

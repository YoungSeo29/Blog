package com.example.blog.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "coupon_policy")
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int totalCount;

    @Column(nullable = false)
    private int issuedCount;

    @Builder
    public CouponPolicy(int totalCount) {
        this.totalCount = totalCount;
        this.issuedCount = 0;
    }

    public boolean isExhausted() {
        return this.issuedCount >= this.totalCount;
    }

    public void increaseIssuedCount() {
        if( isExhausted() ) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }
        this.issuedCount++;
    }


}

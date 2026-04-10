package com.example.blog.controller;

import com.example.blog.domain.User;
import com.example.blog.service.CouponService;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CouponApiController {

    private final CouponService couponService;
    private final UserService userService;

    @PostMapping("/api/coupon")
    public ResponseEntity<Void> issueCoupon() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());

        try {
            couponService.issue(user.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {

            // 이미 받은 경우 - 409
            if(e.getMessage().contains("이미")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();  // 409 error
            }

            // 모두 소진 된 경우
            return ResponseEntity.badRequest().build();  // 400 error

        }

    }

    @GetMapping("/api/coupon/count")
    public ResponseEntity<Map<String, Long>> getRemainCount() {
        return ResponseEntity.ok(Map.of("remaining", couponService.getRemainingCount()));
    }
}

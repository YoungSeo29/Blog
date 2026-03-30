package com.example.blog.controller;


import com.example.blog.dto.CreateAccessTokenRequestDto;
import com.example.blog.dto.CreateAccessTokenResponseDto;
import com.example.blog.service.RefreshTokenService;
import com.example.blog.service.TokenService;
import com.example.blog.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

//    @PostMapping("/api/token")
//    public ResponseEntity<CreateAccessTokenResponseDto> createNewAccessToken(
//            @RequestBody CreateAccessTokenRequestDto request) {
//        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new CreateAccessTokenResponseDto(newAccessToken));
//    }

    @PostMapping("/api/token")
    public ResponseEntity<Void> createNewAccessToken(
            @RequestBody CreateAccessTokenRequestDto request,
            HttpServletResponse response) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
        CookieUtil.addCookie(response, "access_token", newAccessToken, (int) Duration.ofHours(2).toSeconds());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/refresh-token")
    public ResponseEntity deleteRefreshToken() {
        refreshTokenService.delete();

        return ResponseEntity.ok().build();
    }
}

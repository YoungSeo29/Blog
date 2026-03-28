package com.example.blog.controller;

import com.example.blog.config.jwt.TokenProvider;
import com.example.blog.domain.User;
import com.example.blog.dto.AddUserRequestDto;
import com.example.blog.dto.CreateAccessTokenResponseDto;
import com.example.blog.service.UserService;
import com.example.blog.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("/user")
    public String signup(AddUserRequestDto request) {
        userService.save(request);

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/login";
    }

    @PostMapping("/api/login")
    public ResponseEntity<CreateAccessTokenResponseDto> login(
            @RequestBody AddUserRequestDto request,
            HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);

        String accessToken = tokenProvider.generateToken(
                (User) authentication.getPrincipal(), Duration.ofHours(2));

        CookieUtil.addCookie(response, "access_token", accessToken, (int) Duration.ofHours(2).toSeconds());

        System.out.println("로그인 함수 실행");
        return ResponseEntity.ok(new CreateAccessTokenResponseDto(accessToken));
    }
}

package com.example.blog.config;

import com.example.blog.config.jwt.TokenProvider;
import com.example.blog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.blog.config.oauth.OAuth2SuccessHandler;
import com.example.blog.config.oauth.OAuth2UserCustomService;
import com.example.blog.service.RefreshTokenService;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
/*
기본 WebSecurityConfig는 세션 기반 인증 방식에 필요함
WebOAuthSecurityConfig는 JWT 기반 인증 방식.

** 세션 기반 인증 :
    인증 성공하면, 서버에서 세션 객체 생성 -> 클라에게 JSESSIONID 쿠키를 줌
** JWT 기반 인증 :
    로그인 성공 시, 서버가 JWT 토큰 만들어서 클라에게 줌 -> 클라는 매 요청마다 토큰을 Authorization 헤더에 담아서 보냄.
 */
@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    // 스프링 시큐리티 기능 비활성화
    // = 인증, 인가 필터 모두 스킵 (정적 리소스는 비활성화 해도 됨)
    @Bean
    public WebSecurityCustomizer configure() {

        return (web) -> web.ignoring()
                .requestMatchers("/img/**", "/css/**", "/jss/**");
    }


    // 시큐리티 전반적인 필터 체인 구성

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 토큰 방식으로 인증을 하기 때문에, 기존에 사용하던 폼 로그인, 세션 비활성화
        return http
                .csrf(AbstractHttpConfigurer::disable)  // csrf 보호 비활성화(JWT 기반이라 필요없음)
                .httpBasic(AbstractHttpConfigurer::disable)  // 브라우저 기본 인증창 비활성화
                .formLogin(AbstractHttpConfigurer::disable)  // 폼 로그인 비활성화(JWT 쓰니까)
                .logout(AbstractHttpConfigurer::disable)  // 로그아웃 처리 비활성화
                // 세션을 아예 사용하지 않음
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // JWT 필터를 시큐리티 필터 체인 앞에 등록
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 인증, 인가 설정
                .authorizeRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated().anyRequest().permitAll())
                // Oauth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2.loginPage("/login")
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                        .successHandler(oAuth2SuccessHandler())
                )
                // 인증 실패 시 에러 처리 방식 결정
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        ))
                .build();
    }


    // Oauth2 로그인 성공 이후 할 작업을 정의하는 핸들러 등록
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new Oauth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }


    // 요청 헤더이 있는 JWT 토큰을 파싱 -> 인증 객체 생성 -> SpringContext 에 저장하는 필터
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }


    // 로그인 과정 중, 인가 요청 정보를 쿠키로 저장, 복원하는 커스텀 레포지토리
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptpasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

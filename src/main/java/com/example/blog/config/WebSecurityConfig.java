package com.example.blog.config;

import com.example.blog.config.jwt.TokenProvider;
import com.example.blog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.blog.config.oauth.OAuth2SuccessHandler;
import com.example.blog.config.oauth.OAuth2UserCustomService;
import com.example.blog.repository.RefreshTokenRepository;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserService userService;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    // 스프링 시큐리티 기능 비활성화
    // 스프링 시큐리티의 모든 기능을 사용하지 않겠다는 설정.
    // 인증, 인가 서비스를 모든곳에 적용X -> 일반적으로 정적 리소스에 설정함
    // = 즉, "/static/**" 경로에는 일절 간섭하지말아라!!!!! 인증, 인가 검사 하지마라!!!
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        new AntPathRequestMatcher("/static/**"),
                        new AntPathRequestMatcher("/img/**"),    // 추가
                        new AntPathRequestMatcher("/css/**"),    // 추가
                        new AntPathRequestMatcher("/js/**")      // 추가
                );
    }


    // 특정 HTTP 요청에 대한 웹 기반 보안 구정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // 기본 로그인 방식 전부 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)  // 브라우저 기본 인증창 비활성화
                .formLogin(AbstractHttpConfigurer::disable)  // 폼 로그인 비활성화(JWT 쓰니까)
                .logout(AbstractHttpConfigurer::disable)  // 로그아웃 처리 비활성화

                // 세션을 아예 사용하지 않음
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // JWT 필터를 시큐리티 필터 체인 앞에 등록
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // Oauth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2.loginPage("/login")
                        // Authorization 요청과 관련된 상태 저장
                        .authorizationEndpoint(authorizationEndpoint -> authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuth2UserCustomService))
                        // 인증 성공시 실행할 핸들러
                        .successHandler(oAuth2SuccessHandler())
                )

                .authorizeHttpRequests(auth -> auth   // 특정 경로에 대한 액세스 설정.
                        .requestMatchers(
                                "/login", "/signup", "/user",  // 페이지
                                "/api/login", "/api/token",    // 인증 API
                                "/img/**", "/css/**", "/js/**" // 정적 리소스
                        ).permitAll()
                        .anyRequest().authenticated())

                // 인증 실패 시 에러 처리 방식 결정
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new AntPathRequestMatcher("/api/**")
                        ))
                .build();
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

    // Oauth2 로그인 성공 이후 할 작업을 정의하는 핸들러 등록
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

//    @Bean
//    public AuthenticationManager authenticationManager(
//            HttpSecurity http,
//            BCryptPasswordEncoder passwordEncoder, UserDetailsService userDetailService
//    ) throws Exception {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//
//        return new ProviderManager(authProvider);
//    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

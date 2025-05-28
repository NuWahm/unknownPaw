package com.seroter.unknownPaw.security.config;

import com.seroter.unknownPaw.security.filter.ApiCheckFilter;
import com.seroter.unknownPaw.security.filter.CORSFilter;
import com.seroter.unknownPaw.security.handler.ApiLoginFailHandler;
import com.seroter.unknownPaw.security.service.MembersOAuth2UserDetailsService;
import com.seroter.unknownPaw.security.service.MembersUserDetailsService;
import com.seroter.unknownPaw.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MembersUserDetailsService userDetailsService;
    private final MembersOAuth2UserDetailsService oAuth2UserService;
    private final JWTUtil jwtUtil;
    private final ApiLoginFailHandler apiLoginFailHandler;
    private final ApplicationContext applicationContext;
    private final CORSFilter corsFilter; // 꼭 주입

    // [1] AuthenticationManager 명시적 등록 (PasswordEncoder는 따로 주입)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       org.springframework.security.crypto.password.PasswordEncoder encoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(encoder);
        return builder.build();
    }

    // [2] SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 인증 필요한 API 패턴 넓게 설정
        ApiCheckFilter apiCheckFilter = new ApiCheckFilter(
                new String[]{
                        "/api/posts/**",
                        "/api/member/mypage",
                        "/api/member/profile/simple/**",
                        "/api/member/*/pets",
                        "/api/member/*/posts",
                        "/api/member/me",
                        "/api/member/update",
                        "/api/member/change-password",
                        "/api/member/withdraw",
                        "/api/pet/register/later",
                        "/api/pet/{petId}"
                }, jwtUtil);

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/member/login",
                                "/api/member/register",
                                "/api/maps/**", // 지도 API는 인증 없이 접근 가능
                                "/api/member/check-email",
                                "/api/member/check-phone",
                                "/api/member/check-nickname"
                        ).permitAll()
                        .requestMatchers(
                                "/api/posts/**",
                                "/api/member/mypage",
                                "/api/member/profile/simple/**",
                                "/api/member/*/pets",
                                "/api/member/*/posts",
                                "/api/member/me",
                                "/api/member/update",
                                "/api/member/change-password",
                                "/api/member/withdraw",
                                "/api/pet/register/later",
                                "/api/pet/{petId}"
                        ).authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiCheckFilter, UsernamePasswordAuthenticationFilter.class);

        // OAuth2 로그인도 필요시만 활성화
        if (applicationContext.getBeanNamesForType(ClientRegistrationRepository.class).length > 0) {
            http.oauth2Login(oauth -> oauth.userInfoEndpoint(info -> info.userService(oAuth2UserService)));
        }
        return http.build();
    }
}
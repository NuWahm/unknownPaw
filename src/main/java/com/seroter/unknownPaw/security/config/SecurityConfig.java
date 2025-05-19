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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MembersUserDetailsService userDetailsService;
    private final MembersOAuth2UserDetailsService oAuth2UserService;
    private final JWTUtil jwtUtil;
    private final ApiLoginFailHandler apiLoginFailHandler;
    private final ApplicationContext applicationContext;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       org.springframework.security.crypto.password.PasswordEncoder encoder)
            throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder);
        return builder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ApiCheckFilter apiCheckFilter = new ApiCheckFilter(
                new String[]{
                        "/api/posts/**",
                        "/api/member/mypage"
                },
                jwtUtil
        );

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/member/login", "/api/member/register",
                                "/api/maps/**").permitAll()
                        .requestMatchers(
                                "/api/posts/**",
                                "/api/member/mypage"
                        ).authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(new CORSFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiCheckFilter, UsernamePasswordAuthenticationFilter.class);

        if (applicationContext.getBeanNamesForType(ClientRegistrationRepository.class).length > 0) {
            http.oauth2Login(oauth -> oauth
                    .userInfoEndpoint(info -> info.userService(oAuth2UserService)));
        }
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Vite 개발 서버 주소로 수정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Origin",
                "X-Requested-With",
                "Content-Type",
                "Accept",
                "Key",
                "Authorization"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization" // JWT 토큰을 클라이언트에서 받을 수 있도록 추가
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
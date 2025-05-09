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
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MembersUserDetailsService userDetailsService;
    private final MembersOAuth2UserDetailsService oAuth2UserService;
    private final JWTUtil jwtUtil;
    private final ApiLoginFailHandler apiLoginFailHandler;
    private final ApplicationContext applicationContext;


    // 인증 관리자 등록
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        return builder.build();
    }


    // 시큐리티 필터 체인 설정 (최신 문법)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ApiCheckFilter apiCheckFilter = new ApiCheckFilter(
            new String[]{"/api/posts/**", "/api/member/mypage"}, jwtUtil
        );

        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/member/login", "/api/member/register").permitAll()
                .requestMatchers("/api/posts/**", "/api/member/mypage").permitAll()
                //.requestMatchers("/api/posts/**", "/api/member/mypage").authenticated()
                .anyRequest().permitAll()
            )
            //.addFilterBefore(new CORSFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(apiCheckFilter, UsernamePasswordAuthenticationFilter.class);

        if (applicationContext.getBeanNamesForType(ClientRegistrationRepository.class).length > 0) {
            http.oauth2Login(oauth -> oauth
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
            );
        }

        return http.build();
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // 모든 API 경로 허용
                .allowedOrigins("http://localhost:5173") // ★ 프론트 주소만 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true); // ★ credentials 허용
        }
    }


}
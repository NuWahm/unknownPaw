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
    private final CORSFilter corsFilter;  // 내가 만든 필터 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ApiCheckFilter apiCheckFilter = new ApiCheckFilter(
                new String[]{"/api/posts/**", "/api/member/mypage", "/api/member/profile/**" }, jwtUtil);


        //front main 작업과 매치되도록 수정 예정

        http.csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/member/login", "/api/member/register","/api/maps/**").permitAll()

                        .requestMatchers("/api/posts/**", "/api/member/mypage", "/api/member/profile/simple/me").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiCheckFilter, UsernamePasswordAuthenticationFilter.class);

        if (applicationContext.getBeanNamesForType(ClientRegistrationRepository.class).length > 0) {
            http.oauth2Login(oauth -> oauth.userInfoEndpoint(info -> info.userService(oAuth2UserService)));
        }
        return http.build();
    }

}

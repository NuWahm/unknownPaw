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

    private final MembersUserDetailsService          userDetailsService;
    private final MembersOAuth2UserDetailsService    oAuth2UserService;
    private final JWTUtil                            jwtUtil;
    private final ApiLoginFailHandler                apiLoginFailHandler;
    private final ApplicationContext                 applicationContext;

    /* ----------------------------------------------------------------
       ① AuthenticationManager 생성
          ⓐ EncoderConfig 에 이미 등록된 PasswordEncoder(BCrypt)가 있으므로
             여기서 새로 만들지 않고 **주입**만 받아 사용합니다.
       ---------------------------------------------------------------- */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       org.springframework.security.crypto.password.PasswordEncoder encoder)
            throws Exception {

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(encoder);            // 🔸 주입받은 encoder 사용
        return builder.build();
    }

    /* ----------------------------------------------------------------
       ② *중복* passwordEncoder Bean 제거
          EncoderConfig 에서 이미 정의됐으므로 **아래 메서드는 삭제**했습니다.
          ----------------------------------------------------------------
          @Bean
          public BCryptPasswordEncoder passwordEncoder() { ... }
          ---------------------------------------------------------------- */

    /* ----------------------------------------------------------------
       ③ SecurityFilterChain – 기존 로직 유지 (변경 없음)
       ---------------------------------------------------------------- */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        ApiCheckFilter apiCheckFilter = new ApiCheckFilter(

                new String[]{"/api/posts/**", "/api/member/mypage", "/api/member/profile/**" }, jwtUtil);



        //front main 작업과 매치되도록 수정 예정
        http


                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/member/login", "/api/member/register","api/posts/maps").permitAll()
                        .requestMatchers("/api/posts/**", "/api/member/mypage", "/api/member/profile/simple/me").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(new CORSFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(apiCheckFilter, UsernamePasswordAuthenticationFilter.class);


        /* OAuth2 로그인은 있을 때만 활성화 */
        if (applicationContext.getBeanNamesForType(ClientRegistrationRepository.class).length > 0) {
            http.oauth2Login(oauth -> oauth
                    .userInfoEndpoint(info -> info.userService(oAuth2UserService)));
        }
        return http.build();
    }
}
package com.seroter.unknownPaw.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// CORS(Cross Origin Resource Sharing)
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) //필터의 우선순위가 높다를 표시
@Log4j2
public class CORSFilter extends OncePerRequestFilter {

  // HTTP 요청을 처리하는 doFilterInternal 메서드
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // 요청 URI 로그 출력
    log.info("CORSFilter processing request for URI: {}", request.getRequestURI());
    // 요청 헤더 로그 출력
    log.info("CORSFilter Request Headers: Authorization={}", request.getHeader("Authorization"));

    // 특정 도메인만 허용하도록 설정 (React 앱의 도메인)
    response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");  // 프론트엔드 주소 설정
    response.setHeader("Access-Control-Allow-Credentials", "true"); // 자격 증명(Cookie, Authorization)을 허용
    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"); // 지원하는 HTTP 메소드 설정
    response.setHeader("Access-Control-Max-Age", "3600"); // Preflight 요청 캐시 시간 설정 (1시간)
    response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization"); // 허용하는 헤더 설정

    // OPTIONS 요청에 대한 빠른 응답 처리
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK); // OPTIONS 요청에 대해 200 OK 응답
    } else {
      filterChain.doFilter(request, response); // 다른 요청은 필터 체인에 넘기기
    }
  }
}

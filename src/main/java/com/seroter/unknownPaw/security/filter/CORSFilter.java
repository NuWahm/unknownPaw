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
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // 👇 여기에 로그를 추가합니다. 요청 URI를 찍어봅시다.
    log.info("CORSFilter processing request for URI: {}", request.getRequestURI());
    // 헤더도 찍어볼 수 있습니다.
    log.info("CORSFilter Request Headers: Authorization={}", request.getHeader("Authorization"));

    response.setHeader("Access-Control-Allow-Origin", "*");  // 필요한 주소 설정
    response.setHeader("Access-Control-Allow-Credentials", "true"); //클라이언트 요청이 쿠키를 통해서 자격 증명을 해야 하는 경우 TRUE
    response.setHeader("Access-Control-Allow-Methods", "*"); // GET, POST, PUT, DELETE
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers", // 기본적으로 브라우저에게 노출이 되지 않지만, 브라우저 측에서 접근할 수 있게 허용해주는 헤더 지정
        "Origin, X-Requested-with, Content-Type, Accept, Key, Authorization");
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
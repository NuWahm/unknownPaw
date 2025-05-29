package com.seroter.unknownPaw.security.filter;

// 필요한 import 추가 (org.springframework.security.core.userdetails.User 등)

import com.seroter.unknownPaw.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User; // Spring Security의 User 클래스 사용
import org.springframework.security.core.userdetails.UserDetails; // UserDetails 인터페이스
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors; // Collectors 추가

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {

  private final String[] pattern;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();
  private final JWTUtil jwtUtil;

  // 생성자에서 UserDetailsService 제거
  public ApiCheckFilter(String[] pattern, JWTUtil jwtUtil) {
    this.pattern = pattern;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    boolean needCheck = false;
    for (String p : pattern) {
      if (antPathMatcher.match(request.getContextPath() + p, request.getRequestURI())) {
        needCheck = true;
        break;
      }
    }
    if (!needCheck) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader("Authorization");

    if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
      deny(response, "No valid JWT token found in header."); // 메시지 상세화
      return;
    }

    try {
      String token = header.substring(7);

      log.debug("Extracted Token: {}", token);
      String email = jwtUtil.validateAndExtract(token);
      String role = jwtUtil.getClaims(token).get("role", String.class); // JWT에서 역할 추출

      // JWT에서 추출한 정보로 UserDetails 객체 생성
      // MemberAuthDTO를 사용할 수도 있지만, LazyInitializationException을 피하기 위해
      // 여기서는 Spring Security의 기본 User 클래스를 사용하는 것이 안전합니다.
      // MemberAuthDTO가 Member 엔티티와 강하게 연결되어 있다면 더더욱.
      UserDetails userDetails = User.builder()
          .username(email)
          .password("") // 패스워드는 필요 없으므로 빈 문자열
          .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + role))) // JWT의 역할로 권한 설정
          .build();

      // SecurityContext에 Authentication 주입
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authToken);

      filterChain.doFilter(request, response);

    } catch (Exception ex) {
      log.error("JWT validation or processing error: {}", ex.getMessage(), ex); // 에러 로그 상세화
      deny(response, "Invalid or expired JWT token."); // 메시지 상세화
    }
  }

  /* ---------- 403 공통 응답 ---------- */
  private void deny(HttpServletResponse res, String message) throws IOException {
    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
    res.setContentType("application/json;charset=utf-8");
    try (PrintWriter out = res.getWriter()) {
      JSONObject body = new JSONObject();
      body.put("code", 403);
      body.put("message", message);
      out.println(body);
    }
  }
}
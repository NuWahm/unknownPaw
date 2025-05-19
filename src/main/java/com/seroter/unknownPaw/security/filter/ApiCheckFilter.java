package com.seroter.unknownPaw.security.filter;

import com.seroter.unknownPaw.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;   // ★
import org.springframework.security.core.authority.SimpleGrantedAuthority;            // ★
import org.springframework.security.core.context.SecurityContextHolder;               // ★
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;                                                                // ★

@Log4j2
public class ApiCheckFilter extends OncePerRequestFilter {

  private final String[] pattern;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();
  private final JWTUtil jwtUtil;

  public ApiCheckFilter(String[] pattern, JWTUtil jwtUtil) {
    this.pattern = pattern;
    this.jwtUtil = jwtUtil;
  }

  // MAIN
  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    /** ① 보호 URL인지 확인 */
    boolean needCheck = false;
    for (String p : pattern) {
      if (antPathMatcher.match(request.getContextPath() + p,
          request.getRequestURI())) {
        needCheck = true;
        break;
      }
    }
    if (!needCheck) {                     // 보호 URL 아님 → 그대로 진행
      filterChain.doFilter(request, response);
      return;
    }

    /** ② Authorization 헤더 파싱 */
    String header = request.getHeader("Authorization");

    if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
      deny(response);
      return;
    }

    try {
      String token = header.substring(7);

      log.debug("Extracted Token: {}", token);  //💫
      // sub(email)·role 추출
      String email = jwtUtil.validateAndExtract(token);
      String role = jwtUtil.getClaims(token)
          .get("role", String.class);

      /** ③ SecurityContext에 Authentication 주입 */
      var authList = List.of(
          new SimpleGrantedAuthority("ROLE_" + role)
      );
      var authToken =
          new UsernamePasswordAuthenticationToken(email, null, authList);
      SecurityContextHolder.getContext().setAuthentication(authToken);
      filterChain.doFilter(request, response);

    } catch (Exception ex) {

      ex.printStackTrace();
      deny(response);
    }
  }

  /* ---------- 403 공통 응답 ---------- */
  private void deny(HttpServletResponse res) throws IOException {
    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
    res.setContentType("application/json;charset=utf-8");
    try (PrintWriter out = res.getWriter()) {
      JSONObject body = new JSONObject();
      body.put("code", 403);
      body.put("message", "Fail check API token");
      out.println(body);
    }
  }
}
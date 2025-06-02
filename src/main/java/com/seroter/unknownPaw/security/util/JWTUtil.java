package com.seroter.unknownPaw.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
@Log4j2
public class JWTUtil {

  private final String secretKey = "1234567890abcdefghijklmnopqrstuvwxyz";
  private final long expire = 60 * 24 * 30;          // minutes

  /* ---------- 토큰 발행 ---------- */
  public String generateToken(String email, String role) {
    return Jwts.builder()
        .issuedAt(new Date())
        .expiration(Date.from(
            ZonedDateTime.now().plusMinutes(expire).toInstant()))
        .claim("sub", email)
        .claim("role", role)
        .signWith(Keys.hmacShaKeyFor(
            secretKey.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  // sub(email)만 추출

  public String validateAndExtract(String token) {
    Claims claims = getClaims(token);
    return claims.get("sub", String.class);
  }

  // Claims 전부 얻기
  public Claims getClaims(String token) {                            // ★ 새 메서드
    return (Claims) Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(
            secretKey.getBytes(StandardCharsets.UTF_8)))
        .build()
        .parse(token)
        .getPayload();
  }


  public String getEmail(String token) {
    try {
      Claims claims = getClaims(token);
      return claims.getSubject(); // 보통 email을 subject로 넣는 경우
    } catch (Exception e) {
      throw new RuntimeException("Invalid JWT token", e);
    }
  }
}
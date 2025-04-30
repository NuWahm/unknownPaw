package com.seroter.unknownPaw.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponseDTO {
  private Long mid;                   // 회원 ID
  private String email;             // 이메일
  private String nickname;          // 닉네임
  private String profileImagePath;  // 프로필 이미지
  private float pawRate;            // 평점
  private boolean emailVerified;    // 이메일 인증 여부
  private String role;              // 역할
  private String status;            // 상태
  private LocalDateTime regDate;    // 가입일
}


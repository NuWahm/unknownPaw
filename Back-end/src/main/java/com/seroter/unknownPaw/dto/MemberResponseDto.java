package com.seroter.unknownPaw.dto.member;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponseDto {
    private Long id;                   // 회원 ID
    private String email;             // 이메일
    private String nickname;          // 닉네임
    private String profileImagePath;  // 프로필 이미지
    private float pawRate;            // 평점
    private boolean emailVerified;    // 이메일 인증 여부
    private String role;              // 역할
    private String status;            // 상태
    private LocalDateTime regDate;    // 가입일
}
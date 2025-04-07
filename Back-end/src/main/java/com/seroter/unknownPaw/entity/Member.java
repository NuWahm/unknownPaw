package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "member")
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mid; // 회원 고유 번호(PK)

  // 🔐 로그인 정보
  @Column(nullable = false, unique = true, length = 100)
  private String email; // 이메일 주소, 로그인 ID로 사용됨

  @Column(length = 100)
  private String password; // 일반 로그인 시 사용, 소셜 로그인은 null 가능

  @Column(nullable = false)
  private boolean fromSocial; // 소셜 로그인 여부 (true면 소셜)

  @Column(length = 100)
  private String socialId; // 소셜 로그인 플랫폼에서 받은 사용자 식별 ID

  // 👤 기본 사용자 정보
  @Column(nullable = false, length = 50)
  private String name; // 실명 또는 사용자 이름

  @Column(nullable = false, unique = true, length = 50)
  private String nickname; // 닉네임, 게시판 활동 등에서 사용

  @Column(length = 20)
  private String phoneNumber; // 전화번호

  @Column(nullable = false)
  private int birthday; // 출생 연도 (예: 1990)

  @Column(nullable = false)
  private Boolean gender; // 성별 true = 남성, false = 여성

  @Column(length = 255)
  private String address; // 주소 (시/구 정도 수준)

  // 🌟 사용자 추가 정보
  private float pawRate; // 사용자 평점

  private String profileImagePath; // 프로필 이미지 파일 경로

  @Column(nullable = false)
  private boolean emailVerified; // 이메일 인증 여부

  @Column(length = 30)
  private String signupChannel; // 가입 경로 (kakao, google등)

  // 🛡️ 권한 및 상태
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role; // 사용자 권한 (일반회원, 관리자)

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MemberStatus status; // 회원 상태 (활성, 비활성, 차단, 탈퇴 등)

  public enum Role {
    USER, ADMIN
  }

  public enum MemberStatus {
    ACTIVE, INACTIVE, BANNED, DELETED
  }
}
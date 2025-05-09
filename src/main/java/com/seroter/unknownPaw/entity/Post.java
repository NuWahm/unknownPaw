package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(exclude = "member")
public abstract class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long postId;

  private String title; // 글제목

  @Column(columnDefinition = "TEXT")
  private String content;  // 글내용

  @Enumerated(EnumType.STRING)
  private ServiceCategory serviceCategory;

  private int desiredHourlyRate;

  private int likes; // 관심(좋아요 수)


  private int chatCount; // 채팅 개수

  private String defaultLocation; // 기본 위치

  private String flexibleLocation; // 유동적인 위치

  private LocalDateTime regDate; // 등록일

  private LocalDateTime modDate; // 수정일

  @PrePersist
  protected void onCreate() {
    this.regDate = LocalDateTime.now();
    this.modDate = LocalDateTime.now();

  }

  @PreUpdate
  protected void onUpdate() {
    this.modDate = LocalDateTime.now();
  }

  // 관계 설정
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mid")
  private Member member; // 회원번호(참조 키) (펫오너)

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostRole role;
}

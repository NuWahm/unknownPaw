package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
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

  private String title;
  @Column(columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  private ServiceCategory serviceCategory;

  private int likes;
  private int chatCount;

  private String defaultLocation;
  private String flexibleLocation;

  private LocalDateTime regDate;
  private LocalDateTime modDate;

  @PrePersist
  protected void onCreate() {
    this.regDate = LocalDateTime.now();
    this.modDate = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.modDate = LocalDateTime.now();
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mid")
  private Member member;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PostType postType;

  // ✅ 통합된 시급 필드
  @Column(name = "hourly_rate", nullable = false)
  @Builder.Default
  private Integer hourlyRate = 5000;
}
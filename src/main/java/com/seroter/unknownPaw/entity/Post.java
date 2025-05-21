package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "post_type")
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
  private LocalDateTime serviceDate;
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


  @Column(name = "hourly_rate", nullable = false)
  @Builder.Default
  private Integer hourlyRate = 5000;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Image> images = new ArrayList<>();
}
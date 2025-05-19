package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "community_image")
public class CommunityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityImageId;  // 커뮤니티 이미지 ID

    // ========== [커뮤니티 게시글 참조] ==========
  
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;  // 커뮤니티 게시글과 연관

    // ========== [이미지 URL 저장] ==========
    private String communityImageUrl;  // 커뮤니티 이미지 URL

    // ========== [썸네일 여부 필드 추가] ==========
    private boolean communityIsThumbnail;  // 커뮤니티 이미지가 썸네일인지 여부

    // 이미지 참조
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;

}

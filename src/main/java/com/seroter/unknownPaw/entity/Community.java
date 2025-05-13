package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "community")
public class Community {

    // ========== [기본키: 커뮤니티 ID] ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId; // 실제 컬럼명도 community_id로 생성됨

    // ========== [게시글 정보] ==========
    private String title;                // 제목
    private String content;              // 내용

    // ========== [좋아요 수 설정 메서드] ==========
    // ========== [통계 정보] ==========
    @Setter
    private int likes;                   // 좋아요 수
    // ========== [댓글 수 설정 메서드] ==========
    @Setter
    private int comment;                 // 댓글 수

    // ========== [작성자 정보] ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;               // 작성자 (회원 엔티티 참조)

    // ========== [분류] ==========
    @Enumerated(EnumType.STRING)
    private CommunityCategory communityCategory; // 커뮤니티 카테고리 (enum)

    // ========== [등록일] ==========
    private LocalDateTime regDate;

    // ========== [등록일 자동 세팅] ==========
    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
    }

    // ========== [게시글 수정 메서드] ==========
    public void modify(CommunityRequestDTO communityRequestDTO) {
        this.title = communityRequestDTO.getTitle();
        this.content = communityRequestDTO.getContent();
        this.communityCategory = communityRequestDTO.getCommunityCategory(); // ✅ 단순 대입
    }

    // ========== [커뮤니티 이미지 리스트] (양방향 매핑) ==========
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommunityImage> communityImages = new ArrayList<>();

    // ========== [댓글 리스트] ==========
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    // ========== [이미지 리스트 추가] ==========
    public void addImage(CommunityImage image) {
        communityImages.add(image);
        image.setCommunity(this); // 양방향 관계 유지
    }

    // ========== [이미지 리스트 제거] ==========
    public void removeImage(CommunityImage image) {
        communityImages.remove(image);
        image.setCommunity(null); // 양방향 관계 해제
    }

}

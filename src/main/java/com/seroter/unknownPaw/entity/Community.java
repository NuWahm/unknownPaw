package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;  // 게시글 ID

    private String title; // 글 제목
    private String content; // 글 내용
    private String defaultLocation; // 기본 위치
    private String flexibleLocation; // 유동적인 위치

    private int desiredHourlyRate; // 희망 시급
    private String serviceCategory; // 서비스 카테고리
    private String postType; // 포스트 타입

    private int likes; // 좋아요 수
    private int chatCount; // 채팅 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 작성자

    @Enumerated(EnumType.STRING)
    private CommunityCategory communityCategory; // 커뮤니티 카테고리

    private LocalDateTime regDate; // 등록일

    // 커뮤니티 게시글 수정 메서드
    public void modify(CommunityRequestDTO dto) {
        // 수정할 필드들을 새 값으로 갱신
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.defaultLocation = dto.getDefaultLocation();
        this.flexibleLocation = dto.getFlexibleLocation();
        this.desiredHourlyRate = dto.getDesiredHourlyRate();
        this.serviceCategory = dto.getServiceCategory();
        this.postType = dto.getPostType();
        // 기타 필요한 수정 사항을 추가할 수 있음
    }
}

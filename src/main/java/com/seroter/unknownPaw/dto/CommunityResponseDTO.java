package com.seroter.unknownPaw.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityResponseDTO {

    private Long postId;                      // 게시글 ID
    private String title;                     // 제목
    private String content;                   // 본문
    private String defaultLocation;           // 기본 위치
    private String flexibleLocation;          // 유동적 위치

    private int desiredHourlyRate;            // 희망 시급
    private String serviceCategory;           // 서비스 카테고리
    private String postType;                  // POST 타입

    private int likes;                        // 좋아요 수
    private int chatCount;                    // 채팅 수

    private String thumbnailImageUrl;         // 썸네일 이미지 URL
    private List<String> detailImageUrls;     // 상세 이미지 URL 리스트

    private Long memberId;                    // 작성자 ID
    private String nickname;                  // 작성자 닉네임
    private String profileImageUrl;           // 작성자 프로필 이미지 URL

    private LocalDateTime regDate;            // 등록일
    private LocalDateTime modDate;            // 수정일
}

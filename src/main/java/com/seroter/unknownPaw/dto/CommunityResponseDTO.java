package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Community;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityResponseDTO {

    private Long postId;                   // 게시글 ID
    private String title;                  // 제목
    private String content;                // 내용
    private String defaultLocation;        // 기본 위치
    private String flexibleLocation;       // 유동적인 위치
    private int desiredHourlyRate;         // 희망 시급
    private CommunityCategory communityCategory; // 커뮤니티 카테고리 (Enum)
    private int likes;                     // 좋아요 수
    private int chatCount;                 // 채팅 수
    private String memberName;             // 작성자 이름

    // Community 엔티티를 받아서 필요한 필드를 설정하는 생성자
    public CommunityResponseDTO(Community community) {
        this.postId = community.getPostId();
        this.title = community.getTitle();
        this.content = community.getContent();
        this.defaultLocation = community.getDefaultLocation();
        this.flexibleLocation = community.getFlexibleLocation();
        this.desiredHourlyRate = community.getDesiredHourlyRate();
        this.communityCategory = community.getCommunityCategory(); // Enum 필드
        this.likes = community.getLikes();
        this.chatCount = community.getChatCount();
        this.memberName = community.getMember().getName(); // 작성자 정보는 member에서 가져옴
    }
}

package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Community;
import com.seroter.unknownPaw.entity.CommunityImage;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponseDTO {

    private Long communityId;
    private String title;
    private String content;
    private int likes;
    private int commentCount;
    private String authorName;   // 작성자 이름
    private String authorNickname;  // 작성자 닉네임
    private String authorProfileImage;  // 작성자 프로필 이미지
    private CommunityCategory communityCategory;
    private LocalDateTime regDate;
    private List<String> communityImages; // 커뮤니티 이미지 URL 목록

    // 기본 생성자 추가
    public CommunityResponseDTO(Long communityId, String title, String content, int likes, int commentCount,
                                String authorName, String authorNickname, String authorProfileImage,
                                CommunityCategory communityCategory, LocalDateTime regDate, List<String> communityImages) {
        this.communityId = communityId;
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.commentCount = commentCount;
        this.authorName = authorName;
        this.authorNickname = authorNickname;
        this.authorProfileImage = authorProfileImage;
        this.communityCategory = communityCategory;
        this.regDate = regDate;
        this.communityImages = communityImages;
    }

    // Community -> CommunityResponseDTO 변환
    public static CommunityResponseDTO fromEntity(Community community) {
        List<String> images = community.getCommunityImages().stream()
                .map(CommunityImage::getCommunityImageUrl)
                .collect(Collectors.toList());

        return CommunityResponseDTO.builder()
                .communityId(community.getCommunityId())
                .title(community.getTitle())
                .content(community.getContent())
                .likes(community.getLikes())
                .commentCount(community.getComments().size())  // 댓글 수로 변경
                .authorName(community.getMember().getName())
                .authorNickname(community.getMember().getNickname())
                .authorProfileImage(community.getMember().getProfileImagePath())
                .communityCategory(community.getCommunityCategory())
                .regDate(community.getRegDate())
                .communityImages(images)
                .build();
    }
}

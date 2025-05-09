package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long id;              // 댓글 ID
    private Long communityId;     // 소속 커뮤니티 게시글 ID
    private Long memberId;        // 작성자 ID
    private String nickname;      // 작성자 닉네임
    private String profileImageUrl; // 작성자 프로필 이미지

    private String content;       // 댓글 내용
    private LocalDateTime createdAt; // 작성 시간

    // Comment 엔티티를 받아서 DTO를 초기화하는 생성자
    public CommentDTO(Comment comment) {
        this.id = comment.getCommentId();
        this.communityId = comment.getCommunity().getPostId();
        this.memberId = comment.getMember().getMid();
        this.nickname = comment.getMember().getNickname();
        this.profileImageUrl = comment.getMember().getProfileImagePath();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}

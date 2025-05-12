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

    private Long commentId;       // 댓글 ID (필드명을 명확하게 수정)
    private Long communityId;     // 소속 커뮤니티 게시글 ID
    private Long memberId;        // 작성자 ID
    private String nickname;      // 작성자 닉네임
    private String profileImageUrl; // 작성자 프로필 이미지 URL

    private String content;       // 댓글 내용
    private LocalDateTime createdAt; // 댓글 작성 시간

    // Comment 엔티티를 받아서 DTO를 초기화하는 생성자
    public CommentDTO(Comment comment) {
        this.commentId = comment.getCommentId();  // 댓글 ID
        this.communityId = comment.getCommunity().getCommunityId();  // Community 엔티티에서 communityId 가져오기
        this.memberId = comment.getMember().getMid();  // 작성자 ID
        this.nickname = comment.getMember().getNickname();  // 작성자 닉네임
        this.profileImageUrl = comment.getMember().getProfileImagePath();  // 작성자 프로필 이미지 URL
        this.content = comment.getContent();  // 댓글 내용
        this.createdAt = comment.getCreatedAt();  // 댓글 작성 시간
    }
}

package com.seroter.unknownPaw.dto;

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
}

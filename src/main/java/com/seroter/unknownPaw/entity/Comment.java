package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;  // 댓글 ID (필드명 수정)

    // 댓글 내용을 수정하는 메서드
    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;  // 댓글 내용

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 댓글 작성자

    // 커뮤니티 게시글 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;  // 댓글이 달린 커뮤니티 게시글

    private LocalDateTime createdAt;  // 댓글 작성 시간

    // 댓글 작성 시 createdAt 자동 설정
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();  // 댓글 생성 시 자동으로 현재 시간 설정
    }




}

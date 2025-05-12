package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글의 댓글 조회
    List<Comment> findByCommunity_CommunityId(Long communityId); // 수정된 쿼리

    // 댓글 ID로 댓글 조회
    Comment findByCommentId(Long commentId);
}

package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.dto.CommunityResponseDTO;
import com.seroter.unknownPaw.dto.CommentDTO;

import java.util.List;

public interface CommunityService {

    // ======== 커뮤니티 게시글 관련 ========

    // 커뮤니티 게시글 생성
    Long createCommunityPost(Long memberId, CommunityRequestDTO dto);

    // 커뮤니티 게시글 단건 조회
    CommunityResponseDTO getCommunityPost(Long postId);

    // 모든 커뮤니티 게시글 조회
    List<CommunityResponseDTO> getAllCommunityPosts();

    // 커뮤니티 게시글 수정
    void updateCommunityPost(Long postId, CommunityRequestDTO dto);

    // 커뮤니티 게시글 삭제
    void deleteCommunityPost(Long postId);


    // ======== 댓글(Comment) 관련 ========

    // 댓글 작성
    Long createComment(Long postId, Long memberId, String content);

    // 게시글의 모든 댓글 조회
    List<CommentDTO> getCommentsByPostId(Long postId);

    // 댓글 삭제
    void deleteComment(Long commentId, Long memberId);

    // 댓글 수정
    void updateComment(Long commentId, Long memberId, String newContent);
}

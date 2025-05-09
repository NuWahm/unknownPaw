package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.CommentDTO;
import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.dto.CommunityResponseDTO;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import com.seroter.unknownPaw.repository.CommentRepository;
import com.seroter.unknownPaw.repository.CommunityRepository;
import com.seroter.unknownPaw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    // ========== [게시글 등록] ==========
    @Transactional
    public Long createCommunityPost(Long memberId, CommunityRequestDTO dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // 게시글 엔티티 생성
        Community community = Community.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .serviceCategory(String.valueOf(ServiceCategory.valueOf(dto.getServiceCategory()))) // ServiceCategory로 변환
                .communityCategory(CommunityCategory.valueOf(dto.getCommunityCategory())) // CommunityCategory로 변환
                .desiredHourlyRate(dto.getDesiredHourlyRate())
                .likes(0)
                .chatCount(0)
                .defaultLocation(dto.getDefaultLocation())
                .flexibleLocation(dto.getFlexibleLocation())
                .member(member)
                .postType(String.valueOf(PostType.valueOf(dto.getPostType()))) // PostType으로 변환
                .build();

        // 게시글 저장
        Community savedCommunity = communityRepository.save(community);
        return savedCommunity.getPostId();
    }

    // ========== [게시글 단건 조회] ==========
    public CommunityResponseDTO getCommunityPost(Long postId) {
        Community community = communityRepository.findByPostId(postId);
        if (community == null) {
            throw new IllegalArgumentException("Post not found");
        }

        return new CommunityResponseDTO(community); // DTO에 필요한 정보 설정
    }

    // ========== [게시글 전체 조회] ==========
    public List<CommunityResponseDTO> getAllCommunityPosts() {
        List<Community> communities = communityRepository.findAllByOrderByRegDateDesc();
        return communities.stream()
                .map(CommunityResponseDTO::new)
                .collect(Collectors.toList());
    }

    // ========== [게시글 수정] ==========
    @Transactional
    public void updateCommunityPost(Long postId, CommunityRequestDTO dto) {
        Community community = communityRepository.findByPostId(postId);
        if (community == null) {
            throw new IllegalArgumentException("Post not found");
        }

        // 수정된 값으로 게시글 업데이트
        community.modify(dto); // Community 엔티티 내에서 수정 메서드를 호출
    }

    // ========== [게시글 삭제] ==========
    @Transactional
    public void deleteCommunityPost(Long postId) {
        Community community = communityRepository.findByPostId(postId);
        if (community == null) {
            throw new IllegalArgumentException("Post not found");
        }

        communityRepository.delete(community); // 삭제
    }

    // ========== [댓글 작성] ==========
    @Transactional
    public Long createComment(Long postId, Long memberId, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        Community community = communityRepository.findByPostId(postId);
        if (community == null) {
            throw new IllegalArgumentException("Post not found");
        }

        // 댓글 엔티티 생성
        Comment comment = Comment.builder()
                .content(content)
                .member(member)
                .community(community)
                .build();

        // 댓글 저장
        commentRepository.save(comment);
        return comment.getCommentId();
    }

    // ========== [댓글 조회] ==========
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByCommunity_PostId(postId); // 수정된 쿼리
        return comments.stream()
                .map(comment -> new CommentDTO(comment))
                .collect(Collectors.toList());
    }

    // ========== [댓글 수정] ==========
    @Transactional
    public void updateComment(Long commentId, Long memberId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // 작성자가 맞는지 확인
        if (!comment.getMember().getMid().equals(memberId)) {
            throw new IllegalArgumentException("You can only update your own comment");
        }

        comment.setContent(newContent); // 내용 수정
    }

    // ========== [댓글 삭제] ==========
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // 작성자가 맞는지 확인
        if (!comment.getMember().getMid().equals(memberId)) {
            throw new IllegalArgumentException("You can only delete your own comment");
        }

        commentRepository.delete(comment); // 삭제
    }
}

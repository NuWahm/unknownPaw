package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.CommentDTO;
import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.dto.CommunityResponseDTO;
import com.seroter.unknownPaw.entity.Comment;
import com.seroter.unknownPaw.entity.Community;
import com.seroter.unknownPaw.entity.CommunityImage;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.repository.CommentRepository;
import com.seroter.unknownPaw.repository.CommunityImageRepository;
import com.seroter.unknownPaw.repository.CommunityRepository;
import com.seroter.unknownPaw.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    private final CommunityImageRepository communityImageRepository;

    // ========== [게시글 등록] ==========
    @Transactional
    public Long createCommunityPost(Long memberId, CommunityRequestDTO communityRequestDTO) {
        // 회원 ID로 회원 조회
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // 게시글 카테고리 변환 (안전하게 변환)
        CommunityCategory communityCategory = CommunityCategory.fromString(String.valueOf(communityRequestDTO.getCommunityCategory()));

        // 게시글 엔티티 생성
        Community community = Community.builder()
            .title(communityRequestDTO.getTitle())
            .content(communityRequestDTO.getContent())
            .communityCategory(communityCategory) // 문자열 → Enum
            .likes(0)
            .member(member)  // 게시글 작성자 설정
            .regDate(null)  // @PrePersist로 자동 설정

            .build();

        // 게시글 저장
        Community savedCommunity = communityRepository.save(community);
        return savedCommunity.getCommunityId();
    }

    // ========== [게시글 단건 조회] ==========
    public CommunityResponseDTO getCommunityPost(Long communityId) {
        Community community = communityRepository.findByCommunityId(communityId);
        if (community == null) {
            throw new IllegalArgumentException("Post not found");
        }

        // CommunityResponseDTO 생성 시, fromEntity 사용
        return CommunityResponseDTO.fromEntity(community);
    }

    // ========== [게시글 전체 조회] ==========
    public List<CommunityResponseDTO> getAllCommunityPosts() {
        List<Community> communities = communityRepository.findAllByOrderByRegDateDesc();
        return communities.stream()

            .map(CommunityResponseDTO::fromEntity)  // fromEntity 메서드를 이용해 변환
            .collect(Collectors.toList());
    }

    // ========== [게시글 수정] ==========
    @Transactional
    public void updateCommunityPost(Long communityId, CommunityRequestDTO dto) {
        Community community = communityRepository.findByCommunityId(communityId);

        if (community == null) {
            throw new IllegalArgumentException("Post not found");
        }

        // 엔티티 수정 메서드 호출
        community.modify(dto);

    }

    // ========== [게시글 삭제] ==========
    @Transactional

    public void deleteCommunityPost(Long communityId) {
        Community community = communityRepository.findByCommunityId(communityId);
        if (community == null) {
            throw new IllegalArgumentException("Post not found");
        }

        communityRepository.delete(community);

    }

    // ========== [댓글 작성] ==========
    @Transactional

    public Long createComment(Long communityId, Long memberId, String content) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        Community community = communityRepository.findByCommunityId(communityId);

        if (community == null) {
            throw new IllegalArgumentException("Post not found");
        }


        // 댓글 엔티티 생성 (생성 시간 수동 설정)


        Comment comment = Comment.builder()
            .content(content)
            .member(member)
            .community(community)
            .createdAt(LocalDateTime.now())
            .build();

        commentRepository.save(comment);
        return comment.getCommentId();
    }

    // ========== [댓글 조회] ==========

    public List<CommentDTO> getCommentsByCommunityId(Long communityId) {
        List<Comment> comments = commentRepository.findByCommunity_CommunityId(communityId);
        return comments.stream()
            .map(CommentDTO::new)
            .collect(Collectors.toList());
    }

    // ========== [댓글 수정] ==========
    @Transactional
    public void updateComment(Long commentId, Long memberId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!comment.getMember().getMid().equals(memberId)) {
            throw new IllegalArgumentException("You can only update your own comment");
        }


        comment.setContent(newContent);

    }

    // ========== [댓글 삭제] ==========
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getMember().getMid().equals(memberId)) {
            throw new IllegalArgumentException("You can only delete your own comment");
        }


        commentRepository.delete(comment);
    }

    // 커뮤니티 게시글에 이미지를 추가하는 메서드
    @Transactional
    public void addImagesToCommunity(Long communityId, List<String> imageUrls) {
        Community community = communityRepository.findByCommunityId(communityId);
        if (community == null) {
            throw new IllegalArgumentException("Community post not found");
        }

        // 이미지 추가
        for (String imageUrl : imageUrls) {
            CommunityImage image = CommunityImage.builder()
                .communityImageUrl(imageUrl)
                .communityIsThumbnail(false)  // 기본적으로 썸네일은 아니라고 가정
                .community(community)  // 해당 커뮤니티 게시글과 연결
                .build();
            communityImageRepository.save(image);  // 이미지 저장
        }
    }

    // 특정 커뮤니티 게시글에 속한 이미지들 조회
    public List<CommunityImage> getCommunityImages(Long communityId) {
        return communityImageRepository.findByCommunity_CommunityId(communityId);
    }

    // 커뮤니티 게시글의 썸네일 이미지 조회
    public CommunityImage getThumbnailImage(Long communityId) {
        return communityImageRepository.findByCommunity_CommunityId(communityId)
            .stream()
            .filter(CommunityImage::isCommunityIsThumbnail)  // 썸네일 이미지 필터링
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Thumbnail image not found"));
    }

    // ========== [댓글 ID로 댓글 조회] ==========
    public Comment getCommentById(Long commentId) {
        return commentRepository.findByCommentId(commentId);
//=======
//        commentRepository.delete(comment); // 삭제

    }
}
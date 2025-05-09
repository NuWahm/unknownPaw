package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.dto.CommunityResponseDTO;
import com.seroter.unknownPaw.dto.CommentDTO;
import com.seroter.unknownPaw.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // ========== [게시글 등록] ==========
    @PostMapping("/create/{memberId}")
    public ResponseEntity<Long> createPost(
            @PathVariable Long memberId,
            @RequestBody CommunityRequestDTO dto
    ) {
        Long postId = communityService.createCommunityPost(memberId, dto);
        return ResponseEntity.ok(postId);
    }

    // ========== [게시글 단건 조회] ==========
    @GetMapping("/{postId}")
    public ResponseEntity<CommunityResponseDTO> getPost(@PathVariable Long postId) {
        CommunityResponseDTO response = communityService.getCommunityPost(postId);
        return ResponseEntity.ok(response);
    }

    // ========== [게시글 전체 조회] ==========
    @GetMapping("/list")
    public ResponseEntity<List<CommunityResponseDTO>> getAllPosts() {
        List<CommunityResponseDTO> posts = communityService.getAllCommunityPosts();
        return ResponseEntity.ok(posts);
    }

    // ========== [게시글 수정] ==========
    @PutMapping("/update/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @RequestBody CommunityRequestDTO dto
    ) {
        communityService.updateCommunityPost(postId, dto);
        return ResponseEntity.ok().build();
    }

    // ========== [게시글 삭제] ==========
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        communityService.deleteCommunityPost(postId);
        return ResponseEntity.ok().build();
    }

    // ========== [댓글 작성] ==========
    @PostMapping("/{postId}/comment/{memberId}")
    public ResponseEntity<Long> createComment(
            @PathVariable Long postId,
            @PathVariable Long memberId,
            @RequestBody String content
    ) {
        Long commentId = communityService.createComment(postId, memberId, content);
        return ResponseEntity.ok(commentId);
    }

    // ========== [댓글 전체 조회] ==========
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long postId) {
        List<CommentDTO> comments = communityService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // ========== [댓글 수정] ==========
    @PutMapping("/comment/{commentId}/member/{memberId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @PathVariable Long memberId,
            @RequestBody String newContent
    ) {
        communityService.updateComment(commentId, memberId, newContent);
        return ResponseEntity.ok().build();
    }

    // ========== [댓글 삭제] ==========
    @DeleteMapping("/comment/{commentId}/member/{memberId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @PathVariable Long memberId
    ) {
        communityService.deleteComment(commentId, memberId);
        return ResponseEntity.ok().build();
    }
}

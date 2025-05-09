package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.dto.CommunityResponseDTO;
import com.seroter.unknownPaw.dto.CommentDTO;
import com.seroter.unknownPaw.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // ========== [게시글 등록] ==========
    @PostMapping("/post")
    public ResponseEntity<Long> createCommunityPost(@RequestParam Long memberId, @RequestBody CommunityRequestDTO dto) {
        Long postId = communityService.createCommunityPost(memberId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    // ========== [게시글 조회 (단건)] ==========
    @GetMapping("/post/{postId}")
    public ResponseEntity<CommunityResponseDTO> getCommunityPost(@PathVariable Long postId) {
        CommunityResponseDTO community = communityService.getCommunityPost(postId);
        return ResponseEntity.ok(community);
    }

    // ========== [게시글 조회 (전체)] ==========
    @GetMapping("/posts")
    public ResponseEntity<List<CommunityResponseDTO>> getAllCommunityPosts() {
        List<CommunityResponseDTO> communityList = communityService.getAllCommunityPosts();
        return ResponseEntity.ok(communityList);
    }

    // ========== [게시글 수정] ==========
    @PutMapping("/post/{postId}")
    public ResponseEntity<Void> updateCommunityPost(@PathVariable Long postId, @RequestBody CommunityRequestDTO dto) {
        communityService.updateCommunityPost(postId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ========== [게시글 삭제] ==========
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deleteCommunityPost(@PathVariable Long postId) {
        communityService.deleteCommunityPost(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ========== [댓글 작성] ==========
    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<Long> createComment(@PathVariable Long postId, @RequestParam Long memberId, @RequestBody String content) {
        Long commentId = communityService.createComment(postId, memberId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }

    // ========== [댓글 조회] ==========
    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDTO> comments = communityService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // ========== [댓글 수정] ==========
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId, @RequestParam Long memberId, @RequestBody String newContent) {
        communityService.updateComment(commentId, memberId, newContent);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ========== [댓글 삭제] ==========
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long memberId) {
        communityService.deleteComment(commentId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

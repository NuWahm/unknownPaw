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
    @PostMapping("/posts")
    public ResponseEntity<Long> createCommunityPost(@RequestParam Long memberId, @RequestBody CommunityRequestDTO dto) {
        // 게시글 등록 서비스 호출
        Long postId = communityService.createCommunityPost(memberId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);  // 생성된 게시글 ID 반환
    }

    // ========== [게시글 조회 (단건)] ==========
    @GetMapping("/posts/{postId}")
    public ResponseEntity<CommunityResponseDTO> getCommunityPost(@PathVariable Long postId) {
        // 단건 게시글 조회 서비스 호출
        CommunityResponseDTO community = communityService.getCommunityPost(postId);
        return ResponseEntity.ok(community);  // 조회된 게시글 반환
    }

    // ========== [게시글 조회 (전체)] ==========
    @GetMapping("/posts")
    public ResponseEntity<List<CommunityResponseDTO>> getAllCommunityPosts() {
        // 전체 게시글 조회 서비스 호출
        List<CommunityResponseDTO> communityList = communityService.getAllCommunityPosts();
        return ResponseEntity.ok(communityList);  // 전체 게시글 반환
    }

    // ========== [게시글 수정] ==========
    @PutMapping("/posts/{postId}")
    public ResponseEntity<Void> updateCommunityPost(@PathVariable Long postId, @RequestBody CommunityRequestDTO dto) {
        // 게시글 수정 서비스 호출
        communityService.updateCommunityPost(postId, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 수정된 상태로 응답
    }

    // ========== [게시글 삭제] ==========
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deleteCommunityPost(@PathVariable Long postId) {
        // 게시글 삭제 서비스 호출
        communityService.deleteCommunityPost(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 삭제 완료 응답
    }

    // ========== [댓글 작성] ==========
    @PostMapping("/posts/{postId}/comment")
    public ResponseEntity<Long> createComment(@PathVariable Long postId, @RequestParam Long memberId, @RequestBody CommentDTO commentDTO) {
        // 댓글 작성 서비스 호출
        Long commentId = communityService.createComment(postId, memberId, commentDTO.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);  // 생성된 댓글 ID 반환
    }

    // ========== [댓글 조회] ==========
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByPostId(@PathVariable Long postId) {
        // 게시글에 대한 댓글 조회 서비스 호출
        List<CommentDTO> comments = communityService.getCommentsByCommunityId(postId);
        return ResponseEntity.ok(comments);  // 조회된 댓글 목록 반환
    }

    // ========== [댓글 수정] ==========
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId, @RequestParam Long memberId, @RequestBody String newContent) {
        // 댓글 수정 서비스 호출
        communityService.updateComment(commentId, memberId, newContent);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 수정 완료 응답
    }

    // ========== [댓글 삭제] ==========
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long memberId) {
        // 댓글 삭제 서비스 호출
        communityService.deleteComment(commentId, memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 삭제 완료 응답
    }
}

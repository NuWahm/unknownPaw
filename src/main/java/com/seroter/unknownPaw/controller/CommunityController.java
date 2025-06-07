package com.seroter.unknownPaw.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seroter.unknownPaw.dto.CommentDTO;
import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.dto.CommunityResponseDTO;
import com.seroter.unknownPaw.entity.Community;
import com.seroter.unknownPaw.entity.Enum.ImageType;
import com.seroter.unknownPaw.repository.CommunityRepository;
import com.seroter.unknownPaw.service.CommunityService;
import com.seroter.unknownPaw.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Log4j2
public class CommunityController {

    private final CommunityRepository communityRepository;
    private final CommunityService communityService;
    private final ImageService imageService;

    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createCommunityPostWithImage(
            @RequestParam Long memberId,
            @RequestPart("community") CommunityRequestDTO communityDTO, // "community"
            @RequestPart(value = "images", required = false) List<MultipartFile> images // "images"
    ) throws Exception {
        Long postId = communityService.createCommunityPost(memberId, communityDTO);

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                String imageUrl = imageService.saveImage(image, ImageType.COMMUNITY.name(), "community", postId, null);
                imageUrls.add(imageUrl);
            }
            communityService.addImagesToCommunity(postId, imageUrls);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }
    // ========== [게시글 조회 (단건)] ==========
    @GetMapping("/posts/{postId}")
    public ResponseEntity<CommunityResponseDTO> getCommunityPost(@PathVariable Long postId) {
        // 단건 게시글 조회 서비스 호출
        CommunityResponseDTO community = communityService.getCommunityPost(postId);
        return ResponseEntity.ok(community);  // 조회된 게시글 반환
    }

    @GetMapping("/posts")
    public ResponseEntity<List<CommunityResponseDTO>> getPosts(@RequestParam(required = false) String type) {
        List<CommunityResponseDTO> posts;

        if (type != null) {
            posts = communityService.getPostsByType(type.toUpperCase());
        } else {
            posts = communityService.getAllCommunityPosts();  // 전체 조회
        }

        return ResponseEntity.ok(posts);

    }


    // ========== [게시글 수정] ==========
    @PutMapping(value = "/posts/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // consume 타입 변경
    public ResponseEntity<CommunityResponseDTO> updateCommunityPost( // 반환 타입 변경
                                                                     @PathVariable Long postId,
                                                                     @RequestParam Long memberId, // 게시글 작성자 ID (권한 확인용)
                                                                     @RequestParam String title,
                                                                     @RequestParam String content,
                                                                     @RequestParam String communityCategory, // Enum이 아닌 String으로 받음
                                                                     @RequestParam("existingImageNamesJson") String existingImageNamesJson, // JSON 문자열로 받음
                                                                     @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages // 새 이미지 파일 목록
    ) {
        log.info("게시글 수정 요청 수신: postId={}, memberId={}, title={}, content={}, category={}, existingImagesJson={}",
            postId, memberId, title, content, communityCategory, existingImageNamesJson);
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<String> existingImageNames = mapper.readValue(existingImageNamesJson, new TypeReference<List<String>>() {});
            log.info("파싱된 기존 이미지 이름: {}", existingImageNames);

            // 서비스 계층으로 업데이트 로직 위임 (새로운 updateCommunityPost 메서드를 호출)
            CommunityResponseDTO updatedPost = communityService.updateCommunityPost(
                postId, memberId, title, content, communityCategory, existingImageNames, newImages
            );

            return ResponseEntity.ok(updatedPost); // 업데이트된 게시글 정보를 반환
        } catch (Exception e) {
            log.error("게시글 수정 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null); // 에러 응답
        }
    }

    // 이미지 삭제 엔드포인트 수정
    @DeleteMapping("/posts/{postId}/images/{imageName:.+}") // .+: 모든 문자 하나 이상, /를 포함한 경로 매칭 가능
    public ResponseEntity<Void> deleteCommunityImage(
        @PathVariable Long postId,
        @PathVariable String imageName) { // imageName은 "community/uuid_filename.jpg" 형태의 전체 경로로 전달됨

        log.info("이미지 삭제 요청 받음: postId={}, imageName={}", postId, imageName);

        // 이제 여기서 imageName을 분리할 필요가 없습니다.
        // CommunityService.removeCommunityImage가 이미 전체 경로를 받아 내부에서 처리합니다.
        try {
            communityService.removeCommunityImage(postId, imageName); // <-- 이 부분을 그대로 유지

            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        } catch (Exception e) {
            log.error("이미지 삭제 중 오류 발생: postId={}, imageName={}, 에러={}", postId, imageName, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
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

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            String imagePath = "community/" + imageName;
            Resource resource = imageService.loadImageAsResource(imagePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
    // community 좋아요 추가
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<Void> likeCommunity(@PathVariable Long id, @RequestParam Long memberId) {
        communityService.likePost(id, memberId);
        return ResponseEntity.ok().build();
    }
    // community 좋아요 취소
    @DeleteMapping("/posts/{id}/unlike")
    public ResponseEntity<Void> unlikeCommunity(@PathVariable Long id, @RequestParam Long memberId) {
        communityService.unlikePost(id, memberId);
        return ResponseEntity.ok().build();
    }

    // 커뮤니티 좋아요 누른글 불려오기
    @GetMapping("/posts/likes")
    public ResponseEntity<List<CommunityResponseDTO>> getLikedCommunityPosts(@RequestParam Long memberId) {
        List<Community> liked =communityRepository.findByLikedMemberId(memberId);
        List<CommunityResponseDTO> result = liked.stream().map(CommunityResponseDTO::fromEntity).toList();
        return ResponseEntity.ok(result);
    }
    // 좋아요 여부 확인 API
    @GetMapping("/posts/{id}/liked")
    public ResponseEntity<Map<String, Boolean>> isPostLiked(
            @PathVariable Long id,
            @RequestParam Long memberId) {
        boolean liked = communityService.isLikedByMember(id, memberId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("liked", liked);
        return ResponseEntity.ok(result);
    }
    // 커뮤니티 좋아요 카운트 불러오기
    @GetMapping("/posts/{id}/likeCount")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long id) {
        int count = communityService.getLikeCount(id);
        return ResponseEntity.ok(count);
    }
}



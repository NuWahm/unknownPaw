package com.seroter.unknownPaw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seroter.unknownPaw.dto.ModifyRequestDTO;
import com.seroter.unknownPaw.dto.PageRequestDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.Post;
import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.service.ImageService;
import com.seroter.unknownPaw.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Log4j2
public class PostController {

  private final PostService postService;
  private final ImageService imageService;
  private final ObjectMapper objectMapper;

  /* ---------------- 목록 ---------------- */
  @GetMapping("/{postType}/list")
  public ResponseEntity<?> list(
          @PathVariable String postType,
          PageRequestDTO pageRequestDTO,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String location,
          @RequestParam(required = false) String category

  ) {
    try {
      PostType pType = PostType.from(postType);
      System.out.println("pType list:" + postType);
      PageRequest pageRequest = PageRequest.of(pageRequestDTO.getPage(), pageRequestDTO.getSize());
      Page<PostDTO> result = postService.searchPosts(
              postType,     // enum → String
              keyword,
              location,
              category,
              pageRequestDTO.getPageable()

      );
      return ResponseEntity.ok(result);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("유효하지 않은 게시글 타입입니다.");
    }
  }

  /* ---------------- 상세 ---------------- */
  @GetMapping("/{postType}/read/{postId}")
  public ResponseEntity<?> read(
          @PathVariable String postType,
          @PathVariable Long postId

  ) {
    // 콘솔로 받은 값 확인
    System.out.println("Front에서 받은 postType: " + postType);
    PostType pType;
    try {
      pType = PostType.from(postType);
      System.out.println("변환된 PostType Enum 값: " + pType); // 변환 성공 시 Enum 값
    } catch (IllegalArgumentException e) {
      // 유효하지 않은 postType 문자열인 경우 400 Bad Request 응답 반환
      log.error("Invalid post type received: {}", postType, e);
      return ResponseEntity.badRequest().body("유효하지 않은 게시글 타입입니다.");
    }

    PostDTO dto = postService.get(pType.name(), postId);
    return ResponseEntity.ok(dto);
  }

  /* ---------------- 등록 ---------------- */
  @PostMapping("/{postType}/register")
  public ResponseEntity<?> register(
          @PathVariable String postType,
          @RequestBody PostDTO postDTO,
          @RequestParam Long memberId
  ) {
    log.info("받은 카테고리: {}", postDTO.getServiceCategory());

    PostType enumPostType;
    try {
      enumPostType = PostType.from(postType);      // <-- 여기
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid postType: "+postType);
    }
    Long newId = postService.register(enumPostType.name(), postDTO, memberId);
    return ResponseEntity.ok(Map.of("postId", newId));
  }
  /* ---------------- 수정 ---------------- */
  @PutMapping("/{postType}/modify")
  public ResponseEntity<?> modify(
          @PathVariable String postType, // 👈 이렇게!
          @RequestBody ModifyRequestDTO modifyRequestDTO
  ) {
    PostType enumPostType;
    try {
      enumPostType = PostType.from(postType); // 기존 방식 재활용
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid postType: " + postType);
    }
    postService.modify(enumPostType.name(), modifyRequestDTO.getPostDTO());
    return ResponseEntity.ok(Map.of(
            "msg", "수정 완료",
            "postId", modifyRequestDTO.getPostDTO().getPostId()
    ));
  }

  @PostMapping("/{postType}/registerWithImage")
  public ResponseEntity<?> registerWithImage(
          @PathVariable String postType,
          @RequestParam("post") String postJson,
          @RequestParam("file") MultipartFile file,
          @RequestParam Long memberId
  ) {
    try {
      PostDTO postDTO = objectMapper.readValue(postJson, PostDTO.class);

      // 1. 게시글 저장
      Long postId = postService.register(postType, postDTO, memberId);

      // 2. 이미지 저장 (postId 연결)
      Long petId = null;
      if (postType.equalsIgnoreCase("petowner") && postDTO.getPetId() != null) {
        petId = postDTO.getPetId();
      }
      imageService.saveImage(file, postType, postType, postId, petId);

      return ResponseEntity.ok(Map.of("postId", postId));
    } catch (Exception e) {
      log.error("글+이미지 등록 실패", e);
      return ResponseEntity.badRequest().body("등록 실패: " + e.getMessage());
    }
  }
  /* ---------------- 삭제 ---------------- */
  @DeleteMapping("/{postType}/delete/{postId}")
  public ResponseEntity<?> delete(
          @PathVariable PostType postType,
          @PathVariable Long postId
  ) {
    postService.remove(postType.name(), postId);
    return ResponseEntity.ok(Map.of(
            "msg", "삭제 완료",
            "postId", postId
    ));
  }
  // 최근 7일 이내 펫오너 게시글 랜덤 6개
  @GetMapping("/petowner/recent/random6")
  public List<PostDTO> getRecentRandomPetOwnerPosts() {
    return postService.getRandom6PetOwnerPosts();
  }

  // 최근 7일 이내 펫시터 게시글 랜덤 6개
  @GetMapping("/petsitter/recent/random6")
  public List<PostDTO> getRecentRandomPetSitterPosts() {
    return postService.getRandom6PetSitterPosts();
  }


  @GetMapping("/{postType}/{mid}")
  public ResponseEntity<?> getPostsByMember(
      @PathVariable String postType,
      @PathVariable Long mid
  ) {

    try {
      PostType pType = PostType.from(postType);

      List<PostDTO> posts = postService.getPostsByMember(pType, mid);
      return ResponseEntity.ok(posts);

    } catch (IllegalArgumentException e) {

      return ResponseEntity.badRequest().body("유효하지 않은 게시글 타입입니다: " + postType);
    }
  }

  // ❤️ 좋아요 등록
  @PostMapping("/likes/{postType}/{postId}")
  public ResponseEntity<String> likePost(@PathVariable PostType postType,
                                         @PathVariable Long postId,
                                         @RequestParam Long memberId) {
    postService.likePost(memberId, postId, postType);
    return ResponseEntity.ok("좋아요 완료");
  }

  // 💔 좋아요 취소
  @DeleteMapping("/likes/{postType}/{postId}")
  public ResponseEntity<String> unlikePost(@PathVariable PostType postType,
                                           @PathVariable Long postId,
                                           @RequestParam Long memberId) {
    postService.unlikePost(memberId, postId, postType);
    return ResponseEntity.ok("좋아요 취소 완료");
  }

  // 🧾 좋아요 누른 게시글 목록 조회
  @GetMapping("/likes/{postType}")
  public ResponseEntity<Set<PostDTO>> getLikedPosts(@PathVariable PostType postType,
                                                    @RequestParam Long memberId) {
    Set<PostDTO> dtoSet = postService.getLikedPostDTOs(memberId, postType);
    return ResponseEntity.ok(dtoSet);
  }


}
package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.ModifyRequestDTO;
import com.seroter.unknownPaw.dto.PageRequestDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.Post;
import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Log4j2
public class PostController {

  private final PostService postService;

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
      PageRequest pageRequest = PageRequest.of(pageRequestDTO.getPage() -1, pageRequestDTO.getSize());
      Page<? extends Post> result = postService.searchPosts(
          postType,     // enum → String
          keyword,
          location,
          category,
          pageRequestDTO.getPageable()

      );
      Page<PostDTO> dtoPage = result.map(PostDTO::fromEntity);
      return ResponseEntity.ok(dtoPage);
    } catch (IllegalArgumentException e) {
      return  ResponseEntity.badRequest().body("유효하지 않은 게시글 타입입니다.");
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
          @RequestParam Long mid
  ) {
    PostType enumPostType;
    try {
      enumPostType = PostType.valueOf(postType.toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid postType: " + postType);
    }
    Long newId = postService.register(enumPostType.name(), postDTO, mid);
    return ResponseEntity.ok(Map.of("postId", newId));
  }

  /* ---------------- 수정 ---------------- */
  @PutMapping("/{postType}/modify")
  public ResponseEntity<?> modify(
      @PathVariable PostType postType,
      @RequestBody ModifyRequestDTO modifyRequestDTO
  ) {
    postService.modify(postType.name(), modifyRequestDTO.getPostDTO());
    return ResponseEntity.ok(Map.of(
        "msg", "수정 완료",
        "postId", modifyRequestDTO.getPostDTO().getPostId()
    ));
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
}
package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.ModifyRequestDTO;
import com.seroter.unknownPaw.dto.PageRequestDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.Post;
import com.seroter.unknownPaw.entity.PostType;
import com.seroter.unknownPaw.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
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
          @PathVariable PostType postType,
          PageRequestDTO pageRequestDTO,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String location,
          @RequestParam(required = false) String category
  ) {
    Page<? extends Post> result = postService.searchPosts(
            postType.name(),     // enum → String
            keyword,
            location,
            category,
            pageRequestDTO.getPageable()

    );
    Page<PostDTO> dtoPage = result.map(PostDTO::fromEntity);
    return ResponseEntity.ok(dtoPage);
  }

  /* ---------------- 상세 ---------------- */
  @GetMapping("/{postType}/read/{postId}")
  public ResponseEntity<?> read(
          @PathVariable PostType postType,
          @PathVariable Long postId
  ) {
    PostDTO dto = postService.get(postType.name(), postId);
    return ResponseEntity.ok(dto);
  }

  /* ---------------- 등록 ---------------- */
  @PostMapping("/{postType}/register")
  public ResponseEntity<?> register(
          @PathVariable PostType postType,
          @RequestBody PostDTO postDTO,
          @RequestParam Long memberId
  ) {
    Long newId = postService.register(postType.name(), postDTO, memberId);
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
            "msg",    "수정 완료",
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
            "msg",    "삭제 완료",
            "postId", postId
    ));
  }
}

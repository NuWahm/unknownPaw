package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.ModifyRequestDTO;
import com.seroter.unknownPaw.dto.PageRequestDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.Post;
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

  // 📌 게시글 목록 조회 or 검색
  @GetMapping("/{role}/list")
  public ResponseEntity<?> list(
          @PathVariable String role,
          PageRequestDTO pageRequestDTO,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String location,
          @RequestParam(required = false) String category
  ) {
    Page<? extends Post> result = postService.searchPosts(
            role, keyword, location, category, pageRequestDTO.getPageable()
    );
    return ResponseEntity.ok(result);
  }

  // 📌 게시글 상세 조회
  @GetMapping("/{role}/read/{postId}")
  public ResponseEntity<?> read(@PathVariable String role, @PathVariable Long postId) {
    PostDTO postDTO = postService.get(role, postId);
    return ResponseEntity.ok(postDTO);
  }

  // 📌 게시글 등록
  @PostMapping("/{role}/register")
  public ResponseEntity<?> register(@PathVariable String role,
                                    @RequestBody PostDTO postDTO,
                                    @RequestParam Long memberId) {
    Long newId = postService.register(role, postDTO, memberId);
    return ResponseEntity.ok(Map.of("postId", newId));
  }

  // 📌 게시글 수정
  @PutMapping("/{role}/modify")
  public ResponseEntity<?> modify(@PathVariable String role,
                                  @RequestBody ModifyRequestDTO modifyRequestDTO) {
    postService.modify(role, modifyRequestDTO.getPostDTO());
    return ResponseEntity.ok(Map.of("msg", "수정 완료", "postId", modifyRequestDTO.getPostDTO().getPostId()));
  }

  // 📌 게시글 삭제
  @DeleteMapping("/{role}/delete/{postId}")
  public ResponseEntity<?> delete(@PathVariable String role, @PathVariable Long postId) {
    postService.remove(role, postId);
    return ResponseEntity.ok(Map.of("msg", "삭제 완료", "postId", postId));
  }
}

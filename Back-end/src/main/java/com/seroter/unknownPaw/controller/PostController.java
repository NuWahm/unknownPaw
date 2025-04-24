package com.seroter.unknownPaw.controller;


import com.seroter.unknownPaw.dto.ModifyRequestDTO;
import com.seroter.unknownPaw.dto.PageRequestDTO;
import com.seroter.unknownPaw.dto.PageResultDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.Post;
import com.seroter.unknownPaw.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Log4j2
public class PostController {

  private final PostService postService;


  // 📌 무한스크롤 방식으로 게시글 조회 (앱용)
  @GetMapping("/{role}/scroll")
  public ResponseEntity<?> scroll(
          @PathVariable String role,
          SpringDataJaxb.PageRequestDto pageRequestDTO,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String location,
          @RequestParam(required = false) String category
  ) {
    Page<PostDTO> result = postService.scrollPosts(role, keyword, location, category, pageRequestDTO.getPageable());
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
                                    @RequestParam Long mid) {

    Long newId = switch (role) {
      case "petOwner" -> postService.register(postDTO, memberId);
      case "petSitter" -> postService.register(postDTO, memberId);
      default -> throw new IllegalArgumentException("잘못된 역할입니다.");
    };

    return ResponseEntity.ok(Map.of("postId", newId));
  }

  // 📌 게시글 수정
  @PutMapping("/{role}/modify")
  public ResponseEntity<?> modify(@PathVariable String role,
                                  @RequestBody ModifyRequestDTO modifyRequestDTO) {
    PostDTO dto = modifyRequestDTO.getPostDTO();
    if ("petOwner".equals(role)) {
      postService.modify(dto);
    } else {
      postService.modify(dto);
    }
    return ResponseEntity.ok(Map.of("msg", "수정 완료", "postId", dto.getPostId()));
  }

  // 📌 게시글 삭제
  @DeleteMapping("/{role}/delete/{postId}")
  public ResponseEntity<?> delete(@PathVariable String role, @PathVariable Long postId) {
    postService.remove(role, postId);
    return ResponseEntity.ok(Map.of("msg", "삭제 완료", "postId", postId));
  }

  @GetMapping("/member/{memberId}/scroll")
  public ResponseEntity<PageResultDTO<PostDTO, Post>> getPostsByMemberWithScroll(
          @RequestParam("role") String role,
          @PathVariable Long memberId,
          Pageable pageable) {
    return ResponseEntity.ok(postService.getPostsByMemberWithScroll(role, memberId, pageable));
  }

  // 페이지네이션 (웹용)
  @GetMapping("/member/{memberId}/pagination")
  public ResponseEntity<PageResultDTO<PostDTO, Post>> getPostsByMemberWithPagination(
          @RequestParam("role") String role,
          @PathVariable Long memberId,
          Pageable pageable) {
    return ResponseEntity.ok(postService.getPostsByMemberWithPagination(role, memberId, pageable));
  }
}

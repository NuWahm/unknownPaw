package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.ModifyRequestDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.Post;
import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
      Pageable pageable,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) String category

  ) {
    try {
      PostType pType = PostType.from(postType);
      System.out.println("pType list:" + postType);

      Page<? extends Post> result = postService.searchPosts(
          postType,     // enum → String
          keyword,
          location,
          category,
          pageable


      // page 보정 (page가 1 미만일 경우 1로 설정)
      int pageNum = pageRequestDTO.getPage();
      if (pageNum < 1) {
        pageNum = 1;
      }

      PageRequest pageRequest = PageRequest.of(pageNum - 1, pageRequestDTO.getSize());

      Page<? extends Post> result = postService.searchPosts(
              postType,
              keyword,
              location,
              category,
              pageRequest

      );

      Page<PostDTO> dtoPage = result.map(PostDTO::fromEntity);
      return ResponseEntity.ok(dtoPage);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("유효하지 않은 게시글 타입입니다.");
    }
    // 정렬 추가
    catch (Exception e) {
      // 기타 예외 처리 로직 추가 (필요시)
      log.error("Error listing posts: {}", e.getMessage(), e); // 로깅 추가 (로거 선언 필요)
      return ResponseEntity.internalServerError().body("게시글 조회 중 오류가 발생했습니다.");
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

      @PathVariable PostType postType,
      @RequestBody PostDTO postDTO,
      @RequestParam Long memberId
  ) {
    Long newId = postService.register(PostType.valueOf(postType.name()), postDTO, memberId);
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

  // 최근 7일 이내 펫오너 게시글 랜덤 6개
  @GetMapping("/petowner/recent/random6")
  public ResponseEntity<?> getRecentRandomPetOwnerPosts() {
    try {
        List<PostDTO> posts = postService.getRandom6PetOwnerPosts();
        return ResponseEntity.ok(posts);
    } catch (Exception e) {
        log.error("펫오너 게시글 조회 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "게시글을 불러오는데 실패했습니다."));
    }
  }

  // 최근 7일 이내 펫시터 게시글 랜덤 6개
  @GetMapping("/petsitter/recent/random6")
  public ResponseEntity<?> getRecentRandomPetSitterPosts() {
    try {
        List<PostDTO> posts = postService.getRandom6PetSitterPosts();
        return ResponseEntity.ok(posts);
    } catch (Exception e) {
        log.error("펫시터 게시글 조회 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "게시글을 불러오는데 실패했습니다."));
    }
  }



  @GetMapping("/{postType}/{mid}")
  public ResponseEntity<?> getPostsByMember(
      @PathVariable String postType,
      @PathVariable Long mid
  ) {

    try {
      PostType pType = PostType.from(postType);

      List<PostDTO> posts = postService.getPostsByMember(pType.toString(), mid);
      return ResponseEntity.ok(posts);

    } catch (IllegalArgumentException e) {

      return ResponseEntity.badRequest().body("유효하지 않은 게시글 타입입니다: " + postType);
    }
  }


}


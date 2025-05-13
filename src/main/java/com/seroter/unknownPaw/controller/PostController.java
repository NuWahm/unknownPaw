package com.seroter.unknownPaw.controller;


import com.seroter.unknownPaw.dto.PageRequestDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.dto.ModifyRequestDTO;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.entity.Post;

import com.seroter.unknownPaw.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
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


  // 📌 게시글 목록 조회

  @GetMapping("/{type}/list")
  public ResponseEntity<?> list(
          @PathVariable String type,
          PageRequestDTO pageRequestDTO,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) String location,
          @RequestParam(required = false) String category
  ) {
    Page<? extends Post> result = postService.searchPosts(
            type, keyword, location, category, pageRequestDTO.getPageable()
    );
    return ResponseEntity.ok(result);
  }

  // 📌 게시글 상세 조회
  @GetMapping("/{type}/read/{postId}")
  public ResponseEntity<?> read(@PathVariable String type, @PathVariable Long postId) {
    PostDTO postDTO = postService.get(type, postId);
    return ResponseEntity.ok(postDTO);
  }

  // 📌 게시글 등록
  @PostMapping("/{type}/register")
  public ResponseEntity<?> register(@PathVariable String type,
                                    @RequestBody PostDTO postDTO,
                                    @RequestParam Long memberId) {
      Long newId = postService.register(type, postDTO, memberId);
      return ResponseEntity.ok(Map.of("postId", newId));
  }

  // 📌 게시글 수정
  @PutMapping("/{type}/modify")
  public ResponseEntity<?> modify(@PathVariable String type,
                                  @RequestBody ModifyRequestDTO modifyRequestDTO) {
    PostDTO dto = modifyRequestDTO.getPostDTO();
    postService.modify(type, dto);

      return ResponseEntity.ok(Map.of("msg", "수정 완료", "postId", dto.getPostId()));
  }

  // 📌 게시글 삭제
  @DeleteMapping("/{type}/delete/{postId}")
  public ResponseEntity<?> delete(@PathVariable String type, @PathVariable Long postId) {
    postService.remove(type, postId);
    return ResponseEntity.ok(Map.of("msg", "삭제 완료", "postId", postId));
  }



//  Main random 게시판 글 불러오기 주석처리
//  @GetMapping("/{type}/random")
//  public ResponseEntity<?> getRandomPost(@PathVariable String type) {
//    log.info(">>"+type+"random");
//    PostDTO dto = postService.getRandomPostByType(type);
//
//    if (dto == null || dto.getPostId() == -1L) {
//      return ResponseEntity.status(404).body(Map.of("message", "해당 역할에 대한 게시글이 없습니다."));
//    }
//
//    return ResponseEntity.ok(dto);
//  }


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






}


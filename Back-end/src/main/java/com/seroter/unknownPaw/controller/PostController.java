package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.dto.ModifyRequestDTO;
import com.seroter.unknownPaw.service.PetOwnerService;
import com.seroter.unknownPaw.service.PetSitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Log4j2
public class PostController {

    private final PetOwnerService petOwnerService;
    private final PetSitterService petSitterService;

    // 📌 게시글 목록 조회
    @GetMapping("/{role}/list")
    public ResponseEntity<?> list(@PathVariable String role, PageRequestDTO pageRequestDTO) {
        if ("petOwner".equals(role)) {
            return ResponseEntity.ok(petOwnerService.getList(pageRequestDTO));
        } else if ("petSitter".equals(role)) {
            return ResponseEntity.ok(petSitterService.getList(pageRequestDTO));
        }
        return ResponseEntity.badRequest().body("Invalid role");
    }

    // 📌 게시글 상세 조회
    @GetMapping("/{role}/read/{postId}")
    public ResponseEntity<?> read(@PathVariable String role, @PathVariable Long postId) {
        PostDTO postDTO = "petOwner".equals(role)
                ? petOwnerService.get(postId)
                : petSitterService.get(postId);
        return ResponseEntity.ok(postDTO);
    }

    // 📌 게시글 등록
    @PostMapping("/{role}/register")
    public ResponseEntity<?> register(@PathVariable String role,
                                      @RequestBody PostDTO postDTO,
                                      @RequestParam Long memberId) {
        Long newId = switch (role) {
            case "petOwner" -> petOwnerService.register(postDTO, memberId);
            case "petSitter" -> petSitterService.register(postDTO, memberId);
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
            petOwnerService.modify(dto);
        } else {
            petSitterService.modify(dto);
        }
        return ResponseEntity.ok(Map.of("msg", "수정 완료", "postId", dto.getPostId()));
    }

    // 📌 게시글 삭제
    @DeleteMapping("/{role}/delete/{postId}")
    public ResponseEntity<?> delete(@PathVariable String role, @PathVariable Long postId) {
        if ("petOwner".equals(role)) {
            petOwnerService.remove(postId);
        } else {
            petSitterService.remove(postId);
        }
        return ResponseEntity.ok(Map.of("msg", "삭제 완료", "postId", postId));
    }
}

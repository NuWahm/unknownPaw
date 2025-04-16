package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/image")
@Log4j2
public class PostImageController {

  private final ImageService imageService;

  private String role;
  String targetType = role;
 

  // 게시판 이미지 등록

  @PostMapping("/upload/{role}")
  public ResponseEntity<?> upload(@PathVariable String role,
                                  @RequestParam("file") MultipartFile file,
                                  @RequestParam("targetId") Long targetId) {
    try {
      if (!role.equals("petOwner") && !role.equals("petSitter")) {
        return ResponseEntity.badRequest().body("올바르지 않은 역할입니다.");
      }

      String fileName = imageService.saveImage(file, role, targetType, targetId);
      return ResponseEntity.ok(Map.of("fileName", fileName, "role", role));
    } catch (Exception e) {
      log.error("이미지 업로드 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패");
    }
  }


  // 게시판 이미지 수정

  @PostMapping("/replace/{role}")
  public ResponseEntity<?> replaceImage(@PathVariable String role,
                                        @RequestParam("oldFileName") String oldFileName,
                                        @RequestParam("file") MultipartFile newFile,
                                        @RequestParam("targetId") Long targetId) {
    try {
      String newFileName = imageService.replaceImage(newFile, role, oldFileName, targetType, targetId);
      return ResponseEntity.ok(Map.of("fileName", newFileName, "message", "교체 성공"));
    } catch (Exception e) {
      log.error("이미지 교체 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("교체 실패");
    }
  }


  // 게시판 이미지 삭제

  @DeleteMapping("/{role}/{fileName}")
  public ResponseEntity<?> deleteImage(@PathVariable String role, @PathVariable String fileName) {
    try {
      boolean deleted = imageService.deleteImage(role, fileName);
      return deleted
          ? ResponseEntity.ok("삭제 성공")
          : ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일 없음");
    } catch (Exception e) {
      log.error("삭제 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
    }
  }
}

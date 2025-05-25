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


  // 게시판 이미지 등록

  @PostMapping("/upload/{postType}")
  public ResponseEntity<?> upload(@PathVariable String postType,
                                  @RequestParam("file") MultipartFile file,
                                  @RequestParam("targetId") Long targetId) {
    try {
      if (!postType.equals("petOwner") && !postType.equals("petSitter")) {
        return ResponseEntity.badRequest().body("올바르지 않은 역할입니다.");
      }

      String fileName = imageService.saveImage(file, postType, postType, targetId, null);
      return ResponseEntity.ok(Map.of("fileName", fileName, "role", postType));
    } catch (Exception e) {
      log.error("이미지 업로드 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패");
    }
  }


  // 게시판 이미지 수정

  @PostMapping("/replace/{postType}")
  public ResponseEntity<?> replaceImage(@PathVariable String postType,
                                        @RequestParam("oldFileName") String oldFileName,
                                        @RequestParam("file") MultipartFile newFile,
                                        @RequestParam("targetId") Long targetId) {
    try {
      String newFileName = imageService.replaceImage(newFile, postType, oldFileName, postType, targetId, null);
      return ResponseEntity.ok(Map.of("fileName", newFileName, "message", "교체 성공"));
    } catch (Exception e) {
      log.error("이미지 교체 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("교체 실패");
    }
  }


  // 게시판 이미지 삭제

  @DeleteMapping("/{postType}/{fileName}")
  public ResponseEntity<?> deleteImage(@PathVariable String postType, @PathVariable String fileName) {
    try {
      boolean deleted = imageService.deleteImage(postType, fileName);
      return deleted
              ? ResponseEntity.ok("삭제 성공")
              : ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일 없음");
    } catch (Exception e) {
      log.error("삭제 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
    }
  }

  // 지도 이미지 업로드
  @PostMapping("/upload/{postType}/map")
  public ResponseEntity<?> uploadMapImage(@PathVariable String postType,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam("targetId") Long targetId) {
    try {
      String targetType = postType + "Map"; // "petOwnerMap" or "petSitterMap"
      String fileName = imageService.saveImage(file, postType, targetType, targetId, null);
      return ResponseEntity.ok(Map.of("fileName", fileName, "role", postType, "type", "map"));
    } catch (Exception e) {
      log.error("지도 이미지 업로드 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("지도 이미지 업로드 실패");
    }
  }

  //지도 이미지 교체
  @PostMapping("/replace/{postType}/map")
  public ResponseEntity<?> replaceMapImage(@PathVariable String postType,
                                           @RequestParam("oldFileName") String oldFileName,
                                           @RequestParam("file") MultipartFile newFile,
                                           @RequestParam("targetId") Long targetId) {
    try {
      String targetType = postType + "_map";
      String newFileName = imageService.replaceImage(newFile, postType, oldFileName, targetType, targetId, null);
      return ResponseEntity.ok(Map.of("fileName", newFileName, "message", "지도 이미지 교체 성공"));
    } catch (Exception e) {
      log.error("지도 이미지 교체 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("교체 실패");
    }
  }

}

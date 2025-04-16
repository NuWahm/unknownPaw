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
@RequestMapping("/api/pets/image")
@Log4j2
public class PetImageController {

  private final ImageService imageService;


  // 펫 이미지 등록

  @PostMapping("/upload")
  public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam("targetId") Long petId) {
    try {
      String fileName = imageService.saveImage(file, "pet", "pet", petId);
      return ResponseEntity.ok(Map.of("fileName", fileName));
    } catch (Exception e) {
      log.error("펫 이미지 업로드 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업로드 실패");
    }
  }


  // 펫 이미지 수정

  @PostMapping("/replace")
  public ResponseEntity<?> replaceImage(@RequestParam("oldFileName") String oldFileName,
                                        @RequestParam("file") MultipartFile newFile,
                                        @RequestParam("targetId") Long petId) {
    try {
      String newFileName = imageService.replaceImage(newFile, "pet", oldFileName, "pet", petId);
      return ResponseEntity.ok(Map.of("fileName", newFileName, "message", "교체 성공"));
    } catch (Exception e) {
      log.error("이미지 교체 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("교체 실패");
    }
  }


  // 펫 이미지 삭제

  @DeleteMapping("/{fileName}")
  public ResponseEntity<?> deleteImage(@PathVariable String fileName) {
    try {
      boolean deleted = imageService.deleteImage("pet", fileName);
      return deleted
          ? ResponseEntity.ok("삭제 성공")
          : ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일 없음");
    } catch (Exception e) {
      log.error("삭제 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
    }
  }
}

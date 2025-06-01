package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Image;
import com.seroter.unknownPaw.repository.ImageRepository;
import com.seroter.unknownPaw.service.ImageService;
import com.seroter.unknownPaw.service.PetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/pets/image")
@RequiredArgsConstructor
@Log4j2
public class PetImageController {

  private final ImageService imageService;
  private final PetService    petService;
  private final ImageRepository imageRepository;

  /**
   * 1) 파일 저장
   * 2) DB Pet.imagePath 갱신
   * 3) 갱신된 PetDTO 리턴
   */
  @PostMapping("/upload")
  public ResponseEntity<PetDTO> upload(
          @RequestParam("file") MultipartFile file,
          @RequestParam("targetId") Long petId
  ) {
    try {
      // 1. 이미지 저장 (ImageService)
      String savedPath = imageService.saveImage(file, "pet", "pet", petId, petId);
      
      // 2. 이미지 엔티티 조회
      Image savedImage = imageRepository.findByPath(savedPath)
              .orElseThrow(() -> new EntityNotFoundException("저장된 이미지를 찾을 수 없습니다: " + savedPath));
      
      // 3. Pet 엔티티 업데이트 (이미지 경로 + imgId)
      PetDTO updatedPet = petService.updatePetImagePath(petId, savedImage);
      
      return ResponseEntity.ok(updatedPet);
    } catch (Exception e) {
      log.error("🐾 펫 이미지 업로드 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /** 기존 이미지 교체 */
  @PostMapping("/replace")
  public ResponseEntity<Map<String,Object>> replaceImage(
          @RequestParam("oldFileName") String oldFileName,
          @RequestParam("file") MultipartFile newFile,
          @RequestParam("targetId") Long petId
  ) {
    try {
      String newFileName = imageService.replaceImage(
              newFile,
              "pet",
              oldFileName,
              "pet",
              petId,
              petId
      );
      return ResponseEntity.ok(Map.of(
              "fileName", newFileName,
              "message",  "교체 성공"
      ));
    } catch (Exception e) {
      log.error("🐾 펫 이미지 교체 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("error", "교체 실패"));
    }
  }

  /** 이미지 삭제 */
  @DeleteMapping("/{fileName}")
  public ResponseEntity<Map<String,String>> deleteImage(
          @PathVariable String fileName
  ) {
    try {
      boolean deleted = imageService.deleteImage("pet", fileName);
      if (deleted) {
        return ResponseEntity.ok(Map.of("message", "삭제 성공"));
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "파일 없음"));
      }
    } catch (Exception e) {
      log.error("🐾 펫 이미지 삭제 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("error", "삭제 실패"));
    }
  }

  // 이미지 조회 엔드포인트 추가
  @GetMapping("/{petId}/{fileName}")
  public ResponseEntity<Resource> getImage(
          @PathVariable Long petId,
          @PathVariable String fileName) {
    try {
      String imagePath = "pet/" + petId + "/" + fileName;
      Resource resource = imageService.loadImageAsResource(imagePath);
      return ResponseEntity.ok()
              .contentType(MediaType.IMAGE_JPEG)
              .body(resource);
    } catch (Exception e) {
      log.error("이미지 로드 실패", e);
      return ResponseEntity.notFound().build();
    }
  }
}
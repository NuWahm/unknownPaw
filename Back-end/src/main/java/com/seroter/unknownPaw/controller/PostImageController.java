// ✅ PostImageController.java
package com.seroter.unknownPaw.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/image")
@Log4j2
public class PostImageController {

    @Value("${com.example.upload.path}")
    private String uploadPath;

    // ✅ 이미지 업로드
    @PostMapping("/upload/{role}")
    public ResponseEntity<?> upload(@PathVariable String role, @RequestParam("file") MultipartFile file) {
        try {
            if (!role.equals("petOwner") && !role.equals("petSitter")) {
                return ResponseEntity.badRequest().body("올바르지 않은 역할(role)입니다.");
            }

            String originalName = file.getOriginalFilename();
            String saveName = UUID.randomUUID() + "_" + originalName;

            File roleDir = new File(uploadPath, role);
            if (!roleDir.exists()) roleDir.mkdirs();

            File saveFile = new File(roleDir, saveName);
            file.transferTo(saveFile);

            return ResponseEntity.ok(Map.of("fileName", saveName, "role", role));
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }

    // ✅ 이미지 교체
    @PostMapping("/replace/{role}")
    public ResponseEntity<?> replaceImage(
            @PathVariable String role,
            @RequestParam("oldFileName") String oldFileName,
            @RequestParam("file") MultipartFile newFile) {
        try {
            File oldFile = new File(uploadPath + "/" + role, oldFileName);
            if (oldFile.exists()) oldFile.delete();

            String originalName = newFile.getOriginalFilename();
            String saveName = UUID.randomUUID() + "_" + originalName;

            File roleDir = new File(uploadPath, role);
            if (!roleDir.exists()) roleDir.mkdirs();

            File saveFile = new File(roleDir, saveName);
            newFile.transferTo(saveFile);

            return ResponseEntity.ok(Map.of("fileName", saveName, "message", "이미지 교체 성공"));
        } catch (Exception e) {
            log.error("이미지 교체 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 교체 실패");
        }
    }

    // ✅ 이미지 삭제
    @DeleteMapping("/{role}/{fileName}")
    public ResponseEntity<?> deleteImage(@PathVariable String role, @PathVariable String fileName) {
        try {
            File file = new File(uploadPath + "/" + role, fileName);
            if (file.exists()) {
                file.delete();
                return ResponseEntity.ok("삭제 성공");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일이 존재하지 않습니다.");
            }
        } catch (Exception e) {
            log.error("이미지 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }
}

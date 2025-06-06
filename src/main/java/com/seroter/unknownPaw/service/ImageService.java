package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.config.UploadPathProvider;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImageService {

  private final ImageRepository imageRepository;
  private final PetRepository petRepository;
  private final UploadPathProvider uploadPathProvider;
  private final PetOwnerRepository petOwnerRepository;
  private final PetSitterRepository petSitterRepository;
  private final MemberRepository memberRepository;

  /**
   * application.yml ‑> com.seroter.upload.path
   */
  @Value("${com.seroter.upload.path}")
  private String uploadRoot;

  /**
   * 모든 이미지 저장/교체 공용 메서드. <br/>
   * (member 프로필일 경우 '이전 이미지' 레코드와 파일을 전부 삭제하고 새로 1장만 남긴다)
   */
  @Transactional
  public String saveImage(MultipartFile file,
                          String imageType,
                          String targetType,
                          Long targetId,
                          Long petId) throws Exception {

    /* ---------- 1. 저장 디렉터리 준비 ---------- */
    String relDir   = imageType.equals("pet")         // uploadRoot/pet/57
            ? imageType + File.separator + petId
            : imageType;                                // uploadRoot/member …
    File dir = new File(uploadRoot, relDir);
    if (!dir.exists() && !dir.mkdirs()) {
      throw new RuntimeException("폴더 생성 실패: " + dir.getAbsolutePath());
    }

    /* ---------- 2. 파일 이름 생성 및 저장 ---------- */
    String originalName = file.getOriginalFilename();
    String uuid = UUID.randomUUID().toString();
    String saveName = uuid + "_" + originalName;

    File saveFile = new File(dir, saveName);
    file.transferTo(saveFile);                                   // 원본

    // 썸네일
    String thumbName = "thumb_" + saveName;
    File thumbFile = new File(dir, thumbName);
    Thumbnails.of(saveFile).size(400, 600).toFile(thumbFile);

    /* ---------- 3. imageType 문자열 → 정수 코드 ---------- */
    int imgType;
    try {
      imgType = Integer.parseInt(imageType);
    } catch (NumberFormatException e) {
      imgType = switch (imageType.toLowerCase()) {
        case "member" -> 1;
        case "pet" -> 2;
        case "petowner", "pet_owner",
             "petsitter", "pet_sitter",
             "post" -> 3;
        case "community" -> 4;
        default -> 0;
      };
    }

    String type = targetType.toLowerCase();

    /* ---------- 4. (member 프로필인 경우) 기존 이미지/파일 삭제 ---------- */
    if (type.equals("member")) {
      // 기존 파일 삭제
      List<Image> olds = imageRepository.findProfileImagesByMemberMid(targetId);
      for (Image old : olds) {
        new File(uploadRoot, old.getPath()).delete();
        if (old.getThumbnailPath() != null)
          new File(uploadRoot, old.getThumbnailPath()).delete();
      }
      // DB 삭제
      imageRepository.deleteProfileImagesByMemberId(targetId);
    }

    /* ---------- 5. Image 엔티티 생성 ---------- */
    Image image = switch (type) {

      case "member" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(imageType + "/" + saveName)
              .thumbnailPath(imageType + "/" + thumbName)
              .imageType(imgType)
              .member(Member.builder().mid(targetId).build())
              .build();

      case "pet" -> {
        String relOrigin = "pet/" + saveName;
        String relThumb  = "pet/thumb_" + saveName;
        yield Image.builder()
                .uuid(uuid)
                .profileImg(originalName)
                .path(relOrigin)
                .thumbnailPath(relThumb)
                .imageType(imgType)
                .pet(Pet.builder().petId(targetId).build())
                .build();
      }

      case "petowner", "pet_owner" -> {
        Optional<PetOwner> poOpt = petOwnerRepository.findById(targetId);
        PetOwner post = poOpt.orElseGet(() -> {
          PetOwner tmp = new PetOwner();
          tmp.setPostId(targetId);
          return tmp;
        });
        imageRepository.deleteByPostId(targetId);   // 이전 이미지 제거
        yield Image.builder()
                .uuid(uuid)
                .profileImg(originalName)
                .path(imageType + "/" + saveName)
                .thumbnailPath(imageType + "/" + thumbName)
                .imageType(imgType)
                .post(post)
                .member(poOpt.map(PetOwner::getMember).orElse(null))
                .pet(poOpt.map(PetOwner::getPet).orElse(null))
                .build();
      }

      case "petsitter", "pet_sitter" -> {
        Optional<PetSitter> psOpt = petSitterRepository.findById(targetId);
        PetSitter post = psOpt.orElseGet(() -> {
          PetSitter tmp = new PetSitter();
          tmp.setPostId(targetId);
          return tmp;
        });
        imageRepository.deleteByPostId(targetId);
        yield Image.builder()
                .uuid(uuid)
                .profileImg(originalName)
                .path(imageType + "/" + saveName)
                .thumbnailPath(imageType + "/" + thumbName)
                .imageType(imgType)
                .post(post)
                .member(psOpt.map(PetSitter::getMember).orElse(null))
                .build();
      }

      case "community" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(imageType + "/" + saveName)
              .thumbnailPath(imageType + "/" + thumbName)
              .imageType(imgType)
              .community(Community.builder().communityId(targetId).build())
              .build();

      default -> throw new IllegalArgumentException("지원하지 않는 targetType: " + targetType);
    };

    /* ---------- 6. 저장 및 Member.profileImagePath 갱신 ---------- */
    Image saved = imageRepository.save(image);
    if (type.equals("member")) {
      memberRepository.updateProfilePath(targetId, saved.getPath());
    }
    if (type.equals("pet")) {
      // Pet 엔티티에 imagePath / thumbnailPath 반영 및 imgId 관계 설정
      petRepository.findById(targetId).ifPresent(p -> {
        p.setImagePath(saved.getPath());
        p.setThumbnailPath(saved.getThumbnailPath());
        p.setImgId(saved);  // Set the imgId relationship
        petRepository.save(p);  // Save the updated Pet entity
      });
    }

    log.info("Image saved → {}", saved.getPath());
    return saved.getPath();          // ex) member/uuid_filename.jpg
  }

  /* ====================================================================== */
  /* === 기타 유틸 메서드 ================================================== */
  /* ====================================================================== */

  @Transactional
  public String replaceImage(MultipartFile newFile,
                             String imageType,
                             String oldFileName,
                             String targetType,
                             Long targetId,
                             Long petId) throws Exception {

    // 기존 원본 삭제
    new File(uploadRoot + File.separator + imageType, oldFileName).delete();
    imageRepository.deleteByPath(imageType + "/" + oldFileName);

    return saveImage(newFile, imageType, targetType, targetId, petId);
  }

  @Transactional
  public boolean deleteImage(String imageType, String fileName) {
    try {
      File file = new File(uploadRoot + File.separator + imageType, fileName);
      if (file.exists()) file.delete();
      imageRepository.deleteByPath(imageType + "/" + fileName);
      return true;
    } catch (Exception e) {
      log.error("이미지 삭제 실패", e);
      return false;
    }
  }

  /**
   * 파일‑시스템 이미지를 Spring Resource 로드
   */
  public Resource loadImageAsResource(String fileName) throws Exception {
    Path path = Paths.get(uploadRoot, fileName);
    Resource res = new UrlResource(path.toUri());
    if (res.exists() && res.isReadable()) return res;
    throw new RuntimeException("이미지를 찾을 수 없습니다: " + fileName);
  }
}

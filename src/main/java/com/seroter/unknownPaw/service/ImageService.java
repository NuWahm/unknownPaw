package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.ImageType;
import com.seroter.unknownPaw.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImageService {

  private final ImageRepository imageRepository;

  @Value("${com.seroter.upload.path}")
  private String uploadPath;

  // ✅ 이미지 업로드 및 DB 저장

  //  saveImage 매개변수로 있는 imageType= 이미지 연결하는 주체
  //  ex(1=member, 2=pet, 3=post)

  //  saveImage 매개변수로 있는 targetType= 이미지를 어떤 Entity 에 연결할지 지정
  //  ex) "member" → Member 객체의 이미지
  //  ex) "pet" → Pet 객체의 이미지
  //  ex) "petOwner" → PetOwner 게시글 이미지
  //  ex) "petSitter" → PetSitter 게시글 이미지



  public String saveImage(MultipartFile file, ImageType imageType, String targetType, Long targetId) throws Exception {

    String originalName = file.getOriginalFilename();
    String uuid = UUID.randomUUID().toString();
    String saveName = uuid + "_" + originalName;
    String folder = imageType.name().toLowerCase();

    File dir = new File(uploadPath + "/" + imageType);
    if (!dir.exists()) dir.mkdirs();

    File saveFile = new File(dir, saveName);
    file.transferTo(saveFile);

    String fullPath = folder + "/" + saveName;

    // switch expression 구문
    // 자바 14+부터 지원하는 **switch expression**을 활용하여 가독성 매우 좋음
    // 중복되는 필드 (uuid, profileImg, path, imageType)는 통일되게 설정되고
    // 타입별로 연결해야 하는 연관 객체만 달라짐


    //💡 targetType(ImageRequestDTO 에 선언)은 어떤 엔티티와 연결된 이미지인지를 구분합니다.
    //  ex) "member" → Member 객체에 연결
    //  ex) "pet" → Pet 객체에 연결
    //  ex) "petOwner" → PetOwner 게시글 이미지로 연결
    //  ex) "petSitter" → PetSitter 게시글 이미지로 연결
    //👉이 값은 Image 객체 생성 시, 어떤 필드를 설정할지 switch 문에서 결정하는 데 사용돼요.

    //💡 targetId(ImageRequestDTO 에 선언)dms targetType 으로 지정한 엔티티의 PK(ID) 를 뜻합니다.
    //   ex) targetType = "member", targetId = 10L → Member(10)과 연결
    //   ex) targetType = "pet", targetId = 3L → Pet(3)과 연결
    //👉 Image 에 연결될 대상이 어떤 객체인지를 식별하는 데 필요합니다.


    Image image = switch (targetType) {
      case "member" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(fullPath)
              .imageType(imageType.ordinal()) // or use a method in the enum
              .member(Member.builder().mid(targetId).build())
              .build();

      case "pet" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(fullPath)
              .imageType(imageType.ordinal())
              .pet(Pet.builder().petId(targetId).build())
              .build();

      case "petOwner" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(fullPath)
              .imageType(imageType.ordinal())
              .petOwner(PetOwner.builder().postId(targetId).build())
              .build();

      case "petSitter" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(fullPath)
              .imageType(imageType.ordinal())
              .petSitter(PetSitter.builder().postId(targetId).build())
              .build();

      case "community" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(fullPath)
              .imageType(imageType.ordinal())
              .community(Community.builder().communityId(targetId).build())
              .build();

      default -> throw new IllegalArgumentException("잘못된 targetType 입니다: " + targetType);
    };

    imageRepository.save(image);
    return saveName;
  }


  // ✅ 이미지 교체 (파일+DB)
  // @Transactional 실패하면 모든 변경사항을 롤백할 수 있어 데이터 일관성을 지켜줌
  @Transactional
  public String replaceImage(MultipartFile newFile, ImageType imageType, String oldFileName, String targetType, Long targetId) throws Exception {
    // 1. 기존 파일 삭제
    File oldFile = new File(uploadPath + "/" + imageType, oldFileName);
    if (oldFile.exists()) oldFile.delete();

    // 2. DB 삭제
    imageRepository.deleteByPath(imageType + "/" + oldFileName);

    // 3. 새 이미지 저장
    return saveImage(newFile, imageType, targetType, targetId);
  }

  // ✅ 이미지 삭제 (파일+DB)
  @Transactional
  public boolean deleteImage(String imageType, String fileName) {
    try {
      File file = new File(uploadPath + "/" + imageType, fileName);
      if (file.exists()) {
        file.delete();
        imageRepository.deleteByPath(imageType + "/" + fileName);
        return true;
      }
    } catch (Exception e) {
      log.error("이미지 삭제 실패", e);
    }
    return false;
  }
}
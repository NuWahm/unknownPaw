package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.config.UploadPathProvider;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.repository.ImageRepository;
import com.seroter.unknownPaw.repository.PetOwnerRepository;
import com.seroter.unknownPaw.repository.PetSitterRepository;
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
  private final UploadPathProvider uploadPathProvider;
  private final PetOwnerRepository petOwnerRepository;
  private final PetSitterRepository petSitterRepository;

  // ✅ 이미지 업로드 및 DB 저장

  //  saveImage 매개변수로 있는 imageType= 이미지 연결하는 주체
  //  ex(1=member, 2=pet, 3=post)

  //  saveImage 매개변수로 있는 targetType= 이미지를 어떤 Entity 에 연결할지 지정
  //  ex) "member" → Member 객체의 이미지
  //  ex) "pet" → Pet 객체의 이미지
  //  ex) "petOwner" → PetOwner 게시글 이미지
  //  ex) "petSitter" → PetSitter 게시글 이미지



  public String saveImage(MultipartFile file, String imageType, String targetType, Long targetId, Long petId) throws Exception {
    String uploadPath = uploadPathProvider.getUploadPath();
    String originalName = file.getOriginalFilename();
    String uuid = UUID.randomUUID().toString();
    String saveName = uuid + "_" + originalName;

    File dir = new File(uploadPath + "/" + imageType);
    if (!dir.exists()) dir.mkdirs();

    File saveFile = new File(dir, saveName);
    file.transferTo(saveFile);

    // ⭐ imageType 변환 로직 추가!
    int imgType;
    try {
      imgType = Integer.parseInt(imageType);
    } catch (NumberFormatException e) {
      switch (imageType.toLowerCase()) {
        case "petowner":
        case "pet_owner":
        case "petsitter":
        case "pet_sitter":
          imgType = 3; // 게시글(post)
          break;
        case "community":
          imgType = 4;
          break;
        case "member":
          imgType = 1;
          break;
        case "pet":
          imgType = 2;
          break;
        default:
          imgType = 0;
      }
    }

    String type = targetType.toLowerCase();
    Image image = switch (type) {

      case "member" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(imageType + "/" + saveName)
              .imageType(imgType)
              .member(Member.builder().mid(targetId).build())
              .build();

      case "pet" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(imageType + "/" + saveName)
              .imageType(imgType)
              .pet(Pet.builder().petId(targetId).build())
              .build();


      case "petowner", "pet_owner" -> {
        Optional<PetOwner> postOpt = petOwnerRepository.findById(targetId);
        Member member = null;
        Pet pet = null;
        if (postOpt.isPresent()) {
          PetOwner owner = postOpt.get();
          member = postOpt.get().getMember(); // 게시글의 작성자
          pet = owner.getPet();
        }
        PetOwner post = postOpt.orElseGet(() -> {
          PetOwner tmp = new PetOwner();
          tmp.setPostId(targetId);
          return tmp;
        });
        yield Image.builder()
                .uuid(uuid)
                .profileImg(originalName)
                .path(imageType + "/" + saveName)
                .imageType(imgType)
                .post(post)
                .member(member)
                .pet(pet)
                .build();
      }

      case "petsitter", "pet_sitter" -> {
        Optional<PetSitter> postOpt = petSitterRepository.findById(targetId);
        Member member = null;
        if (postOpt.isPresent()) {
          member = postOpt.get().getMember(); // 게시글의 작성자
        }
        PetSitter post = postOpt.orElseGet(() -> {
          PetSitter tmp = new PetSitter();
          tmp.setPostId(targetId);
          return tmp;
        });
        yield Image.builder()
                .uuid(uuid)
                .profileImg(originalName)
                .path(imageType + "/" + saveName)
                .imageType(imgType)
                .post(post)
                .member(member)
                .build();
      }

      case "community" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(imageType + "/" + saveName)
              .imageType(imgType)
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
  public String replaceImage(MultipartFile newFile, String imageType, String oldFileName, String targetType, Long targetId, Long petId) throws Exception {
    String uploadPath = uploadPathProvider.getUploadPath();

    File oldFile = new File(uploadPath + "/" + imageType, oldFileName);
    if (oldFile.exists()) oldFile.delete();

    imageRepository.deleteByPath(imageType + "/" + oldFileName);

    return saveImage(newFile, imageType, targetType, targetId, petId);
  }

  // ✅ 이미지 삭제 (파일+DB)
  @Transactional
  public boolean deleteImage(String imageType, String fileName) {
    try {
      String uploadPath = uploadPathProvider.getUploadPath();

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

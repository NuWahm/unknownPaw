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
import net.coobird.thumbnailator.Thumbnails;

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

  @Value("${com.seroter.upload.path}")
  private String uploadPath;

  public String saveImage(MultipartFile file, String imageType, String targetType, Long targetId, Long petId) throws Exception {
    String uploadPath = uploadPathProvider.getUploadPath();
    String originalName = file.getOriginalFilename();
    String uuid = UUID.randomUUID().toString();
    String saveName = uuid + "_" + originalName;

    File dir = new File(uploadPath + "/" + imageType);
    if (!dir.exists()) dir.mkdirs();

    File saveFile = new File(dir, saveName);
    file.transferTo(saveFile);

    String thumbName = uuid + "_thumb_" + originalName;
    File thumbnailFile = new File(dir, thumbName);

    Thumbnails.of(saveFile)
            .size(400, 600)
            .toFile(thumbnailFile);

    int imgType;
    try {
      imgType = Integer.parseInt(imageType);
    } catch (NumberFormatException e) {
      switch (imageType.toLowerCase()) {
        case "petowner", "pet_owner", "petsitter", "pet_sitter" -> imgType = 3;
        case "community" -> imgType = 4;
        case "member" -> imgType = 1;
        case "pet" -> imgType = 2;
        default -> imgType = 0;
      }
    }

    String type = targetType.toLowerCase();
    Image image = switch (type) {
      case "member" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(imageType + "/" + saveName)
              .thumbnailPath(imageType + "/" + thumbName)
              .imageType(imgType)
              .member(Member.builder().mid(targetId).build())
              .build();

      case "pet" -> Image.builder()
              .uuid(uuid)
              .profileImg(originalName)
              .path(imageType + "/" + saveName)
              .thumbnailPath(imageType + "/" + thumbName)
              .imageType(imgType)
              .pet(Pet.builder().petId(targetId).build())
              .build();

      case "petowner", "pet_owner" -> {
        Optional<PetOwner> postOpt = petOwnerRepository.findById(targetId);
        Member member = null;
        Pet pet = null;
        if (postOpt.isPresent()) {
          PetOwner owner = postOpt.get();
          member = owner.getMember();
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
                .thumbnailPath(imageType + "/" + thumbName)
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
          member = postOpt.get().getMember();
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
                .thumbnailPath(imageType + "/" + thumbName)
                .imageType(imgType)
                .post(post)
                .member(member)
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

      default -> throw new IllegalArgumentException("잘못된 targetType 입니다: " + targetType);
    };

    imageRepository.save(image);
    return saveName;
  }

  @Transactional
  public String replaceImage(MultipartFile newFile, String imageType, String oldFileName, String targetType, Long targetId, Long petId) throws Exception {
    String uploadPath = uploadPathProvider.getUploadPath();

    File oldFile = new File(uploadPath + "/" + imageType, oldFileName);
    if (oldFile.exists()) oldFile.delete();

    imageRepository.deleteByPath(imageType + "/" + oldFileName);

    return saveImage(newFile, imageType, targetType, targetId, petId);
  }

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
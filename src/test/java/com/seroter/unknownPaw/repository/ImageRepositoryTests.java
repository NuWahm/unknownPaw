package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ImageRepositoryTests {

  @Autowired
  ImageRepository imageRepository;

  @PersistenceContext
  EntityManager em;

  @Test
  void testMemberImage_Save_Find_Delete() {
    Member member = Member.builder()
            .name("홍길동")
            .email("test" + UUID.randomUUID() + "@example.com")
            .nickname("홍길동" + UUID.randomUUID())
            .birthday(1990)
            .gender(true)
            .emailVerified(false)
            .fromSocial(false)
            .role(Member.Role.USER)
            .status(Member.MemberStatus.ACTIVE)
            .build();
    em.persist(member);

    Image image = Image.builder()
            .imageType(1)
            .member(member)
            .uuid(UUID.randomUUID().toString())
            .path("/member/image")
            .profileImg("profile.jpg")
            .build();
    imageRepository.save(image);

    Image foundImage = imageRepository.findMemberProfileImage(member.getMid());
    assertThat(foundImage).isNotNull();
    assertThat(foundImage.getMember().getMid()).isEqualTo(member.getMid());

    imageRepository.deleteByMemberId(member.getMid());
    em.flush();
    em.clear();

    Image deletedImage = imageRepository.findMemberProfileImage(member.getMid());
    assertThat(deletedImage).isNull();
  }

  @Test
  void testPetImage_Save_Find_Delete() {
    Pet pet = Pet.builder()
            .petName("코코")
            .build();
    em.persist(pet);

    List<Image> images = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      images.add(Image.builder()
              .imageType(2)
              .pet(pet)
              .uuid(UUID.randomUUID().toString())
              .profileImg("pet_" + i + ".jpg")
              .path("/pet/image")
              .build());
    }
    imageRepository.saveAll(images);

    List<Image> foundImages = imageRepository.findPetImages(pet.getPetId());
    assertThat(foundImages).hasSize(5);

    imageRepository.deleteByPetId(pet.getPetId());
    em.flush();
    em.clear();

    List<Image> deletedImages = imageRepository.findPetImages(pet.getPetId());
    assertThat(deletedImages).isEmpty();
  }

  @Test
  void testPostImage_Save_Find_Delete() {
    Post post = PetOwner.builder()
            .title("우리집 강아지 산책 시켜주세요!")
            .build();
    em.persist(post);

    Image image = Image.builder()
            .imageType(3)
            .post(post)
            .uuid(UUID.randomUUID().toString())
            .profileImg("post1.jpg")
            .path("/post/image")
            .build();
    imageRepository.save(image);

    List<Image> foundImages = imageRepository.findPostImages(post.getPostId());
    assertThat(foundImages).hasSize(1);
    assertThat(foundImages.get(0).getPost().getPostId()).isEqualTo(post.getPostId());

    imageRepository.deleteByPostId(post.getPostId());
    em.flush();
    em.clear();

    List<Image> deleted = imageRepository.findPostImages(post.getPostId());
    assertThat(deleted).isEmpty();
  }

  @Test
  void testSitterPostImage_Save_Find_Delete() {
    Post post = PetSitter.builder()
            .title("우리집 강아지 산책 시켜주세요!")
            .build();
    em.persist(post);

    Image image = Image.builder()
            .imageType(3)
            .post(post)
            .uuid(UUID.randomUUID().toString())
            .profileImg("post1.jpg")
            .path("/post/image")
            .build();
    imageRepository.save(image);

    List<Image> foundImages = imageRepository.findPostImages(post.getPostId());
    assertThat(foundImages).hasSize(1);
    assertThat(foundImages.get(0).getPost().getPostId()).isEqualTo(post.getPostId());

    imageRepository.deleteByPostId(post.getPostId());
    em.flush();
    em.clear();

    List<Image> deleted = imageRepository.findPostImages(post.getPostId());
    assertThat(deleted).isEmpty();
  }
}

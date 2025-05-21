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

<<<<<<< HEAD
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
=======

    // 객체를 영속성 컨텍스트(PersistenceContext)에 등록합니다.
    // 쉽게 말해서 이 객체를 DB에 저장할 준비 해줘" 라는 뜻 repository 보면됩니다. 말그대로 저장한한다.
    // 필수, 안 하면 DB에 insert 안 됨.
    @PersistenceContext
    EntityManager em;


    // 멤버 이미지 저장,조회,삭제
    @Test
    void testMemberImage_Save_Find_Delete() {

        // 임시 멤버 생성
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
        em.persist(member); // EntityManager em; 위에 내용 참고


        // 멤버 이미지 등록
        Image image = Image.builder()
                .imageType(3)
                .member(member)
                .uuid(UUID.randomUUID().toString())
                .path("/member/image")
                .profileImg("profile.jpg")
                .build();

        imageRepository.save(image);

        // 멤버 이미지 조회
        Image foundImage = imageRepository.findMemberProfileImage(member.getMid());
        assertThat(foundImage).isNotNull();
        assertThat(foundImage.getMember().getMid()).isEqualTo(member.getMid());
        // assertThat-  AssertJ 라이브러리의 테스트 메서드 입니다
        // 예상한 값과 실제 값이 같은지 비교
        // 쉽게 말하면 "이 결과가 내가 예상한 거랑 같니?"를 묻는 테스트 확인 코드입니다.


        // 멤버 이미지 삭제
        imageRepository.deleteByMemberId(member.getMid());
        em.flush(); // 즉시 DB에 반영
        em.clear(); // 1차 캐시 날려서 새로 쿼리하게 만듦 (조회 테스트에 필요)


        Image deletedImage = imageRepository.findMemberProfileImage(member.getMid());
        assertThat(deletedImage).isNull();  // 조회 했을때 NULL 값나오면 성공
>>>>>>> origin/back-do3
    }

<<<<<<< HEAD
    List<Image> foundImages = imageRepository.findPetImages(pet.getPetId());
    assertThat(foundImages).hasSize(5);

    imageRepository.deleteByPetId(pet.getPetId());
    em.flush();
    em.clear();
=======
    // pet 이미지 등록,조회,삭제
    @Test
    void testPetImage_Save_Find_Delete() {
        // 임시 펫 생성
        Pet pet = Pet.builder()
                .petName("코코")
                .build();
        em.persist(pet);

        // 펫 이미지 등록
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

        // 펫 이미지 조회
        List<Image> foundImages = imageRepository.findPetImages(pet.getPetId());
        assertThat(foundImages).hasSize(5);


        // 펫 이미지 삭제
        imageRepository.deleteByPetId(pet.getPetId());
        em.flush();
        em.clear();
>>>>>>> origin/back-do3

        List<Image> deletedImages = imageRepository.findPetImages(pet.getPetId());
        assertThat(deletedImages).isEmpty();
    }

<<<<<<< HEAD
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
=======

    // 오너 이미지 등록 조회 삭제
    @Test
    void testPetOwnerPostImage_Save_Find_Delete() {

        // 임시 오너 게시판 등록
        PetOwner post = PetOwner.builder()
                .title("우리집 강아지 산책 시켜주세요!")
                .build();
        em.persist(post);

        // 오너 게시판 이미지 등록
        Image image = Image.builder()
                .imageType(3)
                .petOwner(post)
                .uuid(UUID.randomUUID().toString())
                .profileImg("post1.jpg")
                .path("/post/image")
                .build();

        imageRepository.save(image);


        // 오너 게시판 이미지 조회
        List<Image> foundImages = imageRepository.findPetOwnerPostImages(post.getPostId());
        assertThat(foundImages).hasSize(1);
        assertThat(foundImages.get(0).getPetOwner().getPostId()).isEqualTo(post.getPostId());

        // 오너 게시판 이미지 삭제
        imageRepository.deleteByPetOwnerId(post.getPostId());
        em.flush();
        em.clear();

        List<Image> deleted = imageRepository.findPetOwnerPostImages(post.getPostId());
        assertThat(deleted).isEmpty();
    }


    // 시터 이미지 등록,조회,삭제
    @Test
    void testPetSitterPostImage_Save_Find_Delete() {
        // 시터 게시판 임시 생성
        PetSitter post = PetSitter.builder()
                .title("우리집 강아지 산책 시켜주세요!")
                .build();
        em.persist(post);

        // 시터 게시판 이미지 생성
        Image image = Image.builder()
                .imageType(3)
                .petSitter(post)
                .uuid(UUID.randomUUID().toString())
                .profileImg("post1.jpg")
                .path("/post/image")
                .build();

        imageRepository.save(image);

        //  시터 게시판 이미지 조회
        List<Image> foundImages = imageRepository.findPetSitterPostImages(post.getPostId());
        assertThat(foundImages).hasSize(1);
        assertThat(foundImages.get(0).getPetSitter().getPostId()).isEqualTo(post.getPostId());

        // 시터 게시판 이미지 삭제
        imageRepository.deleteByPetSitterId(post.getPostId());
        em.flush();
        em.clear();

        List<Image> deleted = imageRepository.findPetSitterPostImages(post.getPostId());
        assertThat(deleted).isEmpty();
    }
>>>>>>> origin/back-do3
}

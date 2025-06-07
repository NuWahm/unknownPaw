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
        em.persist(member);

        // 멤버 이미지 등록
        Image image = Image.builder()
                .imageType(Image.TYPE_PROFILE)
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

        // 멤버 이미지 삭제
        imageRepository.deleteByMemberId(member.getMid());
        em.flush();
        em.clear();

        Image deletedImage = imageRepository.findMemberProfileImage(member.getMid());
        assertThat(deletedImage).isNull();
    }

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
                    .imageType(Image.TYPE_PET)
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

        List<Image> deletedImages = imageRepository.findPetImages(pet.getPetId());
        assertThat(deletedImages).isEmpty();
    }

    @Test
    void testPostImage_Save_Find_Delete() {
        // 임시 게시글 생성
        Post post = PetOwner.builder()
                .title("우리집 강아지 산책 시켜주세요!")
                .build();
        em.persist(post);

        // 게시글 이미지 등록
        Image image = Image.builder()
                .imageType(Image.TYPE_POST)
                .post(post)
                .uuid(UUID.randomUUID().toString())
                .profileImg("post1.jpg")
                .path("/post/image")
                .build();
        imageRepository.save(image);

        // 게시글 이미지 조회
        List<Image> foundImages = imageRepository.findPostImages(post.getPostId());
        assertThat(foundImages).hasSize(1);
        assertThat(foundImages.get(0).getPost().getPostId()).isEqualTo(post.getPostId());

        // 게시글 이미지 삭제
        imageRepository.deleteByPostId(post.getPostId());
        em.flush();
        em.clear();

        List<Image> deleted = imageRepository.findPostImages(post.getPostId());
        assertThat(deleted).isEmpty();
    }
}

package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;  // UUID 생성 관련 import 추가
import java.util.Random;

@SpringBootTest
public class PostRepositoryImplTests {

    @Autowired
    PetOwnerRepository petOwnerRepository;

    @Autowired
    PetSitterRepository petSitterRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ImageRepository imageRepository;

    @Test
    public void createPostWithOwnerAndSitter() {
        Random random = new Random();

        for (int i = 1; i <= 100; i++) {
            // 1. Member 생성 (Owner 및 Sitter 공통)
            Member owner = Member.builder()
                    .email("owner" + i + "@example.com")
                    .password("password")
                    .name("Owner" + i)
                    .nickname("OwnerNick" + i)
                    .phoneNumber("010-1234-567" + i)
                    .pawRate(0.5f)
                    .gender(random.nextBoolean())
                    .birthday(1990 + (i % 10))
                    .address("Address" + i)
                    .emailVerified(true)
                    .fromSocial(false)
                    .role(Member.Role.USER)
                    .status(Member.MemberStatus.ACTIVE)
                    .signupChannel("test")
                    .build();
            memberRepository.save(owner);

            // 2. PetOwner 생성 (오너 게시글)
            PetOwner petOwner = PetOwner.builder()
                    .title("우리집 강아지 산책 도와주세요! #" + i)
                    .content("강아지가 순하고 사람을 좋아해요. 편안한 산책을 좋아해요.")
                    .serviceCategory(ServiceCategory.WALK)
                    .desiredHourlyRate(10000 + random.nextInt(5000))
                    .likes(random.nextInt(50))
                    .chatCount(random.nextInt(10))
                    .defaultLocation("서울시 송파구")
                    .flexibleLocation("서울시 강동구")
                    .member(owner)
                    .build();
            petOwnerRepository.save(petOwner);

            // 3. PetSitter 생성 (시터 게시글)
            PetSitter petSitter = PetSitter.builder()
                    .title("강아지 산책 시켜드려요! #" + i)
                    .content("2시간 동안 강아지와 산책할 수 있어요. 자전거로도 산책 가능!")
                    .serviceCategory(ServiceCategory.WALK)
                    .desiredHourlyRate(10000 + random.nextInt(5000))
                    .likes(random.nextInt(30))
                    .chatCount(random.nextInt(10))
                    .defaultLocation("서울시 강남구")
                    .flexibleLocation("서울시 서초구")
                    .member(owner)  // 동일한 owner가 시터 역할을 할 수 있습니다.
                    .build();
            petSitterRepository.save(petSitter);


//  5. Pet 생성 (펫 정보)
            Pet pet = Pet.builder()
                    .petName("몽실이" + i)
                    .breed("푸들")
                    .petBirth(2019 + (i % 5))
                    .petGender(random.nextBoolean())
                    .weight(4.5 + (i % 3))
                    .petMbti("ENFP")
                    .neutering(true)
                    .petIntroduce("사람 좋아하고 순해요")
                    .member(owner)
                    .petOwner(petOwner)  // 펫 오너와 연결
                    .build();
            petRepository.save(pet);

// 6. 이미지 생성 (pet 저장 후 pet 참조)
            Image petImage = Image.builder()
                    .profileImg("pet_image_" + i + ".jpg") // 파일명
                    .uuid(UUID.randomUUID().toString())    // UUID 생성
                    .path("/images/pet/" + "pet_image_" + i + ".jpg") // 파일 경로
                    .role(2) // Pet 이미지로 설정
                    .pet(pet) // 저장된 Pet 참조
                    .build();
            imageRepository.save(petImage);

// 7. Pet에 이미지 연결
            pet.setImgId(petImage); // setter가 있어야 해
            petRepository.save(pet);
        }
    }
}



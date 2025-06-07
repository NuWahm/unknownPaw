package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.util.Random;

@SpringBootTest
public class PostRepositoryImplTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                    .email("mung" + i + "@mogae.com")
                    .password(passwordEncoder.encode("1"))
                    .name("Owner" + i)
                    .nickname("OwnerNick" + i + "test")
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
                    .hourlyRate(10000 + random.nextInt(5000))
                    .likes(random.nextInt(50))
                    .chatCount(random.nextInt(10))
                    .defaultLocation("부산시 부산진구")
                    .flexibleLocation("부산시 기장군")
                    .member(owner)
                    .build();
            petOwnerRepository.save(petOwner);

            // 3. PetSitter 생성 (시터 게시글)
            PetSitter petSitter = PetSitter.builder()
                    .title("강아지 산책 시켜드려요! #" + i)
                    .content("경험이 많은 펫시터입니다. 안전하고 즐거운 산책을 약속드립니다.")
                    .serviceCategory(ServiceCategory.WALK)
                    .hourlyRate(8000 + random.nextInt(3000))
                    .likes(random.nextInt(30))
                    .chatCount(random.nextInt(5))
                    .defaultLocation("부산시 해운대구")
                    .flexibleLocation("부산시 수영구")
                    .member(owner)
                    .build();
            petSitterRepository.save(petSitter);

            // 4. Pet 생성
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
                    .petOwnerId(petOwner)
                    .status(Pet.PetStatus.ACTIVE)
                    .build();
            petRepository.save(pet);

            // 5. Pet 이미지 생성
            Image petImage = Image.builder()
                    .imageType(Image.TYPE_PET)
                    .pet(pet)
                    .uuid(UUID.randomUUID().toString())
                    .profileImg("pet_" + i + ".jpg")
                    .path("/images/pet/pet_" + i + ".jpg")
                    .build();
            imageRepository.save(petImage);

            // 6. Pet에 이미지 연결
            pet.setImgId(petImage);
            petRepository.save(pet);
        }
    }
}

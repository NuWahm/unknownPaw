package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.stream.IntStream;

@SpringBootTest
class PetRepositoryTests {

    @Autowired
    PetRepository petRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    PetOwnerRepository petOwnerRepository;
    @Autowired
    PetSitterRepository petSitterRepository;

    @Test
    public void insertPet() {
        Random random = new Random();

        IntStream.rangeClosed(1, 100).forEach(i -> {
            // 1. 회원 생성 (pet 소유 여부에 따라 Owner / Sitter 구분)
            Member member = Member.builder()
                    .email("Odeng" + i + "@mogae.com")
                    .password("1")
                    .name("Owner" + i)
                    .nickname("MungMung" + i)
                    .phoneNumber("010-1111-" + String.format("%04d", i))
                    .pawRate(0.5f)
                    .gender(random.nextBoolean())
                    .birthday(1990 + (i % 10))
                    .address("부산시 테스트구")
                    .emailVerified(true)
                    .fromSocial(false)
                    .role(Member.Role.USER)
                    .status(Member.MemberStatus.ACTIVE)
                    .signupChannel("test")
                    .build();

            memberRepository.save(member);

            // 2. PetOwner와 PetSitter 구분 (member의 pet 소유 여부로 결정)
            boolean isOwner = random.nextBoolean();  // true이면 Owner, false이면 Sitter
            PetOwner petOwner = null;
            PetSitter petSitter = null;

            if (isOwner) {
                petOwner = PetOwner.builder()
                        .member(member)
                        .title("Owner_" + member.getNickname()) // 여기서는 name이 아니라 title을 사용하는게 맞을 수 있음.
                        .content("This is the owner description.") // 예시로 content 추가
                        .build();

                petOwnerRepository.save(petOwner);  // PetOwner 먼저 저장
            } else {
                petSitter = PetSitter.builder()
                        .member(member)
                        .title("Sitter_" + member.getNickname()) // 마찬가지로 title을 사용하여 설정
                        .content("This is the sitter description.") // 예시로 content 추가
                        .build();

                petSitterRepository.save(petSitter);  // PetSitter 먼저 저장
            }

            // 3. 펫 생성
            Pet pet = Pet.builder()
                    .petName("Pet_" + i)
                    .breed("Breed_" + random.nextInt(5))
                    .petBirth(2018 + random.nextInt(3))
                    .petGender(random.nextBoolean())
                    .weight(random.nextDouble() * 10)
                    .petMbti("MBTI_" + random.nextInt(5))
                    .neutering(random.nextBoolean())
                    .petIntroduce("Pet Introduction")
                    .member(member)
                    .petOwnerId(petOwner)  // Owner가 있을 때는 PetOwner 지정
                    .petSitterId(petSitter)  // Sitter가 있을 때는 PetSitter 지정
                    .build();

            // 4. 이미지 생성
            Image image = Image.builder()
                    .profileImg("profile_image_" + i + ".jpg")
                    .uuid("uuid_" + random.nextInt(1000))
                    .path("/images/" + i + ".jpg")
                    .imageType(2)  // 2 means it's a pet image
                    .pet(pet)
                    .build();

            // 5. 저장
            petRepository.save(pet);
            imageRepository.save(image);

            System.out.println("✅ 펫 등록: " + pet.getPetName()
                    + " (주인: " + member.getEmail()
                    + ", 보호자: " + (petOwner != null ? petOwner.getMember().getName() : "없음")
                    + ", 시터: " + (petSitter != null ? petSitter.getMember().getName() : "없음")
                    + ", 이미지: " + image.getProfileImg() + ")");
        });
    }
}

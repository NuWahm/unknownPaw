package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertPet() {
        Random random = new Random();

        IntStream.rangeClosed(1, 100).forEach(i -> {
            // 1. 회원 생성
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

            // 2. PetOwner와 PetSitter 구분
            boolean isOwner = random.nextBoolean();
            PetOwner petOwner = null;
            PetSitter petSitter = null;

            if (isOwner) {
                petOwner = PetOwner.builder()
                        .title("우리집 강아지 산책 시켜주세요! #" + i)
                        .content("강아지가 순하고 사람을 좋아해요.")
                        .member(member)
                        .build();
                petOwnerRepository.save(petOwner);
            } else {
                petSitter = PetSitter.builder()
                        .title("강아지 산책 시켜드려요! #" + i)
                        .content("경험이 많은 펫시터입니다.")
                        .member(member)
                        .build();
                petSitterRepository.save(petSitter);
            }

            // 3. Pet 생성
            Pet pet = Pet.builder()
                    .petName("몽실이" + i)
                    .breed("푸들")
                    .petBirth(2019 + (i % 5))
                    .petGender(random.nextBoolean())
                    .weight(4.5 + (i % 3))
                    .petMbti("ENFP")
                    .neutering(true)
                    .petIntroduce("사람 좋아하고 순해요")
                    .member(member)
                    .petOwnerId(petOwner)
                    .status(Pet.PetStatus.ACTIVE)
                    .build();
            petRepository.save(pet);

            // 4. Pet 이미지 생성
            Image petImage = Image.builder()
                    .imageType(Image.TYPE_PET)
                    .pet(pet)
                    .uuid(java.util.UUID.randomUUID().toString())
                    .profileImg("pet_" + i + ".jpg")
                    .path("/images/pet/pet_" + i + ".jpg")
                    .build();
            imageRepository.save(petImage);

            // 5. Pet에 이미지 연결
            pet.setImgId(petImage);
            petRepository.save(pet);
        });
    }

    @Test
    public void createDummyPets() {
        Random random = new Random();
        String[] breeds = {"푸들", "말티즈", "진돗개", "시바견", "웰시코기", "비숑", "요크셔테리어", "치와와", "포메라니안", "시츄"};
        String[] mbtis = {"ENFP", "ISTJ", "ESFJ", "INTJ", "ENTP", "ISFP", "ESTJ", "INFP", "ENTJ", "ISFJ"};

        // 먼저 멤버와 펫오너를 생성
        for (int i = 1; i <= 30; i++) {
            // Member 생성
            Member member = Member.builder()
                    .email("petowner" + i + "@example.com")
                    .password(passwordEncoder.encode("password" + i))
                    .name("펫오너" + i)
                    .nickname("오너" + i)
                    .phoneNumber("010-" + String.format("%04d", random.nextInt(10000)) + "-" + String.format("%04d", random.nextInt(10000)))
                    .pawRate(random.nextFloat() * 5.0f)
                    .gender(random.nextBoolean())
                    .birthday(1980 + random.nextInt(30))
                    .address("부산시 해운대구")
                    .emailVerified(true)
                    .fromSocial(false)
                    .role(Member.Role.USER)
                    .status(Member.MemberStatus.ACTIVE)
                    .signupChannel("test")
                    .build();
            memberRepository.save(member);

            // PetOwner 생성
            PetOwner petOwner = PetOwner.builder()
                    .title("우리집 강아지 산책 도와주세요! #" + i)
                    .content("강아지가 순하고 사람을 좋아해요. 편안한 산책을 좋아해요.")
                    .serviceCategory(ServiceCategory.WALK)
                    .hourlyRate(10000 + random.nextInt(5000))
                    .likes(random.nextInt(50))
                    .chatCount(random.nextInt(10))
                    .defaultLocation("부산시 부산진구")
                    .flexibleLocation("부산시 기장군")
                    .member(member)
                    .build();
            petOwnerRepository.save(petOwner);

            // 각 멤버당 1-3마리의 펫 생성
            int petCount = 1 + random.nextInt(3);
            for (int j = 0; j < petCount; j++) {
                String breed = breeds[random.nextInt(breeds.length)];
                String mbti = mbtis[random.nextInt(mbtis.length)];
                
                Pet pet = Pet.builder()
                        .petName(breed + (j + 1))
                        .breed(breed)
                        .petBirth(2019 + random.nextInt(5))
                        .petGender(random.nextBoolean())
                        .weight(3.0 + random.nextDouble() * 5.0)
                        .petMbti(mbti)
                        .neutering(random.nextBoolean())
                        .petIntroduce(breed + "는 정말 귀엽고 순한 강아지입니다.")
                        .member(member)
                        .petOwnerId(petOwner)
                        .status(Pet.PetStatus.ACTIVE)
                        .build();
                petRepository.save(pet);

                // 펫 이미지 생성
                Image petImage = Image.builder()
                        .imageType(Image.TYPE_PET)
                        .pet(pet)
                        .uuid(java.util.UUID.randomUUID().toString())
                        .profileImg("pet_" + i + "_" + j + ".jpg")
                        .path("/images/pet/pet_" + i + "_" + j + ".jpg")
                        .build();
                imageRepository.save(petImage);

                // 펫에 이미지 연결
                pet.setImgId(petImage);
                petRepository.save(pet);
            }
        }
    }
}

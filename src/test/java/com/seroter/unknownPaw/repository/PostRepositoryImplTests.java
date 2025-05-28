
package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import com.seroter.unknownPaw.entity.escrowEntity.EscrowPayment;
import com.seroter.unknownPaw.entity.escrowEntity.EscrowStatus;
import com.seroter.unknownPaw.entity.escrowEntity.ServiceProof;
import com.seroter.unknownPaw.repository.escrowRepository.EscrowPaymentRepository;
import com.seroter.unknownPaw.repository.escrowRepository.ServiceProofRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;  // UUID 생성 관련 import 추가
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

  @Autowired
  EscrowPaymentRepository escrowPaymentRepository;

  @Autowired
  ServiceProofRepository serviceProofRepository;


  @Test
  public void createPostWithOwnerAndSitter() {
    Random random = new Random();

    for (int i = 1; i <= 100; i++) {
      // 1. Member 생성 (Owner 및 Sitter 공통)
      Member owner = Member.builder()
              .email("mung" + i + "@mogae.com")
              .password(passwordEncoder.encode("1")) // 필요 시 인코딩 주석 해제
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
              .postType(PostType.PET_OWNER) // role 추가
              .build();
      petOwnerRepository.save(petOwner);

      // 3. PetSitter 생성 (시터 게시글)
      PetSitter petSitter = PetSitter.builder()
              .title("강아지 산책 시켜드려요! #" + i)
              .content("2시간 동안 강아지와 산책할 수 있어요. 자전거로도 산책 가능!")
              .serviceCategory(ServiceCategory.WALK)
              .hourlyRate(10000 + random.nextInt(5000))
              .likes(random.nextInt(30))
              .chatCount(random.nextInt(10))
              .defaultLocation("서울시 강남구")
              .flexibleLocation("서울시 서초구")
              .member(owner)  // 동일한 owner가 시터 역할을 할 수 있습니다.
              .postType(PostType.PET_SITTER)
              .build();
      petSitterRepository.save(petSitter);

      // Escrow 더미 생성 (20% 확률로 DISPUTE 상태 포함)
      EscrowStatus[] statuses = EscrowStatus.values();
      EscrowStatus randomStatus;
      if (random.nextDouble() < 0.2) {  // 20% 확률로 DISPUTE 상태
        randomStatus = EscrowStatus.DISPUTE;
      } else {
        randomStatus = statuses[random.nextInt(statuses.length)];
      }

      EscrowPayment escrow = EscrowPayment.builder()
              .postId(petOwner.getPostId())
              .amount(15000L + random.nextInt(10000))
              .sitterMid(petSitter.getMember().getMid())
              .ownerMid(petOwner.getMember().getMid())
              .status(randomStatus)
              .paidAt(LocalDateTime.now())
              .build();
      escrowPaymentRepository.save(escrow);

      // ServiceProof 더미 생성 (EscrowPayment 상태가 PROOF_SUBMITTED일 때)
      if (escrow.getStatus() == EscrowStatus.PROOF_SUBMITTED) {
        ServiceProof proof = ServiceProof.builder()
                .escrowPayment(escrow)  // 연결된 에스크로
                .photoPath("/images/proof_" + i + ".jpg")  // 예시 이미지 경로
                .latitude(37.5665 + random.nextDouble() * 0.01)  // 예시 위도 (서울 근처)
                .longitude(126.978 + random.nextDouble() * 0.01)  // 예시 경도 (서울 근처)
                .submittedAt(LocalDateTime.now())
                .build();
        serviceProofRepository.save(proof);
      }

      // Pet 생성 (펫 정보)
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
              .petOwnerId(petOwner)  // 펫 오너와 연결
              .status(Pet.PetStatus.ACTIVE)
              .build();
      petRepository.save(pet);

      // 이미지 생성 (pet 저장 후 pet 참조)
      Image petImage = Image.builder()
              .profileImg("pet_image_" + i + ".jpg") // 파일명
              .uuid(UUID.randomUUID().toString())    // UUID 생성
              .path("/images/pet/" + "pet_image_" + i + ".jpg") // 파일 경로
              .imageType(2) // Pet 이미지로 설정
              .pet(pet) // 저장된 Pet 참조
              .build();
      imageRepository.save(petImage);

      // Pet에 이미지 연결
      pet.setImgId(petImage);
      petRepository.save(pet);
    }
  }
}
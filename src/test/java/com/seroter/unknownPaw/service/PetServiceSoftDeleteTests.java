package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.EditProfile.PetUpdateRequestDTO;
import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.entity.Pet.PetStatus; // PetStatus 임포트
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import jakarta.persistence.EntityManager; // <<-- 이 import 추가
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback; // <<-- 이 import 추가

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 각 테스트 메서드가 독립적인 트랜잭션에서 실행되고 롤백되도록 합니다.
@DisplayName("Pet 서비스 소프트 딜리트 통합 테스트")
public class PetServiceSoftDeleteTests {

  @Autowired
  private PetService petService;

  @Autowired
  private PetRepository petRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired // <<-- EntityManager 주입
  private EntityManager entityManager;

  private Member testMember;
  private Pet registeredPet;

  @BeforeEach
  void setUp() {
    // 각 테스트 전에 필요한 멤버와 펫 데이터를 미리 생성합니다.
    testMember = Member.builder()
        .email("testuser2@example.com")
        .password("testpassword") // 실제 환경에서는 인코딩 필요
        .name("Test User")
        .nickname("TestNick2")
        .phoneNumber("010-9872-5432")
        .gender(true)
        .birthday(1990)
        .address("Test Address")
        .emailVerified(true)
        .fromSocial(false)
        .role(Member.Role.USER)
        .status(Member.MemberStatus.ACTIVE)
        .signupChannel("test")
        .build();
    memberRepository.save(testMember); // 멤버 저장

    PetDTO petDTO = PetDTO.builder()
        .petName("테스트펫")
        .breed("시바견")
        .petBirth(2020)
        .petGender(true)
        .weight(10.0)
        .petMbti("INFP")
        .neutering(true)
        .petIntroduce("테스트용 펫입니다.")
        .build();

    // 펫 등록 시 status는 자동으로 ACTIVE로 설정됩니다.
    Long petId = petService.registerMyPet(testMember, petDTO);
    registeredPet = petRepository.findById(petId)
        .orElseThrow(() -> new RuntimeException("테스트 펫 등록 실패"));
  }

  @Test
  @DisplayName("새로운 펫 등록 시 상태가 ACTIVE로 설정되는지 확인")
  void testRegisterPetStatusActive() {
    assertNotNull(registeredPet);
    assertEquals(PetStatus.ACTIVE, registeredPet.getStatus(), "등록된 펫의 상태는 ACTIVE여야 합니다.");
  }

  @Test
  @DisplayName("활성 상태의 펫을 성공적으로 조회하는지 확인")
  void testGetActivePet() {
    // ID와 Member를 통해 조회 (활성 펫만 조회)
    PetDTO foundPetDTO = petService.getPet(registeredPet.getPetId(), testMember);
    assertNotNull(foundPetDTO);
    assertEquals(registeredPet.getPetId(), foundPetDTO.getPetId());
    assertEquals(PetStatus.ACTIVE.name(), foundPetDTO.getStatus(), "조회된 펫의 상태는 ACTIVE여야 합니다.");

    // Member를 통해 목록 조회 (활성 펫만 조회)
    List<PetDTO> myPets = petService.getPetsByMember(testMember);
    assertFalse(myPets.isEmpty());
    assertTrue(myPets.stream().anyMatch(p -> p.getPetId().equals(registeredPet.getPetId())), "펫 목록에 등록된 펫이 포함되어야 합니다.");
  }

  @Test
  @DisplayName("펫 삭제 시 상태가 DELETED로 변경되는지 확인 (소프트 딜리트)")
  @Rollback(false) // 이 테스트 메서드는 롤백되지 않도록 설정
  void testSoftDeletePet() {
    // 펫 삭제 서비스 호출
    petService.deletePet(registeredPet.getPetId(), testMember.getEmail());

    // ✨ 중요: 영속성 컨텍스트(1차 캐시)를 초기화하여 DB에서 최신 값을 다시 로드하도록 강제합니다.
    // 이것이 가장 유력한 해결책입니다.
    entityManager.clear();

    // DB에서 직접 펫을 조회하여 상태 확인 (소프트 딜리트가 되었으므로, 리포지토리의 findById는 찾아야 함)
    // 이제 findById는 DB에서 최신 상태를 가져올 것입니다.
    Optional<Pet> deletedPetOptional = petRepository.findById(registeredPet.getPetId());
    assertTrue(deletedPetOptional.isPresent(), "소프트 삭제 후에도 펫은 DB에 존재해야 합니다.");
    assertEquals(PetStatus.DELETED, deletedPetOptional.get().getStatus(), "삭제된 펫의 상태는 DELETED여야 합니다.");
  }

  @Test
  @DisplayName("삭제된 펫은 조회되지 않는지 확인")
  void testGetDeletedPet() {
    // 먼저 펫을 삭제합니다.
    petService.deletePet(registeredPet.getPetId(), testMember.getEmail());

    // ✨ 중요: 이 테스트에서도 1차 캐시를 비워야 합니다.
    // petService.getPet(...) 메서드는 Repository를 통해 조회하지만,
    // 현재 테스트의 트랜잭션은 여전히 활성화되어 있고,
    // 이전 deletePet 호출이 같은 트랜잭션에서 발생했기 때문에 1차 캐시에 stale한 데이터가 있을 수 있습니다.
    entityManager.clear();

    // 삭제된 펫을 ID로 조회 시 EntityNotFoundException이 발생해야 합니다.
    assertThrows(EntityNotFoundException.class, () -> {
      petService.getPet(registeredPet.getPetId());
    }, "삭제된 펫 조회 시 EntityNotFoundException이 발생해야 합니다.");

    // 삭제된 펫을 ID와 Member로 조회 시 IllegalArgumentException이 발생해야 합니다.
    assertThrows(IllegalArgumentException.class, () -> {
      petService.getPet(registeredPet.getPetId(), testMember);
    }, "삭제된 펫을 Member와 함께 조회 시 IllegalArgumentException이 발생해야 합니다.");

    // Member를 통해 펫 목록 조회 시 삭제된 펫은 포함되지 않아야 합니다.
    List<PetDTO> myPetsAfterDelete = petService.getPetsByMember(testMember);
    assertTrue(myPetsAfterDelete.isEmpty() || myPetsAfterDelete.stream().noneMatch(p -> p.getPetId().equals(registeredPet.getPetId())), "펫 목록에 삭제된 펫이 포함되어서는 안 됩니다.");
  }

  @Test
  @DisplayName("존재하지 않거나 소유하지 않은 펫 삭제 시 예외 발생 확인")
  void testDeleteNonExistentOrUnauthorizedPet() {
    // 존재하지 않는 펫 ID로 삭제 시도
    assertThrows(EntityNotFoundException.class, () -> {
      petService.deletePet(9999L, testMember.getEmail());
    }, "존재하지 않는 펫 삭제 시 EntityNotFoundException이 발생해야 합니다.");

    // 다른 사용자의 펫으로 삭제 시도 (새로운 멤버 생성)
    Member anotherMember = Member.builder()
        .email("another@example.com")
        .password("pass")
        .name("Another User")
        .nickname("AnotherNick")
        .phoneNumber("010-1111-2222")
        .gender(false)
        .birthday(1995)
        .address("Other Address")
        .emailVerified(true)
        .fromSocial(false)
        .role(Member.Role.USER)
        .status(Member.MemberStatus.ACTIVE)
        .signupChannel("test")
        .build();
    memberRepository.save(anotherMember);

    // 다른 사용자의 이메일로 펫 삭제 시도
    assertThrows(EntityNotFoundException.class, () -> { // 서비스 로직에서 펫을 찾지 못하거나 소유권 불일치로 인해 발생
      petService.deletePet(registeredPet.getPetId(), anotherMember.getEmail());
    }, "다른 사용자의 펫 삭제 시 EntityNotFoundException (또는 적절한 예외)이 발생해야 합니다.");
  }

  @Test
  @DisplayName("비활성 상태의 펫 업데이트 시 예외 발생 확인")
  void testUpdateDeletedPet() {
    // 먼저 펫을 삭제합니다.
    petService.deletePet(registeredPet.getPetId(), testMember.getEmail());

    // ✨ 중요: 이 테스트에서도 1차 캐시를 비워야 합니다.
    entityManager.clear();

    // 삭제된 펫을 업데이트 시도
    PetDTO updateRequestDTO = PetDTO.builder()
        .petName("새로운 이름")
        .build();

    assertThrows(IllegalArgumentException.class, () -> {
      petService.updatePet(registeredPet.getPetId(), testMember, updateRequestDTO);
    }, "삭제된 펫 업데이트 시 IllegalArgumentException이 발생해야 합니다.");
  }
}
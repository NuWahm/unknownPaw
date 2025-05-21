package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.dto.EditProfile.PetUpdateRequestDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  // 서비스 로직에 필요한 어노테이션 (class 구현시 작성)
@RequiredArgsConstructor // final로 선언된 petRepository를 자동으로 주입
public class PetService {

  private final PetRepository petRepository;


  // 펫 등록
  public Long registerPet(PetDTO petDTO) {
    Pet pet = dtoToEntity(petDTO);
    petRepository.save(pet);
    return pet.getPetId();
  }

  // 펫 수정
//  public Long updatePet(PetDTO petDTO) {
//    Pet pet = dtoToEntity(petDTO);
//    petRepository.save(pet); //
//    return pet.getPetId();
//  }

  //  펫 삭제
//  public void removePet(Long petId) {
//    petRepository.deleteById(petId);
//  }

  //  펫 조회
  public PetDTO getPet(Long petId) {
    Pet pet = petRepository.findById(petId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));
    return entityToDTO(pet);
  }

  //  DTO → Entity 변환
  private Pet dtoToEntity(PetDTO dto) {
    // petId는 등록 시에는 null, 수정 시에는 값이 있을 수 있습니다.
    // Member 엔티티는 이 메서드 밖에서 설정해야 합니다.
    return Pet.builder()
        .petId(dto.getPetId()) // 수정 시 petId 포함
        .petName(dto.getPetName())
        .breed(dto.getBreed())
        .petBirth(dto.getPetBirth())
        .petGender(dto.isPetGender()) // boolean 필드 변환 추가
        .weight(dto.getWeight())
        .petMbti(dto.getPetMbti())
        .neutering(dto.isNeutering()) // boolean 필드 변환 추가
        .petIntroduce(dto.getPetIntroduce())
        // regDate, modDate는 BaseEntity 또는 @PrePersist/@PreUpdate로 자동 관리
        // member, imgId, petOwnerId, petSitterId는 이 메서드 밖에서 설정 또는 관리
        .build();
  }


  //  Entity → DTO 변환
  private PetDTO entityToDTO(Pet pet) {
    return PetDTO.builder()
        .petId(pet.getPetId())
        .petName(pet.getPetName())
        .breed(pet.getBreed())
        .petBirth(pet.getPetBirth())
        .petGender(pet.isPetGender()) // boolean 필드 변환 추가
        .weight(pet.getWeight())
        .petMbti(pet.getPetMbti())
        .neutering(pet.isNeutering()) // boolean 필드 변환 추가
        .petIntroduce(pet.getPetIntroduce())
        .regDate(pet.getRegDate()) // BaseEntity에서 상속받은 필드
        .modDate(pet.getModDate()) // BaseEntity에서 상속받은 필드
        .build();
  }


  // 등록
  @Transactional // 데이터 변경이 발생하므로 트랜잭션 설정
  public Long registerMyPet(Member member, PetDTO petDTO) {
    // DTO를 Entity로 변환하고, Member 엔티티를 설정합니다.
    Pet pet = dtoToEntity(petDTO);
    pet.setMember(member); // 펫 엔티티에 소유자(Member) 설정

    Pet savedPet = petRepository.save(pet);
    return savedPet.getPetId();
  }

  @Transactional // 데이터 변경이 발생하므로 트랜잭션 설정
  public PetDTO updatePet(Long petId, Member member, PetUpdateRequestDTO updateRequestDTO) {
    // 1. 해당 ID와 해당 Member에 속한 펫을 찾습니다. (소유권 검증 포함)
    // findByPetIdAndMember 메서드는 PetRepository에 정의되어 있어야 합니다.
    Optional<Pet> optionalPet = petRepository.findByPetIdAndMember(petId, member);

    // 펫을 찾지 못했거나 해당 회원의 펫이 아닌 경우 예외 발생
    Pet pet = optionalPet.orElseThrow(() ->
        new IllegalArgumentException("해당 ID의 펫을 찾을 수 없거나 수정 권한이 없습니다."));

    // 2. Pet 엔티티 업데이트
    // DTO에서 받은 값으로 엔티티의 필드를 설정합니다.
    // DTO에 포함된 필드만 업데이트하도록 null 체크 등을 수행할 수 있습니다.
    if (updateRequestDTO.getPetName() != null && !updateRequestDTO.getPetName().trim().isEmpty()) {
      pet.setPetName(updateRequestDTO.getPetName().trim());
    }
    if (updateRequestDTO.getBreed() != null && !updateRequestDTO.getBreed().trim().isEmpty()) {
      pet.setBreed(updateRequestDTO.getBreed().trim());
    }
    // int, boolean, double 타입은 null이 될 수 없으므로 Optional 또는 기본값 고려 필요
    // 여기서는 DTO에 해당 필드가 항상 포함된다고 가정하고 바로 설정합니다.
    pet.setPetBirth(updateRequestDTO.getPetBirth());
    pet.setPetGender(updateRequestDTO.isPetGender()); // boolean getter는 is로 시작할 수 있습니다.
    pet.setWeight(updateRequestDTO.getWeight());
    pet.setNeutering(updateRequestDTO.isNeutering());

    // String 타입 필드는 null 또는 빈 문자열일 수 있으므로 체크
    if (updateRequestDTO.getPetMbti() != null) {
      pet.setPetMbti(updateRequestDTO.getPetMbti().trim());
    }
    if (updateRequestDTO.getPetIntroduce() != null) {
      pet.setPetIntroduce(updateRequestDTO.getPetIntroduce().trim());
    }

    Pet updatedPet = petRepository.save(pet); // 명시적 저장 예시

    // 4. 업데이트된 엔티티를 DTO로 변환하여 반환
    return entityToDTO(updatedPet);
  }

  /**
   * 펫 삭제
   * 특정 회원의 펫을 삭제합니다. (소유권 검증 포함)
   *
   * @param petId  삭제할 펫의 고유 ID
   * @param member 해당 펫을 소유한 회원 엔티티 (보안 및 소유권 확인)
   * @throws IllegalArgumentException 해당 ID의 펫을 찾을 수 없거나 해당 회원의 펫이 아닌 경우
   */
  @Transactional // 데이터 변경이 발생하므로 트랜잭션 설정
  public void removePet(Long petId, Member member) {
    // 해당 ID와 해당 Member에 속한 펫을 찾습니다. (소유권 검증 포함)
    Optional<Pet> optionalPet = petRepository.findByPetIdAndMember(petId, member);

    // 펫을 찾지 못했거나 해당 회원의 펫이 아닌 경우 예외 발생
    Pet pet = optionalPet.orElseThrow(() ->
        new IllegalArgumentException("해당 ID의 펫을 찾을 수 없거나 삭제 권한이 없습니다."));

    // 펫 삭제
    petRepository.delete(pet); // 조회된 엔티티를 삭제
  }


  /**
   * 펫 조회
   * 특정 회원의 특정 펫을 조회합니다. (소유권 검증 포함)
   *
   * @param petId  조회할 펫의 고유 ID
   * @param member 해당 펫을 소유한 회원 엔티티 (보안 및 소유권 확인)
   * @return 조회된 펫 정보 (PetDTO 형태)
   * @throws IllegalArgumentException 해당 ID의 펫을 찾을 수 없거나 해당 회원의 펫이 아닌 경우
   */
  @Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정
  public PetDTO getPet(Long petId, Member member) {
    // 해당 ID와 해당 Member에 속한 펫을 찾습니다. (소유권 검증 포함)
    Pet pet = petRepository.findByPetIdAndMember(petId, member)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 펫을 찾을 수 없거나 조회 권한이 없습니다."));

    return entityToDTO(pet);
  }

  /**
   * 특정 회원의 모든 펫 목록을 조회합니다.
   *
   * @param member 펫 목록을 조회할 회원 엔티티
   * @return 해당 회원의 펫 목록 (PetDTO 형태)
   */
  @Transactional(readOnly = true) // 읽기 전용 트랜잭션 설정
  public List<PetDTO> getPetsByMember(Member member) {
    List<Pet> pets = petRepository.findByMember(member);

    // 조회된 Pet 엔티티 목록을 PetDTO 목록으로 변환하여 반환합니다.
    return pets.stream()
        .map(this::entityToDTO) // 각 Pet 엔티티를 PetDTO로 변환
        .collect(Collectors.toList()); // 결과를 List로 수집
  }

}

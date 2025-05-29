package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.entity.Pet.PetStatus; // PetStatus enum을 임포트합니다.
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PetService {

  private final PetRepository petRepository;
  private final MemberRepository memberRepository;

  // Pet 엔티티를 PetDTO로 변환하는 헬퍼 메서드
  private PetDTO entityToDto(Pet pet) {
    if (pet == null) return null;
    return PetDTO.builder()
        .petId(pet.getPetId())
        .petName(pet.getPetName())
        .breed(pet.getBreed())
        .petBirth(pet.getPetBirth())
        .petGender(pet.isPetGender())
        .weight(pet.getWeight())
        .petMbti(pet.getPetMbti())
        .neutering(pet.isNeutering())
        .petIntroduce(pet.getPetIntroduce())
        .status(pet.getStatus().name()) // Enum 값을 String으로 DTO에 전달
        .regDate(pet.getRegDate())
        .modDate(pet.getModDate())
        .build();
  }

  // PetDTO를 Pet 엔티티로 변환하는 헬퍼 메서드 (등록/수정에 사용)
  private Pet dtoToEntity(PetDTO petDTO, Member member) {
    return Pet.builder()
        .petId(petDTO.getPetId()) // 업데이트 시 petId 사용
        .petName(petDTO.getPetName())
        .breed(petDTO.getBreed())
        .petBirth(petDTO.getPetBirth())
        .petGender(petDTO.isPetGender())
        .weight(petDTO.getWeight())
        .petMbti(petDTO.getPetMbti())
        .neutering(petDTO.isNeutering())
        .petIntroduce(petDTO.getPetIntroduce())
        .member(member)
        .status(PetStatus.ACTIVE) // ✨ 새로운 펫 등록 시, 기본 상태는 ACTIVE로 설정합니다.
        // Image, PetOwner, PetSitter 관련 로직 추가
        .build();
  }

  // 펫 등록 로직 (새로운 펫은 기본적으로 ACTIVE 상태로 등록)
  @Transactional
  public Long registerPet(PetDTO petDTO) {
    // 이 메서드는 기존 registerPet(PetDTO petDTO)에 해당합니다.
    // 현재는 member 정보가 없으므로 이 메서드를 통해 등록되는 펫은 member가 null이 될 수 있습니다.
    // registerMyPet과 통합하거나, registerPet이 호출되는 곳에서 member를 제대로 주입해야 합니다.
    // 여기서는 Pet 엔티티의 status를 ACTIVE로 설정하는 부분에 초점을 맞춥니다.
    Pet pet = Pet.builder()
        .petName(petDTO.getPetName())
        .breed(petDTO.getBreed())
        .petBirth(petDTO.getPetBirth())
        .petGender(petDTO.isPetGender())
        .weight(petDTO.getWeight())
        .petMbti(petDTO.getPetMbti())
        .neutering(petDTO.isNeutering())
        .petIntroduce(petDTO.getPetIntroduce())
        .status(PetStatus.ACTIVE) // 기본 상태 ACTIVE
        .build();
    petRepository.save(pet);
    return pet.getPetId();
  }

  @Transactional
  public Long registerPetLater(PetDTO petDTO, Member member) {
    // 기존 registerPetLater 메서드
    Pet pet = dtoToEntity(petDTO, member); // dtoToEntity에서 status가 ACTIVE로 설정됩니다.
    petRepository.save(pet);
    return pet.getPetId();
  }

  @Transactional
  public Long registerMyPet(Member member, PetDTO petDTO) {
    // 기존 registerMyPet 메서드
    Pet pet = dtoToEntity(petDTO, member); // dtoToEntity에서 status가 ACTIVE로 설정됩니다.
    petRepository.save(pet);
    return pet.getPetId();
  }

  // 단일 펫 조회 (ACTIVE 상태인 펫만)
  public PetDTO getPet(Long petId) {
    // getPetDetail 메서드가 이미 status = 'ACTIVE' 조건을 포함하도록 수정되었습니다.
    Pet pet = petRepository.getPetDetail(petId)
        .orElseThrow(() -> new EntityNotFoundException("펫을 찾을 수 없거나 비활성 상태입니다. ID: " + petId));
    return entityToDto(pet);
  }

  // 특정 멤버의 펫 조회 (ACTIVE 상태인 펫만, Controller의 getMyPets에서 호출)
  public List<PetDTO> getPetsByMember(Member member) {
    // findByMemberAndStatus 메서드를 사용하여 ACTIVE 펫만 조회
    return petRepository.findByMemberAndStatus(member, PetStatus.ACTIVE)
        .stream()
        .map(this::entityToDto)
        .collect(Collectors.toList());
  }

  // 특정 펫 조회 (멤버 소유권 검증 포함, ACTIVE 상태인 펫만, Controller의 @GetMapping("/{petId}")에서 호출)
  public PetDTO getPet(Long petId, Member member) {
    // findByPetIdAndMemberAndStatus 메서드를 사용하여 ACTIVE 펫만 조회
    Pet pet = petRepository.findByPetIdAndMemberAndStatus(petId, member, PetStatus.ACTIVE)
        .orElseThrow(() -> new IllegalArgumentException("해당 펫을 찾을 수 없거나, 소유권이 없거나, 비활성 상태입니다."));
    return entityToDto(pet);
  }

  // 펫 정보 업데이트 로직
  @Transactional
  public PetDTO updatePet(Long petId, Member member, PetDTO petDTO) {
    // 업데이트 시에도 ACTIVE 상태인 펫만 조회해야 합니다.
    // findByPetIdAndMemberAndStatus 메서드를 사용하여 ACTIVE 펫만 조회
    Pet pet = petRepository.findByPetIdAndMemberAndStatus(petId, member, PetStatus.ACTIVE)
        .orElseThrow(() -> new IllegalArgumentException("업데이트할 펫을 찾을 수 없거나, 소유권이 없거나, 비활성 상태입니다."));

    // DTO의 필드를 엔티티에 반영
    pet.setPetName(petDTO.getPetName());
    pet.setBreed(petDTO.getBreed());
    pet.setPetBirth(petDTO.getPetBirth());
    pet.setPetGender(petDTO.isPetGender());
    pet.setWeight(petDTO.getWeight());
    pet.setPetMbti(petDTO.getPetMbti());
    pet.setNeutering(petDTO.isNeutering());
    pet.setPetIntroduce(petDTO.getPetIntroduce());
    // 이미지 등 다른 필드 업데이트 로직 추가

    petRepository.save(pet); // 변경사항 저장 (Auditing에 의해 modDate 자동 업데이트)
    return entityToDto(pet);
  }

  // 펫 상태 변경 (DELETED로) 로직
  @Transactional
  public void deletePet(Long petId, String loggedInUserEmail) {
    Member member = memberRepository.findByEmail(loggedInUserEmail)
        .orElseThrow(() -> new EntityNotFoundException("로그인된 회원을 찾을 수 없습니다: " + loggedInUserEmail));

    // Repository의 updatePetStatusToDeleted 메서드를 호출하여 status를 DELETED로 변경
    int updatedRows = petRepository.updatePetStatusToDeleted(petId, member.getMid());

    if (updatedRows == 0) {
      // 해당 펫을 찾지 못했거나, 소유권이 없거나, 이미 DELETED 상태였을 경우
      throw new EntityNotFoundException("삭제할 펫을 찾을 수 없거나, 소유권이 없거나, 이미 삭제된 펫입니다. ID: " + petId);
    }
    log.info("Pet with ID {} status changed to DELETED by user {}", petId, loggedInUserEmail);
  }
}
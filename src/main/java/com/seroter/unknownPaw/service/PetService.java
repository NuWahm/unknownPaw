package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.entity.Pet.PetStatus;
import com.seroter.unknownPaw.entity.Image;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import com.seroter.unknownPaw.repository.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {

  private final PetRepository petRepository;
  private final MemberRepository memberRepository;
  private final ImageRepository imageRepository;

  // ======================= [DTO ↔ Entity 변환] =======================

  private PetDTO entityToDto(Pet pet) {
    if (pet == null) return null;
    return PetDTO.fromEntity(pet);
  }


    private Pet dtoToEntity(PetDTO dto, Member member) {
    return Pet.builder()
            .petId(dto.getPetId())
            .petName(dto.getPetName())
            .breed(dto.getBreed())
            .petBirth(dto.getPetBirth())
            .petGender(dto.isPetGender())
            .weight(dto.getWeight())
            .petMbti(dto.getPetMbti())
            .neutering(dto.isNeutering())
            .petIntroduce(dto.getPetIntroduce())
            .member(member)
            .status(PetStatus.ACTIVE)
            .build();
  }

  // ======================= [등록/수정] =======================

  // 멤버 포함 펫 등록 (회원 가입/내 펫 추가)
  @Transactional
  public Long registerMyPet(Member member, PetDTO petDTO) {
    Pet pet = dtoToEntity(petDTO, member);
    petRepository.save(pet);
    return pet.getPetId();
  }
  @Transactional
  public PetDTO updatePetImagePath(Long petId, Image image) {
    Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new EntityNotFoundException("펫 없음: " + petId));
    
    // Pet 엔티티 업데이트
    pet.setImagePath(image.getPath());
    pet.setThumbnailPath(image.getThumbnailPath());
    pet.setImgId(image);  // imgId 관계 설정
    
    // 변경사항 저장
    Pet savedPet = petRepository.save(pet);
    
    return PetDTO.fromEntity(savedPet);
  }

  // 기존 펫 정보 수정 (소유자 본인만, ACTIVE 상태만)
  @Transactional
  public PetDTO updatePet(Long petId, Member member, PetDTO petDTO) {
    Pet pet = petRepository.findByPetIdAndMemberAndStatus(petId, member, PetStatus.ACTIVE)
            .orElseThrow(() -> new EntityNotFoundException("업데이트할 펫을 찾을 수 없거나, 소유권이 없거나, 비활성 상태입니다."));
    pet.setPetName(petDTO.getPetName());
    pet.setBreed(petDTO.getBreed());
    pet.setPetBirth(petDTO.getPetBirth());
    pet.setPetGender(petDTO.isPetGender());
    pet.setWeight(petDTO.getWeight());
    pet.setPetMbti(petDTO.getPetMbti());
    pet.setNeutering(petDTO.isNeutering());
    pet.setPetIntroduce(petDTO.getPetIntroduce());
    // 이미지 등 추가필드는 필요시 여기에
    petRepository.save(pet);
    return entityToDto(pet);
  }

  // ======================= [조회] =======================

  // 단일 펫 조회 (petId 기준, ACTIVE만)
  public PetDTO getPet(Long petId) {
    Pet pet = petRepository.getActivePetDetail(petId)
            .orElseThrow(() -> new EntityNotFoundException("펫을 찾을 수 없거나 비활성 상태입니다. ID: " + petId));
    return entityToDto(pet);
  }

  // 내 펫 목록 (member 기준, ACTIVE만)
  public List<PetDTO> getPetsByMember(Member member) {
    return petRepository.findByMemberAndStatus(member, PetStatus.ACTIVE)
            .stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
  }

  // 내 펫 목록 (mid 기준, ACTIVE만)
  public List<PetDTO> getPetsByMemberId(Long mid) {
    Member member = memberRepository.findById(mid)
            .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다. ID: " + mid));
    return getPetsByMember(member);
  }

  // 특정 펫 상세조회 (소유권, ACTIVE 체크)
  public PetDTO getPet(Long petId, Member member) {
    Pet pet = petRepository.findByPetIdAndMemberAndStatus(petId, member, PetStatus.ACTIVE)
            .orElseThrow(() -> new EntityNotFoundException("해당 펫을 찾을 수 없거나, 소유권이 없거나, 비활성 상태입니다."));
    return entityToDto(pet);
  }

  // ======================= [삭제/비활성화] =======================

  // 소프트 삭제 (상태값 DELETED, 소유자 체크, ACTIVE만)
  @Transactional
  public void deletePet(Long petId, String email) {
    Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("로그인된 회원을 찾을 수 없습니다: " + email));
    int updatedRows = petRepository.deactivatePetByOwner(petId, member.getMid());
    if (updatedRows == 0) {
      throw new EntityNotFoundException("삭제할 펫을 찾을 수 없거나, 소유권이 없거나, 이미 삭제된 펫입니다. ID: " + petId);
    }
    log.info("Pet with ID {} status changed to DELETED by user {}", petId, email);
  }

  // ======================= [기타: 후처리/이미지 연동 등 필요시 추가] =======================
  // TODO: 이미지, PetOwner/PetSitter 연동필드, 비활성화 시 후처리 등 필요에 따라 확장!
}
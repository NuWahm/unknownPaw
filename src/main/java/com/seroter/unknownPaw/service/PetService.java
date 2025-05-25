package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

  private final PetRepository petRepository;
  private final MemberRepository memberRepository;

  // 펫 등록
  public Long registerPet(PetDTO petDTO) {
    Pet pet = dtoToEntity(petDTO);
    petRepository.save(pet);
    return pet.getPetId();
  }

  // 펫 수정
  public Long updatePet(PetDTO petDTO) {
    Pet pet = dtoToEntity(petDTO);
    petRepository.save(pet);
    return pet.getPetId();
  }

  // 펫 삭제
  public void removePet(Long petId) {
    petRepository.deleteById(petId);
  }

  // 단일 펫 조회
  public PetDTO getPet(Long petId) {
    Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));
    return entityToDTO(pet);
  }

  // DTO → Entity 변환
  private Pet dtoToEntity(PetDTO dto) {
    Member member = memberRepository.findById(dto.getMid())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

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
            .member(member) // 연관관계 설정
            .build();
  }

  // Entity → DTO 변환
  private PetDTO entityToDTO(Pet pet) {
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
            .mid(pet.getMember().getMid())
            .build();
  }
}
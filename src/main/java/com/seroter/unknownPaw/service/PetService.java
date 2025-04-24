package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
  public Long updatePet(PetDTO petDTO) {
    Pet pet = dtoToEntity(petDTO);
    petRepository.save(pet); //
    return pet.getPetId();
  }

  //  펫 삭제
  public void removePet(Long petId) {
    petRepository.deleteById(petId);
  }

  //  펫 조회
  public PetDTO getPet(Long petId) {
    Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));
    return entityToDTO(pet);
  }

  //  DTO → Entity 변환
  private Pet dtoToEntity(PetDTO dto) {
    return Pet.builder()
            .petId(dto.getPetId())
            .petName(dto.getPetName())
            .breed(dto.getBreed())
            .petBirth(dto.getPetBirth())
            .weight(dto.getWeight())
            .petMbti(dto.getPetMbti())
            .petIntroduce(dto.getPetIntroduce())
            .build();
  }

  //  Entity → DTO 변환
  private PetDTO entityToDTO(Pet pet) {
    return PetDTO.builder()
            .petId(pet.getPetId())
            .petName(pet.getPetName())
            .breed(pet.getBreed())
            .petBirth(pet.getPetBirth())
            .weight(pet.getWeight())
            .petMbti(pet.getPetMbti())
            .petIntroduce(pet.getPetIntroduce())
            .build();
  }
}


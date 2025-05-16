package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service  // 서비스 로직에 필요한 어노테이션 (class 구현시 작성)
@RequiredArgsConstructor // final로 선언된 petRepository를 자동으로 주입
public class PetService {

  private final PetRepository petRepository;
  private final MemberRepository memberRepository; // PostAd member+pet 불러오기 위해 필요

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
  // memberId는 PetDTO에서 받아오며 그걸 기반으로 DB의 Member를 가져옴
  // Pet.builder()에 member을 세팅하면 외래키가 자동매핑!
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
            .member(member) // 여기서 외래키 설정됨!
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
            .neutering(pet.isNeutering())
            .petIntroduce(pet.getPetIntroduce())
            .mid(pet.getMember().getMid())
            .build();
  }
      public List<Pet> getPetsByOwnerId(Long Mid) {
        return petRepository.findPetsByMemberId(Mid);
      }
}

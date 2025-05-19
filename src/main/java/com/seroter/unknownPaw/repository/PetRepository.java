package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

  // 📌 [1] 펫 등록/수정 - save() 기본 제공

  // 📌 [2] 펫 삭제 - 기본 삭제
  @Transactional
  void deleteById(Long petId);

  // 📌 [3] 펫 삭제 - 본인의 펫만 삭제 (mid 일치 확인)
  @Modifying
  @Transactional
  @Query("DELETE FROM Pet p WHERE p.petId = :petId AND p.member.mid = :mid")
  int deletePetByOwner(@Param("petId") Long petId, @Param("mid") Long mid);

  // 📌 [4] 펫 목록 조회 - 특정 회원(mid)의 펫 목록 (페이징 처리)
  @Query("SELECT p FROM Pet p WHERE p.member.mid = :mid")
  Page<Pet> getPetsByMemberId(@Param("mid") Long mid, Pageable pageable);

  // 📌 [5] 펫 검색 - 이름으로 부분 검색
  @Query("SELECT p FROM Pet p WHERE p.petName LIKE %:petName%")
  List<Pet> searchByPetName(@Param("petName") String petName);

  // 📌 [6] 펫 상세 조회 - petId로 단일 조회
  @Query("SELECT p FROM Pet p WHERE p.petId = :petId")
  Optional<Pet> getPetDetail(@Param("petId") Long petId);

  // 📌 [7] 펫 + 멤버 연관 조회 - 펫 정보와 주인 정보 함께 조회
  @Query("SELECT p, m FROM Pet p LEFT JOIN p.member m WHERE p.petId = :petId")
  Object[] getPetWithMember(@Param("petId") Long petId);

  // 📌 [8] 펫 + 이미지 ID 조회 - 회원(mid)의 펫 ID + 이름 + 이미지 ID 조회
  @Query("""
              SELECT p.petId, p.petName, i.imgId
              FROM Pet p
              LEFT JOIN Image i ON i.pet = p AND i.imageType = 2
              WHERE p.member.mid = :mid
          """)
  List<Object[]> getPetAndImageByMemberId(@Param("mid") Long mid);
}
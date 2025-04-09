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

  // 펫 등록/수정: save() 기본 제공

  // 펫 삭제 - 기본 삭제
  @Transactional
  void deleteById(Long petId);

  // 펫 삭제 - 본인의 펫만 삭제하도록 커스텀
  @Modifying
  @Transactional
  @Query("DELETE FROM Pet p WHERE p.petId = :petId AND p.member.mid = :mid")
  int deletePetByOwner(@Param("petId") Long petId, @Param("mid") Long mid);

  // 펫 수정은 save()로 가능 (단, 엔티티를 조회해서 수정 후 save 호출 필요)

  // 특정 회원의 펫 목록 조회
  @Query("SELECT p.name, m.name FROM Pet p WHERE p.member.mid = :mid")
  Page<Pet> getPetsByMemberId(@Param("mid") Long mid, Pageable pageable);

  // 펫 이름으로 검색
  @Query("SELECT p FROM Pet p WHERE p.petName LIKE %:petName%")
  List<Pet> searchByPetName(@Param("petName") String petName);

  // 펫 아이디 검색
  @Query("SELECT p FROM Pet p WHERE p.petId = :petId")
  Optional<Pet> getPetDetail(@Param("petId") Long petId);

  // 펫 + 멤버 함께 조회
  @Query("SELECT p, m FROM Pet p LEFT JOIN p.member m WHERE p.petId = :petId")
  Object[] getPetWithMember(@Param("petId") Long petId);


  // 펫 + 이미지 ID 함께 조회
  @Query("SELECT p.petId, p.petName, p.imgId FROM Pet p WHERE p.member.mid = :mid")
  List<Object[]> getPetAndImageByMemberId(@Param("mid") Long mid);
}

// src/main/java/com/seroter/unknownPaw/repository/PetRepository.java
package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.entity.Pet.PetStatus; // PetStatus 임포트
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

  // 📌 [1] 펫 등록/수정 - save() 기본 제공 (새로운 펫은 서비스에서 status를 ACTIVE로 설정)

  // 📌 [2] 펫 삭제 - 본인의 펫만 비활성화 (status = DELETED)
  @Modifying
  @Transactional
  // DELETE 대신 UPDATE 쿼리 사용, status를 DELETED로 설정, ACTIVE 상태인 펫만 대상으로 함
  @Query("UPDATE Pet p SET p.status = 'DELETED' WHERE p.petId = :petId AND p.member.mid = :mid AND p.status = 'ACTIVE'")
  int updatePetStatusToDeleted(@Param("petId") Long petId, @Param("mid") Long mid); // 메서드 이름 변경 권장

  // 📌 [3] 펫 목록 조회 - 특정 회원(mid)의 펫 목록 (페이징 처리)
  // status = 'ACTIVE' 조건 추가
  @Query("SELECT p FROM Pet p WHERE p.member.mid = :mid AND p.status = 'ACTIVE'")
  Page<Pet> getPetsByMemberId(@Param("mid") Long mid, Pageable pageable);

  // 📌 [4] 펫 검색 - 이름으로 부분 검색
  // status = 'ACTIVE' 조건 추가
  @Query("SELECT p FROM Pet p WHERE p.petName LIKE %:petName% AND p.status = 'ACTIVE'")
  List<Pet> searchByPetName(@Param("petName") String petName);

  // 📌 [5] 펫 상세 조회 - petId로 단일 조회
  // status = 'ACTIVE' 조건 추가
  @Query("SELECT p FROM Pet p WHERE p.petId = :petId AND p.status = 'ACTIVE'")
  Optional<Pet> getPetDetail(@Param("petId") Long petId);

  // 📌 [6] 펫 + 멤버 연관 조회 - 펫 정보와 주인 정보 함께 조회
  // status = 'ACTIVE' 조건 추가
  @Query("SELECT p, m FROM Pet p LEFT JOIN p.member m WHERE p.petId = :petId AND p.status = 'ACTIVE'")
  Object[] getPetWithMember(@Param("petId") Long petId);

  // 📌 [7] 펫 + 이미지 ID 조회 - 회원(mid)의 펫 ID + 이름 + 이미지 ID 조회
  // status = 'ACTIVE' 조건 추가
  @Query("""
              SELECT p.petId, p.petName, i.imgId
              FROM Pet p
              LEFT JOIN Image i ON i.pet = p AND i.imageType = 2
              WHERE p.member.mid = :mid AND p.status = 'ACTIVE'
          """)
  List<Object[]> getPetAndImageByMemberId(@Param("mid") Long mid);

  // ✨ 8 특정 회원 페이징이 없는 전체 펫 목록 조회 메서드
  // status = 'ACTIVE' 조건 추가
  @Query("SELECT DISTINCT p FROM Pet p WHERE p.member.mid = :mid AND p.status = 'ACTIVE'")
  List<Pet> findAllByMemberId(@Param("mid") Long mid);

  // 특정회원의 펫 정보 찾기 (status = ACTIVE 조건 추가)
  Optional<Pet> findByPetIdAndMemberAndStatus(Long petId, Member member, PetStatus status);
  // => 사용 시: petRepository.findByPetIdAndMemberAndStatus(petId, member, PetStatus.ACTIVE);

  // 특정회원의 펫 목록 찾기 (status = ACTIVE 조건 추가)
  List<Pet> findByMemberAndStatus(Member member, PetStatus status);
  // => 사용 시: petRepository.findByMemberAndStatus(member, PetStatus.ACTIVE);

  // 모든 활성 펫 조회 (status = ACTIVE 조건 추가)
  List<Pet> findByStatus(PetStatus status);
  // => 사용 시: petRepository.findByStatus(PetStatus.ACTIVE);

  // 기존에 있던 물리적 삭제 메서드들은 사용하지 않도록 주의하거나 제거하는 것이 좋습니다.
  // void deleteById(Long petId);
  // int deletePetByOwner(@Param("petId") Long petId, @Param("mid") Long mid);
}
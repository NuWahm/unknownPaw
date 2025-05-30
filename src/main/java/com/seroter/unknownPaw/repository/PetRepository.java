package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.entity.Pet.PetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

  @Modifying
  @Transactional
  @Query("UPDATE Pet p SET p.status = 'DELETED' WHERE p.petId = :petId AND p.member.mid = :mid AND p.status = 'ACTIVE'")
  int deactivatePetByOwner(@Param("petId") Long petId, @Param("mid") Long mid);

  @Transactional
  void deleteById(Long petId);

  @Query("SELECT p FROM Pet p WHERE p.member.mid = :mid AND p.status = 'ACTIVE'")
  Page<Pet> getActivePetsByMemberId(@Param("mid") Long mid, Pageable pageable);

  @Query("SELECT DISTINCT p FROM Pet p WHERE p.member.mid = :mid AND p.status = 'ACTIVE'")
  List<Pet> findAllActiveByMemberId(@Param("mid") Long mid);

  @Query("SELECT p FROM Pet p WHERE p.petName LIKE %:petName% AND p.status = 'ACTIVE'")
  List<Pet> searchActiveByPetName(@Param("petName") String petName);

  // 👇 서비스에서 이걸 사용하도록 맞추면 됩니다!
  @Query("SELECT p FROM Pet p WHERE p.petId = :petId AND p.status = 'ACTIVE'")
  Optional<Pet> getActivePetDetail(@Param("petId") Long petId);

  @Query("SELECT p, m FROM Pet p LEFT JOIN p.member m WHERE p.petId = :petId AND p.status = 'ACTIVE'")
  Object[] getActivePetWithMember(@Param("petId") Long petId);

  @Query("""
          SELECT p.petId, p.petName, i.imgId
          FROM Pet p
          LEFT JOIN Image i ON i.pet = p AND i.imageType = 2
          WHERE p.member.mid = :mid AND p.status = 'ACTIVE'
      """)
  List<Object[]> getActivePetAndImageByMemberId(@Param("mid") Long mid);

  Optional<Pet> findByPetIdAndMemberAndStatus(Long petId, Member member, PetStatus status);
  List<Pet> findByMemberAndStatus(Member member, PetStatus status);
  List<Pet> findByStatus(PetStatus status);
}
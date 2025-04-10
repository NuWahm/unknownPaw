package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {


  // 역활(role)로 이미지 조회
  List<Image> findByRole(int role);


  // 멤버 프로필 이미지 가져오기
  @Query("SELECT i FROM Image i WHERE i.member.mid = :mid AND i.role = 1")
  Image findMemberProfileImage(@Param("mid") Long mid);


  // 펫 프로필 이미지 가져오기
  @Query("SELECT i FROM Image i WHERE i.pets.petId = :petId AND i.role = 2")
  List<Image> findPetImages(@Param("petId") Long petId);


  // 펫 오너 포스트 이미지 가져오기
  @Query("SELECT i FROM Image i WHERE i.petOwner.petOwnerId = :petOwnerId AND i.role = 3")
  List<Image> findPetOwnerPostImages(@Param("petOwnerId") Long petOwnerId);


  // 펫 시터 포스트 이미지 가져오기
  @Query("SELECT i FROM Image i WHERE i.petSitter.petSitterId = :petSitterId AND i.role = 3")
  List<Image> findPetSitterPostImages(@Param("petSitterId") Long petSitterId);


  // 멤버 이미지 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.member.mid = :mid")
  void deleteByMemberId(@Param("mid") Long mId);

  // 펫 이미지 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.pets.petId = :petId")
  void deleteByPetId(@Param("petId") Long petId);

  // 펫 오너 이미지 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.petOwner.petOwnerId = :petOwnerId")
  void deleteByPetOwnerId(@Param("petOwnerId") Long petOwnerId);

  // 펫 시터 이미지 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.petSitter.petSitterId = :petSitterId")
  void deleteByPetSitterId(@Param("petSitterId") Long petSitterId);


  // uuid 로 이미지 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.uuid = :uuid")
  void deleteByUuid(@Param("uuid") String uuid);

  // 이미지 정보 수정 (파일명, 경로, 원본이름)
  @Modifying
  @Query("UPDATE Image i SET i.filename = :filename, i.path = :path, i.origin = :origin WHERE i.id = :id")
  void updateImageInfo(@Param("id") Long id,
                       @Param("filename") String filename,
                       @Param("path") String path,
                       @Param("origin") String origin);




}

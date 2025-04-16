package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findByRole(int role);

  // 특정 멤버 이미지 한개 가져오는 퀴리문
  @Query("SELECT i FROM Image i WHERE i.member.mid = :mid AND i.role = 1")
  Image findMemberProfileImage(@Param("mid") Long mid);

  // 여러개의 팻이미지 가져오는 쿼리문   List<Image>(여러장의 이미지)
  @Query("SELECT i FROM Image i WHERE i.pet.petId = :petId AND i.role = 2")
  List<Image> findPetImages(@Param("petId") Long petId);

  // 오너 게시글의 여러장의 이미지를 가져오는 쿼리문
  @Query("SELECT i FROM Image i WHERE i.petOwner.postId = :petOwnerId AND i.role = 3")
  List<Image> findPetOwnerPostImages(@Param("petOwnerId") Long petOwnerId);

  // 시터 게시글의 여러장의 이미지를 가져오는 쿼리문
  @Query("SELECT i FROM Image i WHERE i.petSitter.postId = :petSitterId AND i.role = 3")
  List<Image> findPetSitterPostImages(@Param("petSitterId") Long petSitterId);

  // 회원 이미지 전체 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.member.mid = :mid")
  void deleteByMemberId(@Param("mid") Long mid);

  // 펫 이미지 전체 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.pet.petId = :petId")
  void deleteByPetId(@Param("petId") Long petId);

  // 펫 오너 게시판 이미지 전체 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.petOwner.postId = :petOwnerId")
  void deleteByPetOwnerId(@Param("petOwnerId") Long petOwnerId);


  // 펫 시터 게시판 이미지 전체 삭제
  @Modifying
  @Query("DELETE FROM Image i WHERE i.petSitter.postId = :petSitterId")
  void deleteByPetSitterId(@Param("petSitterId") Long petSitterId);

  // 특정 이미지 파일 하나를 uuid 기준으로 삭제
  // 특정 이미지 하나만 정확하게 삭제하고 싶을 때 사용
  @Modifying
  @Query("DELETE FROM Image i WHERE i.uuid = :uuid")
  void deleteByUuid(@Param("uuid") String uuid);


  // 파일 저장 경로 기준으로 삭제
  // 예외 상황 처리할 때 유용 (ex: 이미지 파일은 남았는데 DB만 따로 지워야 할 때 등)
  @Modifying
  @Query("DELETE FROM Image i WHERE i.path = :path")
  void deleteByPath(@Param("path") String path);

  // 특정 이미지의 파일 이름과 경로를 수정하는 쿼리
  @Modifying
  @Query("UPDATE Image i SET i.fileName = :fileName, i.path = :path WHERE i.imgId = :imgId")
  void updateImageInfo(@Param("imgId") Long imgId,
                       @Param("fileName") String fileName,
                       @Param("path") String path);
}

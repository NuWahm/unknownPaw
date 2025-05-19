package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {

    // 펫오너가 작성한 게시글 조회
    List<PetOwner> findByMember_Mid(Long mid);

    // 특정 펫오너 게시글 조회
    Optional<PetOwner> findByPostId(Long postId);

    // 펫오너 랜덤게시물 6개 들고오기
    @Query(value = "SELECT * FROM pet_owner WHERE reg_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<PetOwner> findRecent7DaysRandom6Posts();

}

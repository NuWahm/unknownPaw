package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.entity.PostType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

    // 지역 기반 펫시터 조회
    List<PetSitter> findByDefaultLocation(String location);

    List<PetSitter> findByMember_Mid(Long mid);

    // 최근 7일 이내 펫시터 게시물 중 랜덤 6개
    @Query(value = "SELECT * FROM pet_sitter WHERE reg_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<PetSitter> findRecent7DaysRandom6Posts();


//    메인화면 랜덤하게 게시글 불러오기 (주석처리)
//    @Query("SELECT p FROM PetSitter p WHERE p.postType = :petsitter ORDER BY function('RAND')")
//    List<PetSitter> findRandomPetSitterPosts(@Param("petsitter") PostType petsitter, Pageable pageable);


}

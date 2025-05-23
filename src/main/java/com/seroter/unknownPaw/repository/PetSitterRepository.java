package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetSitter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

    // 지역 기반 펫시터 조회
    List<PetSitter> findByDefaultLocation(String location);

    List<PetSitter> findByMember_Mid(Long mid);

    // 최근 7일 이내 펫시터 게시물 중 랜덤 6개
    @Query(value = "SELECT * FROM pet_sitter WHERE reg_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<PetSitter> findRecent7DaysRandom6Posts();

    // 펫시터 최근 내가쓴글 조회
    int countByMember_Mid(Long mid);
    Optional<PetSitter> findTopByMember_MidOrderByRegDateDesc(Long mid);
}

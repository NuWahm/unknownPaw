package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PetSitterRepository extends PostRepository<PetSitter> {

    @EntityGraph(attributePaths = "images")
    Optional<PetSitter> findById(Long postId);
    // 지역 기반 펫시터 조회
    List<PetSitter> findByDefaultLocation(String location);

    // 최근 7일 이내 펫시터 게시물 중 랜덤 6개
    @Query("SELECT s FROM PetSitter s WHERE s.regDate >= :date")
    List<PetSitter> findRecent7DaysPosts(@Param("date") LocalDateTime date);

    int countByMember_Mid(Long mid);

    Optional<PetSitter> findTopByMember_MidOrderByRegDateDesc(Long mid);
}
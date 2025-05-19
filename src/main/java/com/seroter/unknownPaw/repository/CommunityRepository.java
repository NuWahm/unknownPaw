package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Community;
import com.seroter.unknownPaw.entity.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 게시글 ID로 커뮤니티 게시글 조회
    Community findByCommunityId(Long communityId);
    // 모든 커뮤니티 게시글 조회
    List<Community> findAllByOrderByRegDateDesc();

//    // Community 최근 랜덤게시물 6개 들고오기
//    @Query(value = "SELECT * FROM community WHERE reg_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY RAND() LIMIT 6", nativeQuery = true)
//    List<Community> findRecent7DaysRandom6Community();
}

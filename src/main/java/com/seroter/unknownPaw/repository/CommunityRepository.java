package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 게시글 ID로 커뮤니티 게시글 조회

    Community findByCommunityId(Long communityId);

    // 모든 커뮤니티 게시글 조회
    List<Community> findAllByOrderByRegDateDesc();
}

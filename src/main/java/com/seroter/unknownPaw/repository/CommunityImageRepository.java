package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {

    // 커뮤니티 게시글 ID로 해당 게시글에 속한 이미지들 조회
    List<CommunityImage> findByCommunity_CommunityId(Long communityId);
}

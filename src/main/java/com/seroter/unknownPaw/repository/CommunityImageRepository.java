package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Community;
import com.seroter.unknownPaw.entity.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {

    // 커뮤니티 게시글 ID로 해당 게시글에 속한 이미지들 조회
    List<CommunityImage> findByCommunity_CommunityId(Long communityId);
    List<CommunityImage> findByCommunity(Community community); // 특정 게시글의 이미지 목록 조회
    Optional<CommunityImage> findByCommunityAndCommunityImageUrl(Community community, String communityImageUrl); // 특정 게시글의 특정 이미지 조회 (삭제용)
}

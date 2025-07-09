package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Community;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import com.seroter.unknownPaw.entity.PetOwner;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    // 게시글 ID로 커뮤니티 게시글 조회

//    Community findByCommunityId(Long communityId);

    // 모든 커뮤니티 게시글 조회
//    List<Community> findAllByOrderByRegDateDesc();

    // 커뮤니티 최근 내가 쓴글 조회
    int countByMember_Mid(Long mid);
    Optional<Community> findTopByMember_MidOrderByRegDateDesc(Long mid);

    // 커뮤니티 타입별 조회
//    List<Community> findByCommunityCategory(CommunityCategory category);

    // 커뮤니티 좋아요 글 불러오기
    @Query("SELECT c FROM Community c JOIN c.likedMembers m WHERE m.id = :memberId")
    List<Community> findByLikedMemberId(@Param("memberId") Long memberId);

    // 커뮤니티 좋아요 체크확인 jpql
    boolean existsByCommunityIdAndLikedMembers_Mid(Long communityId, Long memberId);


    //0607
    // 상세 페이지용: findByCommunityId (JPQL 사용) - 이미지도 함께 가져옵니다.
    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.communityImages ORDER BY c.regDate DESC")
    List<Community> findAllByOrderByRegDateDesc();

    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.communityImages WHERE c.communityId = :communityId")
    Community findByCommunityId(Long communityId);

    // 타입별 조회 시 이미지도 함께 가져오기
    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.communityImages WHERE c.communityCategory = :category ORDER BY c.regDate DESC")
    List<Community> findByCommunityCategory(CommunityCategory category); // CommunityService에서 호출될 때 사용




//    // Community 최근 랜덤게시물 6개 들고오기
//    @Query(value = "SELECT * FROM community WHERE reg_date >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY RAND() LIMIT 6", nativeQuery = true)
//    List<Community> findRecent7DaysRandom6Community();
}

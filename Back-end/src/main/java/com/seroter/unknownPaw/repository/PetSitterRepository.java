package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.repository.search.SearchPetSitterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long>, SearchPetSitterRepository {

    Optional<PetSitter> findByPostId(Long postId);

    List<PetSitter> findByMember_Mid(Long mid);

    // 회원이 작성한 게시글 + 좋아요 수 + 채팅 수 (기본 조회)
    @Query("select s, s.member, s.likes, s.chatCount " +
            "from PetSitter s " +
            "where s.member.mid = :mid")
    Page<Object[]> getSitterPostListByMember(Pageable pageable, @Param("mid") Long mid);

    // 특정 게시글 상세 (모든 정보 포함)
    @Query("select s, s.member " +
            "from PetSitter s " +
            "join fetch s.member " +
            "where s.postId = :postId")
    List<Object[]> getSitterPostWithAll(@Param("postId") Long postId);
}

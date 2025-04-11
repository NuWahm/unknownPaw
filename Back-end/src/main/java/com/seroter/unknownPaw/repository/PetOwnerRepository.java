package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.repository.search.SearchPetOwnerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetOwnerRepository extends JpaRepository<PetOwner, Long>, SearchPetOwnerRepository {

    Optional<PetOwner> findByPostId(Long postId);

    List<PetOwner> findByMember_Mid(Long mid);

    // 회원이 작성한 게시글 + 좋아요 수 + 채팅 수 (기본 조회)
    @Query("select o, o.member, o.likes, o.chatCount " +
            "from PetOwner o " +
            "where o.member.mid = :mid")
    Page<Object[]> getOwnerPostListByMember(Pageable pageable, @Param("mid") Long mid);

    // 특정 게시글 상세 (모든 정보 포함)
    @Query("select o, o.member " +
            "from PetOwner o " +
            "join fetch o.member " +
            "where o.postId = :postId")
    List<Object[]> getOwnerPostWithAll(@Param("postId") Long postId);
}

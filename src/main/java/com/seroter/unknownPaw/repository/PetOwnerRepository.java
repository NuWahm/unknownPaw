package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PostType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {

    // 펫오너가 작성한 게시글 조회
    List<PetOwner> findByMember_Mid(Long mid);

    // 특정 펫오너 게시글 조회
    Optional<PetOwner> findByPostId(Long postId);

    @Query(value = "SELECT * FROM pet_owner_post ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<PetOwner> findRandom6Posts();

    @Query("SELECT p FROM PetOwner p ORDER BY function('RAND')")
    List<PetOwner> findRandomPetOwnerPosts(PostType petowner, Pageable pageable);



}

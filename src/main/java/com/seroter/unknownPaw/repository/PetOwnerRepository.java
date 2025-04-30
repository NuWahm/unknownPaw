package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {

    // 펫오너가 작성한 게시글 조회
    List<PetOwner> findByMember_Mid(Long mid);

    // 특정 펫오너 게시글 조회
    Optional<PetOwner> findByPostId(Long postId);


}

package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.entity.PostType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

    // 지역 기반 펫시터 조회
    List<PetSitter> findByDefaultLocation(String location);

    List<PetSitter> findByMember_Mid(Long mid);

    @Query(value = "SELECT * FROM pet_sitter_post ORDER BY RAND() LIMIT 6", nativeQuery = true)
    List<PetSitter> findRandom6Posts();

    @Query("SELECT p FROM PetSitter p ORDER BY function('RAND')")
    List<PetSitter> findRandomPetSitterPosts(PostType petsitter, Pageable pageable);


}

package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetOwner;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PetOwnerRepository extends PostRepository<PetOwner> {
    // 펫오너가 작성한 게시글 조회
    List<PetOwner> findByMember_Mid(Long mid);

    // 펫오너 랜덤게시물 6개 들고오기
    @Query("SELECT s FROM PetOwner s WHERE s.regDate >= :date")
    List<PetOwner> findRecent7DaysPosts(@Param("date") LocalDateTime date);
}

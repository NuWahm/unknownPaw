package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.PetSitter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

    // 지역 기반 펫시터 조회
    List<PetSitter> findByDefaultLocation(String location);

    List<PetSitter> findByMember_Mid(Long mid);

}

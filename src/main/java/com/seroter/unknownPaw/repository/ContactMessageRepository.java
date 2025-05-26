package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    // 특정 회원의 문의 목록 조회
    List<ContactMessage> findByMember_Mid(Long mid);
}

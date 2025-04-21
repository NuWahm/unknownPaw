package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Entry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 📌 [Repository] 게시글 데이터베이스 접근용 인터페이스
 * Spring Data JPA에서 자동 구현됨
 */
public interface EntryRepository extends JpaRepository<Entry, Long> {

    // 📱 앱 무한스크롤 - 최신순 게시글 전체 (처음 로딩)
    List<Entry> findAllByOrderByIdDesc(Pageable pageable);

    // 📱 앱 무한스크롤 - 특정 ID보다 작은 게시글만 (이후 로딩)
    List<Entry> findByIdLessThanOrderByIdDesc(Long id, Pageable pageable);
}

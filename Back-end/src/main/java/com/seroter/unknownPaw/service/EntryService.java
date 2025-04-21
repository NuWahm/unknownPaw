package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.EntryDTO;
import com.seroter.unknownPaw.dto.PageRequestDTO;
import com.seroter.unknownPaw.dto.PageResultDTO;
import com.seroter.unknownPaw.entity.Entry;
import com.seroter.unknownPaw.repository.EntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; // ✅ Spring의 Pageable
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 📌 [Service] 게시글 비즈니스 로직 처리 클래스
 * Controller에서 호출하게 될 실제 서비스 로직 구현
 */
@Service
@RequiredArgsConstructor
public class EntryService {

    private final EntryRepository entryRepository;

    /**
     * 📱 앱 전용 - 무한스크롤 방식으로 게시글 불러오기
     *
     * @param lastEntryId 마지막으로 본 게시글 ID (처음 로딩이면 null)
     * @param size        몇 개씩 불러올지
     */
    public List<EntryDTO> getEntriesForAppScroll(Long lastEntryId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        List<Entry> entries;
        if (lastEntryId == null) {
            // 첫 로딩: 최신 게시글부터
            entries = entryRepository.findAllByOrderByIdDesc(pageable);
        } else {
            // 이후 로딩: ID가 더 작은 게시글만 가져옴
            entries = entryRepository.findByIdLessThanOrderByIdDesc(lastEntryId, pageable);
        }

        return entries.stream()
                .map(EntryDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 💻 웹 전용 - 페이지네이션 방식으로 게시글 불러오기
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 한 페이지당 개수
     */
    public Page<EntryDTO> getEntriesForWebPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        return entryRepository.findAll(pageable)
                .map(EntryDTO::fromEntity);
    }

    public PageResultDTO<EntryDTO, Entry> getEntriesForWebPage(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("id").descending());
        Page<Entry> result = entryRepository.findAll(pageable);

        // Entry를 EntryDTO로 변환하는 함수 전달
        Function<Entry, EntryDTO> fn = (entry -> EntryDTO.builder()
                .id(entry.getId())
                .title(entry.getTitle())
                .content(entry.getContent())
                .build());

        return new PageResultDTO<>(result, fn);
    }

}

package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.EntryDTO;
import com.seroter.unknownPaw.dto.PageRequestDTO;
import com.seroter.unknownPaw.dto.PageResultDTO;
import com.seroter.unknownPaw.service.EntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
public class EntryController {

    private final EntryService entryService;

    /**
     * ✅ 앱 전용: 무한스크롤 방식 게시글 목록
     * - /api/entries/scroll?lastEntryId=10&size=5
     * - 처음 로딩 시 lastEntryId 없이 요청
     */
    @GetMapping("/scroll")
    public ResponseEntity<List<EntryDTO>> getEntriesForAppScroll(
            @RequestParam(required = false) Long lastEntryId,
            @RequestParam(defaultValue = "10") int size) {

        List<EntryDTO> result = entryService.getEntriesForAppScroll(lastEntryId, size);
        return ResponseEntity.ok(result);
    }

    /**
     * ✅ 웹 전용: 페이지네이션 방식 게시글 목록
     * - /api/entries?page=1&size=10
     */
    @GetMapping
    public ResponseEntity<PageResultDTO<EntryDTO, ?>> getEntriesForWeb(
            @ModelAttribute PageRequestDTO pageRequestDTO) {

        PageResultDTO<EntryDTO, ?> result = entryService.getEntriesForWebPage(pageRequestDTO);
        return ResponseEntity.ok(result);
    }
}

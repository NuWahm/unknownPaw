package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.ContactAnswerRequestDTO;
import com.seroter.unknownPaw.dto.ContactMessageRequestDTO;
import com.seroter.unknownPaw.dto.ContactMessageResponseDTO;
import com.seroter.unknownPaw.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // ✅ 1. 문의 등록 (일반 사용자)
    @PostMapping
    public ResponseEntity<String> submitMessage(@RequestBody ContactMessageRequestDTO dto) {
        contactService.submitMessage(dto);
        return ResponseEntity.ok("문의가 접수되었습니다.");
    }

    // ✅ 2. 나의 문의 내역 조회 (마이페이지 등에서 사용)
    @GetMapping("/my")
    public ResponseEntity<List<ContactMessageResponseDTO>> getMyMessages(@RequestParam Long mid) {
        List<ContactMessageResponseDTO> myMessages = contactService.getMyMessages(mid);
        return ResponseEntity.ok(myMessages);
    }

    // ✅ 3. 문의에 대한 관리자 답변 등록 (관리자만 가능)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{messageId}/answer")
    public ResponseEntity<String> submitAnswer(@PathVariable Long messageId,
                                               @RequestBody ContactAnswerRequestDTO dto,
                                               Authentication authentication) {
        contactService.submitAnswer(messageId, dto, authentication);
        return ResponseEntity.ok("답변이 등록되었습니다.");
    }

}

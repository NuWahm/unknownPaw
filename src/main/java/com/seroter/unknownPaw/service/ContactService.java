package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.ContactAnswerRequestDTO;
import com.seroter.unknownPaw.dto.ContactMessageRequestDTO;
import com.seroter.unknownPaw.entity.ContactAnswer;
import com.seroter.unknownPaw.entity.ContactMessage;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.repository.ContactAnswerRepository;
import com.seroter.unknownPaw.repository.ContactMessageRepository;
import com.seroter.unknownPaw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.seroter.unknownPaw.dto.ContactMessageResponseDTO;
import com.seroter.unknownPaw.dto.ContactMessageResponseDTO.ContactAnswerDTO;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository messageRepo;
    private final ContactAnswerRepository answerRepo;
    private final MemberRepository memberRepo;

    // ✅ 1. 문의글 등록
    public void submitMessage(ContactMessageRequestDTO dto) {
        Member member = memberRepo.findById(dto.getMid())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원(mid=" + dto.getMid() + ")이 존재하지 않습니다."));

        ContactMessage message = ContactMessage.builder()
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .member(member)
                .build();

        messageRepo.save(message);
    }

    // ✅ 2. 답변 등록 (관리자 로그인 정보에서 admin 가져옴)
    public void submitAnswer(Long messageId, ContactAnswerRequestDTO dto, Authentication authentication) {
        ContactMessage message = messageRepo.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의가 존재하지 않습니다."));

        // 현재 로그인한 관리자 정보
        String adminEmail = authentication.getName();
        Member admin = memberRepo.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인한 관리자를 찾을 수 없습니다."));

        if (admin.getRole() != Member.Role.ADMIN) {
            throw new SecurityException("관리자 권한이 필요합니다.");
        }

        ContactAnswer answer = ContactAnswer.builder()
                .message(message)
                .admin(admin)
                .content(dto.getContent())
                .build();

        answerRepo.save(answer);
    }

    // ✅ 나의 문의 목록 조회 (memberId 기반)
    public List<ContactMessageResponseDTO> getMyMessages(Long mid) {
        Member member = memberRepo.findById(mid)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        List<ContactMessage> messages = messageRepo.findByMember_Mid(mid);

        return messages.stream().map(message -> ContactMessageResponseDTO.builder()
                        .messageId(message.getMessageId())
                        .subject(message.getSubject())
                        .message(message.getMessage())
                        .createdAt(message.getCreatedAt())
                        .memberName(member.getName())
                        .memberEmail(member.getEmail())
                        .answers(
                                message.getAnswers().stream()
                                        .map(answer -> ContactAnswerDTO.builder()
                                                .answerId(answer.getAnswerId())
                                                .content(answer.getContent())
                                                .adminName(answer.getAdmin().getName())
                                                .createdAt(answer.getCreatedAt())
                                                .build())
                                        .collect(Collectors.toList())
                        )
                        .build())
                .collect(Collectors.toList());
    }

}


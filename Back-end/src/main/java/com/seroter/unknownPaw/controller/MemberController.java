package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.MemberRequestDTO;
import com.seroter.unknownPaw.dto.MemberResponseDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    // ✅ 1. 회원가입
    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("register.....................");
        return ResponseEntity.ok(memberService.register(memberRequestDTO));
    }

    // ✅ 2. 회원 기본 정보 조회 (mid)
    @GetMapping("/{mid}")
    public ResponseEntity<MemberResponseDTO> getMember(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getMember(mid));
    }

    // ✅ 3. 회원 요약 정보 (프로필 등) 조회
    @GetMapping("/profile/simple/{mid}")
    public ResponseEntity<MemberResponseDTO> getSimpleProfile(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getSimpleProfileInfo(mid));
    }

    // ✅ 4. 이메일로 회원 조회 (테스트용)
    @GetMapping("/email")
    public ResponseEntity<Member> findByEmail(@RequestParam String email) {
        return ResponseEntity.of(memberService.findByEmail(email));
    }

    // ✅ 5. 소셜 로그인 시 이메일+소셜 여부로 회원 조회
    @GetMapping("/social")
    public ResponseEntity<Member> findByEmailAndSocial(
            @RequestParam String email,
            @RequestParam boolean fromSocial) {
        return ResponseEntity.of(memberService.findByEmailAndFromSocial(email, fromSocial));
    }

    // ✅ 6. 닉네임 수정
    @PatchMapping("/nickname")
    public ResponseEntity<String> updateNickname(
            @RequestParam Long mid,
            @RequestParam String newNickname) {
        memberService.updateNickname(mid, newNickname);
        return ResponseEntity.ok("닉네임이 성공적으로 변경되었습니다.");
    }

    // ✅ 7. 마이페이지 활동 통계
    @GetMapping("/stats/{mid}")
    public ResponseEntity<Object[]> getMyActivityStats(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getMyActivityStats(mid));
    }

    // ✅ 8. 평점 조회
    @GetMapping("/pawrate/{mid}")
    public ResponseEntity<Float> getPawRate(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getPawRate(mid));
    }

    // ✅ 9. 관리자용 전체 회원 평점
    @GetMapping("/admin/pawrates")
    public ResponseEntity<List<Object[]>> getAllMemberPawRates() {
        return ResponseEntity.ok(memberService.getAllMemberPawRates());
    }

    // ✅ 10. 게시물 통합 조회 (대시보드)
    @GetMapping("/dashboard/{mid}")
    public ResponseEntity<List<Object[]>> getDashboardData(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getDashboardData(mid));
    }
}

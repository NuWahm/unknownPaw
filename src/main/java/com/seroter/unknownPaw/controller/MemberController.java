package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.*;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.security.util.JWTUtil;
import com.seroter.unknownPaw.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    // ✅ 0. 회원가입
    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("register.....................");
        return ResponseEntity.ok(memberService.register(memberRequestDTO));
    }

    // ✅ 1. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {

        Member member = memberService.findByEmail(dto.getEmail())
                .orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("존재하지 않는 계정입니다.");
        }

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("비밀번호가 일치하지 않습니다.");
        }

        try {
            /* ---------- ① role 읽어서 ---------- */
            String role  = member.getRole().name();  // enum 이라면 .name()

            /* ---------- ② 토큰 생성 시 전달 ---------- */
            String token = jwtUtil.generateToken(member.getEmail(), role);

            Map<String, Object> res = new HashMap<>();
            res.put("token",   token);
            res.put("member",  new MemberResponseDTO(member));

            return ResponseEntity.ok(res);

        } catch (Exception ex) {
            log.error("토큰 생성 실패", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("토큰 생성 실패");
        }
    }
    // ✅ 2. 회원 기본 정보 조회 (mid)
    @GetMapping("/id/{mid}")
    public ResponseEntity<MemberResponseDTO> getMember(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getMember(mid));
    }

    // ✅ 3. 회원 요약 정보 (프로필 등) 조회
    @GetMapping("/profile/simple/{mid}")
    public ResponseEntity<MemberResponseDTO> getSimpleProfile(@PathVariable Long mid) {
        System.out.println("요청 simple!! ");
        return ResponseEntity.ok(memberService.getSimpleProfileInfo(mid));
    }
    // 3-1. 특정 회원의 펫 목록 조회
    @GetMapping("/{mid}/pets") // ✨ 새로운 엔드포인트: /api/member/{mid}/pets
    public ResponseEntity<List<PetDTO>> getMemberPets(@PathVariable Long mid) {
        try {
            List<PetDTO> pets = memberService.getMemberPets(mid);
            return ResponseEntity.ok(pets); // 펫이 없으면 빈 리스트 [] 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 3-2 특정 회원이 작성한 게시글 목록 조회
    @GetMapping("/{mid}/posts") // ✨ 새로운 엔드포인트: /api/member/{mid}/posts
    public ResponseEntity<List<PostDTO>> getMemberPosts(@PathVariable Long mid) {
        log.info("getMemberPosts for mid: " + mid);
        try {
            List<PostDTO> posts = memberService.getMemberPosts(mid);
            return ResponseEntity.ok(posts); // 작성한 글이 없으면 빈 리스트 [] 반환
        } catch (Exception e) {
            log.error("회원 게시글 정보 조회 중 오류 발생: " + mid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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
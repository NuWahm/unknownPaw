package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.*;
import com.seroter.unknownPaw.dto.EditProfile.MemberUpdateRequestDTO;
import com.seroter.unknownPaw.dto.EditProfile.PasswordChangeRequestDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.security.util.JWTUtil;
import com.seroter.unknownPaw.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO dto) {
        return memberService.authenticate(dto)
                .map(this::createLoginResponse)
                .orElseGet(this::createErrorResponse);
    }

    private ResponseEntity<Map<String, Object>> createLoginResponse(Member member) {
        log.info("로그인 성공한 회원: {}", member);
        log.info("이메일: {}, 역할: {}", member.getEmail(), member.getRole());

        String token = jwtUtil.generateToken(member.getEmail(), member.getRole().name());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("member", new MemberResponseDTO(member));
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "존재하지 않거나 비밀번호가 일치하지 않는 계정입니다.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    // ✅ 2-2. 회원 정보 업데이트
    @PutMapping("/update") // PUT 메서드 매핑
    public ResponseEntity<?> updateMemberInfo(
            HttpServletRequest request,
            @RequestBody MemberUpdateRequestDTO updateRequestDTO // 요청 본문으로 업데이트할 정보 받기
    ) {
        try {
            // 1. Authorization 헤더에서 토큰 추출 및 검증 (GET /me와 동일)
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmail(token); // JWT에서 이메일 추출

            // 2. 이메일로 사용자 찾기 (GET /me와 동일)
            Member member = memberService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));

            // 3. MemberService를 통해 사용자 정보 업데이트 로직 수행
            memberService.updateMember(member, updateRequestDTO);

            // 4. 업데이트된 사용자 정보 반환 또는 성공 응답
            Member updatedMember = memberService.findByEmail(email) // 업데이트 후 다시 조회 (또는 updateMember 메서드에서 반환)
                    .orElseThrow(() -> new RuntimeException("업데이트된 사용자를 찾을 수 없습니다.")); // 예상치 못한 오류

            return ResponseEntity.ok(new MemberResponseDTO(updatedMember)); // 업데이트된 정보 반환

        } catch (UsernameNotFoundException e) {
            // 사용자를 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // 토큰 오류, 업데이트 중 오류 등 실제 구현에서는 더 구체적인 예외 처리가 필요합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 업데이트 중 오류 발생: " + e.getMessage());
        }
    }

    // ✅ 2-3. 회원의 비밀번호를 수정 - 유효성 검사 등 추가 사항 많아서 따로 뺌
    @PutMapping("/change-password") // 비밀번호 변경 전용 엔드포인트
    public ResponseEntity<?> changePassword(
            HttpServletRequest request,
            @RequestBody PasswordChangeRequestDTO passwordChangeRequestDTO) {

        log.info("change password.................");

        try {
            // 1. Authorization 헤더에서 토큰 추출 및 검증 (다른 엔드포인트와 동일)
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7); // "Bearer " 제거
            String email = jwtUtil.getEmail(token); // JWT에서 이메일 추출

            Member member = memberService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
            // 3. MemberService를 통해 비밀번호 변경 로직 수행
            memberService.changePassword(member, passwordChangeRequestDTO);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다."); // 200 OK

        } catch (UsernameNotFoundException e) {
            log.error("비밀번호 변경 - 사용자 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        } catch (IllegalArgumentException e) {
            // MemberService에서 발생시킨 예외 (예: 현재 비밀번호 불일치, 새 비밀번호 유효성 오류)
            log.error("비밀번호 변경 - 유효성 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 Bad Request
        } catch (Exception e) {
            // 예상치 못한 오류
            log.error("비밀번호 변경 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류 발생: " + e.getMessage());
        }
    }

    // ----------- 타인의 프로필 열람  -----------
    // ✅ 3. 회원 요약 정보 (프로필 등) 조회
    @GetMapping("/profile/simple/{mid}")
    public ResponseEntity<MemberResponseDTO> getSimpleProfile(@PathVariable Long mid) {
        log.info("Requesting simple profile for mid: {}", mid);
        MemberResponseDTO response = memberService.getSimpleProfile(mid); //🤩🤩
        log.info("Returning response: {}", response);
        return ResponseEntity.ok(response);
    }

    // ✅ 3-0 "me" 전용 엔드포인트
    @GetMapping("/profile/simple/me")
    public ResponseEntity<MemberResponseDTO> getSimpleProfileMe(HttpServletRequest request) {
        // 1. JWT 토큰 추출
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmail(token);

        // 2. 이메일로 멤버 조회
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        MemberResponseDTO response = memberService.getSimpleProfile(member.getMid()); // mid는 PK

        return ResponseEntity.ok(response);
    }

    // ✅ 3-1. 특정 회원의 펫 목록 조회
    @GetMapping("/{mid}/pets") // ✨ 새로운 엔드포인트: /api/member/{mid}/pets
    public ResponseEntity<List<PetDTO>> getMemberPets(@PathVariable Long mid) {
        try {
            List<PetDTO> pets = memberService.getMemberPets(mid);
            return ResponseEntity.ok(pets); // 펫이 없으면 빈 리스트 [] 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ✅ 3-2 특정 회원이 작성한 게시글 목록 조회
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

package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.LoginRequestDTO;
import com.seroter.unknownPaw.dto.MemberRequestDTO;
import com.seroter.unknownPaw.dto.MemberResponseDTO;
import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.security.util.JWTUtil;
import com.seroter.unknownPaw.service.MemberService;
import com.seroter.unknownPaw.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
@ToString
public class MemberController {

    private final PetService petService;
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    // ✅ 0. 회원가입
    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("회원가입 요청: {}", memberRequestDTO);
        MemberResponseDTO memberResponseDTO = memberService.register(memberRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponseDTO);
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

    // ✅ 2. 회원 기본 정보 조회 (mid)
    @GetMapping("/id/{mid}")
    public ResponseEntity<MemberResponseDTO> getMember(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getMember(mid));
    }

    // ✅ 3. 회원 요약 정보 (프로필 등) 조회
    @GetMapping("/profile/simple/{mid}")
    public ResponseEntity<MemberResponseDTO> getSimpleProfile(@PathVariable Long mid) {
        log.info("Requesting simple profile for mid: {}", mid);
        MemberResponseDTO response = memberService.getSimpleProfile(mid); //🤩🤩
        log.info("Returning response: {}", response);
        return ResponseEntity.ok(response);
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

    // ✅ 11. 회원의 펫 목록 조회
    @GetMapping(value = "/member/{mid}/pets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PetDTO>> readAllByMember(@PathVariable("mid") Long mid) {
        List<PetDTO> petList = petService.getPetsByOwnerId(mid)
                .stream()
                .map(pet -> new PetDTO(
                        pet.getPetId(),
                        pet.getPetName(),
                        pet.getBreed(),
                        pet.getPetBirth(),
                        pet.isPetGender(),
                        pet.getWeight(),
                        pet.getPetMbti(),
                        pet.isNeutering(),
                        pet.getPetIntroduce(),
                        pet.getMember() != null ? pet.getMember().getMid() : null // 🔥 이거 꼭 넣어야 생성자 일치!
                ))
                .collect(Collectors.toList()); // ← 이제 제대로 작동합니다!
        return ResponseEntity.ok(petList);
    }
}

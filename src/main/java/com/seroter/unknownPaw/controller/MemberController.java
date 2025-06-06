package com.seroter.unknownPaw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seroter.unknownPaw.dto.*;
import com.seroter.unknownPaw.dto.EditProfile.MemberUpdateRequestDTO;
import com.seroter.unknownPaw.dto.EditProfile.PasswordChangeRequestDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.security.util.JWTUtil;
import com.seroter.unknownPaw.service.ImageService;
import com.seroter.unknownPaw.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    // 0. 회원가입
    @PostMapping("/register")
    public ResponseEntity<MemberResponseDTO> register(@RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("register.....................");
        return ResponseEntity.ok(memberService.register(memberRequestDTO));
    }


    @PostMapping(value = "/registerWithImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberResponseDTO> registerWithImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("name") String name,
            @RequestParam("nickname") String nickname,
            @RequestParam("birthday") int birthday,
            @RequestParam("gender") boolean gender,
            @RequestParam("address") String address,
            @RequestParam(value = "petInfo", required = false) String petInfoJson) {
        try {
            PetDTO petInfo = null;
            if (petInfoJson != null && !petInfoJson.isBlank()) {
                petInfo = new ObjectMapper().readValue(petInfoJson, PetDTO.class);
            }
            // 1) 회원 생성
            MemberRequestDTO dto = MemberRequestDTO.builder()
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .name(name)
                    .nickname(nickname)
                    .birthday(birthday)
                    .gender(gender)
                    .address(address)
                    .petInfo(petInfo)
                    .build();

            MemberResponseDTO saved = memberService.register(dto);
            Long mid = saved.getMid();

            // 2) 이미지 저장
            String fileName = imageService.saveImage(
                    file,
                    "member",   // role
                    "member",   // targetType
                    mid,
                    null
            );
            // 만들어진 파일명도 DTO에 담아주면 프론트가 즉시 보여줄 수 있습니다.
            saved.setProfileImagePath("member/" + fileName);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("회원가입+이미지 업로드 실패", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    // 1. 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        Member member = memberService.findByEmail(dto.getEmail()).orElse(null);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 계정입니다.");
        }
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }
        try {
            String role = member.getRole().name();
            String token = jwtUtil.generateToken(member.getEmail(), role);
            Map<String, Object> res = new HashMap<>();
            res.put("token", token);
            res.put("member", new MemberResponseDTO(member));
            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            log.error("토큰 생성 실패", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("토큰 생성 실패");
        }
    }

    // ----------- 내 프로필(마이페이지용) ----------
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentMemberInfo(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmail(token);
            Member member = memberService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
            return ResponseEntity.ok(new MemberResponseDTO(member));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰");
        }
    }

    // ✅ 프론트에서 반드시 필요한 "간단 프로필 + 펫리스트" (예: 게시글 작성 등에서 활용)
    @GetMapping("/profile/simple/me")
    public ResponseEntity<MemberResponseDTO> getSimpleProfileMe(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String email = jwtUtil.getEmail(token);
        Member member = memberService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        MemberResponseDTO response = memberService.getSimpleProfile(member.getMid()); // mid는 PK
        return ResponseEntity.ok(response);
    }

    // ----------- 타인 프로필/조회/기타 ----------
    @GetMapping("/profile/simple/{mid}")
    public ResponseEntity<MemberResponseDTO.Simple> getSimpleProfile(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getSimpleProfileInfo(mid));
    }

    @GetMapping("/{mid}/pets")
    public ResponseEntity<List<PetDTO>> getMemberPets(@PathVariable Long mid) {
        try {
            List<PetDTO> pets = memberService.getMemberPets(mid);
            return ResponseEntity.ok(pets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{mid}/posts")
    public ResponseEntity<List<PostDTO>> getMemberPosts(@PathVariable Long mid) {
        try {
            List<PostDTO> posts = memberService.getMemberPosts(mid);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("회원 게시글 정보 조회 중 오류 발생: " + mid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ----------- 개인정보 수정/비밀번호/탈퇴 ----------
    @PutMapping("/update")
    public ResponseEntity<?> updateMemberInfo(
            HttpServletRequest request,
            @RequestBody MemberUpdateRequestDTO updateRequestDTO
    ) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmail(token);
            Member member = memberService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
            memberService.updateMember(member, updateRequestDTO);
            Member updatedMember = memberService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("업데이트된 사용자를 찾을 수 없습니다."));
            return ResponseEntity.ok(new MemberResponseDTO(updatedMember));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 업데이트 중 오류 발생: " + e.getMessage());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            HttpServletRequest request,
            @RequestBody PasswordChangeRequestDTO passwordChangeRequestDTO) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7);
            String email = jwtUtil.getEmail(token);
            Member member = memberService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
            memberService.changePassword(member, passwordChangeRequestDTO);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (UsernameNotFoundException e) {
            log.error("비밀번호 변경 - 사용자 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("비밀번호 변경 - 유효성 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 중 오류 발생: " + e.getMessage());
        }
    }

    @PutMapping("/withdraw")
    public ResponseEntity<?> withdrawMember(@AuthenticationPrincipal UserDetails userDetails,
                                            @RequestBody(required = false) MemberRequestDTO requestDTO) {
        String email = userDetails.getUsername();
        Long membId = memberService.getMemberIdByEmail(email);
        memberService.withdrawMember(membId, requestDTO);
        return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
    }

    // ----------- 닉네임/이메일/전화번호 중복체크 ----------
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam String nickname) {
        boolean isDuplicated = memberService.isNicknameDuplicated(nickname);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAvailable", !isDuplicated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean isDuplicate = memberService.checkEmailDuplication(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-phone")
    public ResponseEntity<Map<String, Boolean>> checkPhoneNumberExistence(@RequestParam String phoneNumber) {
        boolean exists = memberService.isPhoneNumberExists(phoneNumber);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    // ----------- 마이페이지 통계/평점/대시보드 ----------
    @GetMapping("/stats/{mid}")
    public ResponseEntity<Object[]> getMyActivityStats(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getMyActivityStats(mid));
    }

    @GetMapping("/pawrate/{mid}")
    public ResponseEntity<Float> getPawRate(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getPawRate(mid));
    }

    @GetMapping("/admin/pawrates")
    public ResponseEntity<List<Object[]>> getAllMemberPawRates() {
        return ResponseEntity.ok(memberService.getAllMemberPawRates());
    }

    @GetMapping("/dashboard/{mid}")
    public ResponseEntity<List<Object[]>> getDashboardData(@PathVariable Long mid) {
        return ResponseEntity.ok(memberService.getDashboardData(mid));
    }

    // 찜한 게시글 목록 조회
    @GetMapping("/posts/favourites")
    public ResponseEntity<?> getLikedPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            String email = userDetails.getUsername();
            Member member = memberService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
            
            Page<MemberService.FavouritePostDTO> result = memberService.findLikedPosts(member.getMid(), page, size);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("찜한 게시글 조회 실패", e);
            return ResponseEntity.badRequest().body("찜한 게시글 조회 실패: " + e.getMessage());
        }
    }

}
package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.MemberRequestDTO;
import com.seroter.unknownPaw.dto.MemberResponseDTO;
import com.seroter.unknownPaw.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    // [1] 회원 가입
    @PostMapping(value = "/register")
    public ResponseEntity<Long> register(@RequestBody MemberRequestDTO memberRequestDTO) {
        log.info("register.....................");
        return new ResponseEntity<>(memberService.registerMember(memberRequestDTO), HttpStatus.OK);
    }

    // [2] 회원 정보 조회 (mid로) 마이페이지, 대시보드 조회 등에 사용
    @GetMapping(value = "/{mid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponseDTO> read(@PathVariable("mid") Long mid) {
        return new ResponseEntity<>(memberService.getMember(mid), HttpStatus.OK);
    }

    // [3] 회원 정보 조회 (email로) 로그인 기능, 관리자 페이지 등에서 사용
    @GetMapping(value = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponseDTO> getByEmail(@PathVariable("email") String email) {
        return new ResponseEntity<>(memberService.getMemberByEmail(email), HttpStatus.OK);
    }

    // [4] 회원 정보 수정
    @PutMapping(value = "/{mid}")
    public ResponseEntity<Long> modify(@PathVariable("mid") Long mid, @RequestBody MemberRequestDTO memberRequestDTO) {
        return new ResponseEntity<>(memberService.modifyMember(mid, memberRequestDTO), HttpStatus.OK);
    }

    // [5] 회원 탈퇴 (soft Delete 방식 고려 = MemberService에서)
    @DeleteMapping(value = "/{mid}")
    public ResponseEntity<Void> remove(@PathVariable("mid") Long mid) {
        memberService.removeMember(mid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

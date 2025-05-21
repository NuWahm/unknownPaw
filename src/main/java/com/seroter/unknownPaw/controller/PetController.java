package com.seroter.unknownPaw.controller;


import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.dto.EditProfile.PetUpdateRequestDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.security.util.JWTUtil;
import com.seroter.unknownPaw.service.MemberService;
import com.seroter.unknownPaw.service.PetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/pet")
public class PetController {
  private final PetService petService;
  private final MemberService memberService; // 사용자 식별을 위해 MemberService 필요
  private final JWTUtil jwtUtil; // 토큰에서 사용자 정보 추출을 위해 필요


  // 등록
  @PostMapping(value = "/register")
  public ResponseEntity<Long> register(@RequestBody PetDTO petDTO) {
    log.info("register.................");
    return new ResponseEntity<>(petService.registerPet(petDTO), HttpStatus.OK);
  }

  // 조회
  @GetMapping(value = "/get/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PetDTO> read(@PathVariable("petId") Long petId) {
    return new ResponseEntity<>(petService.getPet(petId), HttpStatus.OK);
  }
  // 수정
//  @PutMapping("/update")
//  public ResponseEntity<Long> update(@RequestBody PetDTO petDTO) {
//    log.info("update.................");
//    return new ResponseEntity<>(petService.updatePet(petDTO), HttpStatus.OK);
//  }

  // 삭제
  @DeleteMapping("/delete/{petId}")
  public ResponseEntity<Void> remove(@PathVariable("petId") Long petId) {
    log.info("delete.................");
//    petService.removePet(petId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  // 개인 정보 수정 - pet 정보 조회하고
  @GetMapping("/me")
  public ResponseEntity<?> getMyPets(HttpServletRequest request) {
    try {
      // 1. Authorization 헤더에서 토큰 추출 및 검증
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7); // "Bearer " 제거
      String email = jwtUtil.getEmail(token); // JWT에서 이메일 추출

      // 2. 이메일로 사용자(Member) 찾기
      Member member = memberService.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

      // 3. PetService를 통해 해당 회원의 펫 목록 조회
      List<PetDTO> myPets = petService.getPetsByMember(member);

      // 4. 조회된 펫 목록 반환
      return ResponseEntity.ok(myPets);

    } catch (UsernameNotFoundException e) {
      // 사용자를 찾을 수 없는 경우 (토큰은 유효하나 DB에 사용자 정보가 없는 경우 등)
      log.error("사용자 찾기 오류: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      // 토큰 파싱 오류, 서비스 로직 오류 등 예상치 못한 오류
      log.error("펫 목록 조회 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 목록 조회 중 오류 발생: " + e.getMessage());
    }
  }


  /**
   * 특정 펫 정보를 조회합니다. (현재 로그인된 회원의 펫만 조회 가능)
   * GET /api/pets/{petId}
   *
   * @param request HttpServletRequest (토큰 추출용)
   * @param petId   조회할 펫의 고유 ID
   * @return 조회된 펫 정보 (PetDTO 형태) 또는 오류 응답
   */
  @GetMapping("/{petId}")
  public ResponseEntity<?> getPet(@PathVariable("petId") Long petId, HttpServletRequest request) {
    try {
      // 1. Authorization 헤더에서 토큰 추출 및 검증
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7); // "Bearer " 제거
      String email = jwtUtil.getEmail(token); // JWT에서 이메일 추출

      // 2. 이메일로 사용자(Member) 찾기
      Member member = memberService.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

      // 3. PetService를 통해 해당 회원의 특정 펫 조회 (서비스 내부에서 소유권 검증)
      PetDTO petDTO = petService.getPet(petId, member);

      // 4. 조회된 펫 정보 반환
      return ResponseEntity.ok(petDTO);

    } catch (UsernameNotFoundException e) {
      log.error("사용자 찾기 오류: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
    } catch (IllegalArgumentException e) {
      // PetService에서 발생시킨 예외 (펫을 찾을 수 없거나 소유권이 없는 경우)
      log.error("펫 조회 권한 오류 또는 펫 없음: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden 또는 404 Not Found
    } catch (Exception e) {
      // 예상치 못한 오류
      log.error("펫 조회 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 조회 중 오류 발생: " + e.getMessage());
    }
  }

  /**
   * 새로운 펫을 등록합니다. (현재 로그인된 회원에게 등록)
   * POST /api/pets
   *
   * @param request HttpServletRequest (토큰 추출용)
   * @param petDTO  등록할 펫 정보 DTO (클라이언트로부터 받음)
   * @return 등록된 펫의 고유 ID 또는 오류 응답
   */
  @PostMapping
  public ResponseEntity<?> registerPet(@RequestBody PetDTO petDTO, HttpServletRequest request) {
    log.info("register pet.................");
    try {
      // 1. Authorization 헤더에서 토큰 추출 및 검증
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7); // "Bearer " 제거
      String email = jwtUtil.getEmail(token); // JWT에서 이메일 추출

      // 2. 이메일로 사용자(Member) 찾기
      Member member = memberService.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

      // 3. PetService를 통해 펫 등록 (회원 정보와 함께 전달)
      Long registeredPetId = petService.registerMyPet(member, petDTO);

      // 4. 등록된 펫 ID 반환
      return new ResponseEntity<>(registeredPetId, HttpStatus.CREATED); // 201 Created

    } catch (UsernameNotFoundException e) {
      log.error("사용자 찾기 오류: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      // 예상치 못한 오류 (예: 데이터 유효성 검사 실패 등)
      log.error("펫 등록 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 등록 중 오류 발생: " + e.getMessage());
    }
  }

  /**
   * 특정 펫 정보를 업데이트합니다. (현재 로그인된 회원의 펫만 수정 가능)
   * PUT /api/pets/{petId}
   *
   * @param request          HttpServletRequest (토큰 추출용)
   * @param petId            업데이트할 펫의 고유 ID
   * @param updateRequestDTO 업데이트할 정보가 담긴 DTO (클라이언트로부터 받음)
   * @return 업데이트된 펫 정보 (PetDTO 형태) 또는 오류 응답
   */
  @PutMapping("/{petId}")
  public ResponseEntity<?> updatePet(
      @PathVariable("petId") Long petId,
      @RequestBody PetUpdateRequestDTO updateRequestDTO, // 업데이트 요청 DTO 사용
      HttpServletRequest request) {

    log.info("update pet................. petId: {}", petId);
    log.info("updateRequestDTO: {}", updateRequestDTO); // 업데이트 요청 DTO 로깅

    try {
      // 1. Authorization 헤더에서 토큰 추출 및 검증
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7); // "Bearer " 제거
      String email = jwtUtil.getEmail(token); // JWT에서 이메일 추출

      // 2. 이메일로 사용자(Member) 찾기
      Member member = memberService.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

      // 3. PetService를 통해 펫 정보 업데이트 (서비스 내부에서 소유권 검증 및 업데이트)
      PetDTO updatedPetDTO = petService.updatePet(petId, member, updateRequestDTO);

      // 4. 업데이트된 펫 정보 반환
      return ResponseEntity.ok(updatedPetDTO); // 200 OK

    } catch (UsernameNotFoundException e) {
      log.error("사용자 찾기 오류: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      // PetService에서 발생시킨 예외 (펫을 찾을 수 없거나 소유권이 없는 경우)
      log.error("펫 업데이트 권한 오류 또는 펫 없음: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
    } catch (Exception e) {
      // 예상치 못한 오류
      log.error("펫 업데이트 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 업데이트 중 오류 발생: " + e.getMessage());
    }
  }

  /**
   * 특정 펫을 삭제합니다. (현재 로그인된 회원의 펫만 삭제 가능)
   * DELETE /api/pets/{petId}
   *
   * @param request HttpServletRequest (토큰 추출용)
   * @param petId   삭제할 펫의 고유 ID
   * @return 성공 응답 또는 오류 응답
   */
  @DeleteMapping("/{petId}")
  public ResponseEntity<?> removePet(@PathVariable("petId") Long petId, HttpServletRequest request) {
    log.info("delete pet................. petId: {}", petId);

    try {
      // 1. Authorization 헤더에서 토큰 추출 및 검증
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7); // "Bearer " 제거
      String email = jwtUtil.getEmail(token); // JWT에서 이메일 추출

      // 2. 이메일로 사용자(Member) 찾기
      Member member = memberService.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

      // 3. PetService를 통해 펫 삭제 (서비스 내부에서 소유권 검증 및 삭제)
      petService.removePet(petId, member);

      // 4. 성공 응답 반환
      return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content

    } catch (UsernameNotFoundException e) {
      log.error("사용자 찾기 오류: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      // PetService에서 발생시킨 예외 (펫을 찾을 수 없거나 소유권이 없는 경우)
      log.error("펫 삭제 권한 오류 또는 펫 없음: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
    } catch (Exception e) {
      // 예상치 못한 오류
      log.error("펫 삭제 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 삭제 중 오류 발생: " + e.getMessage());
    }
  }
}

package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.EditProfile.PetUpdateRequestDTO;
import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.security.util.JWTUtil;
import com.seroter.unknownPaw.service.MemberService;
import com.seroter.unknownPaw.service.PetService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/pet")
@CrossOrigin(origins = "http://localhost:3000")
public class PetController {

  private final PetService petService;
  private final MemberService memberService;
  private final JWTUtil jwtUtil;

  // 펫 등록 (로그인 사용자)
  @PostMapping("/register/later")
  public ResponseEntity<Long> registerPet(
      @RequestBody PetDTO petDTO,
      @AuthenticationPrincipal UserDetails userDetails) {
    log.info("🐾 register pet for user: {}", userDetails.getUsername());
    String email = userDetails.getUsername();
    Member member = memberService.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("로그인된 회원을 찾을 수 없습니다: " + email));
    return ResponseEntity.ok(petService.registerMyPet(member, petDTO));  }

  // 내 펫 목록 (JWT 인증)
  @GetMapping("/me")
  public ResponseEntity<?> getMyPets(HttpServletRequest request) {
    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7);
      String email = jwtUtil.getEmail(token);
      Member member = memberService.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
      List<PetDTO> myPets = petService.getPetsByMember(member);
      return ResponseEntity.ok(myPets);
    } catch (UsernameNotFoundException e) {
      log.error("사용자 찾기 오류: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      log.error("펫 목록 조회 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 목록 조회 중 오류 발생: " + e.getMessage());
    }
  }

  // 단일 펫 조회 (공개)
  @GetMapping("/get/{petId}")
  public ResponseEntity<PetDTO> read(@PathVariable("petId") Long petId) {
    return ResponseEntity.ok(petService.getPet(petId));
  }

  // 펫 정보 수정 (JWT 인증, 펫 소유자만)
  @PutMapping("/{petId}")
  public ResponseEntity<?> updatePet(
      @PathVariable("petId") Long petId,
      @RequestBody PetUpdateRequestDTO updateRequestDTO,
      HttpServletRequest request) {

    log.info("🛠️ update pet................. petId: {}", petId);
    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7);
      String email = jwtUtil.getEmail(token);
      Member member = memberService.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

      PetDTO petDTOForUpdate = PetDTO.builder()
          .petId(petId)
          .petName(updateRequestDTO.getPetName())
          .breed(updateRequestDTO.getBreed())
          .petBirth(updateRequestDTO.getPetBirth())
          .petGender(updateRequestDTO.isPetGender())
          .weight(updateRequestDTO.getWeight())
          .petMbti(updateRequestDTO.getPetMbti())
          .neutering(updateRequestDTO.isNeutering())
          .petIntroduce(updateRequestDTO.getPetIntroduce())
          .build();

      PetDTO updatedPetDTO = petService.updatePet(petId, member, petDTOForUpdate);
      return ResponseEntity.ok(updatedPetDTO);

    } catch (UsernameNotFoundException e) {
      log.error("사용자 찾기 오류: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("펫 업데이트 권한 오류 또는 펫 없음: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    } catch (Exception e) {
      log.error("펫 업데이트 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 업데이트 중 오류 발생: " + e.getMessage());
    }
  }

  // 펫 삭제 (JWT 인증, 소유자만)
  @DeleteMapping("/{petId}")
  public ResponseEntity<?> deletePet(
      @PathVariable("petId") Long petId,
      HttpServletRequest request) {

    log.info("🗑️ DELETE 요청 수신: 펫 ID: {}", petId);

    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 헤더가 누락되었거나 유효하지 않습니다.");
      }
      String token = authHeader.substring(7);
      String email = jwtUtil.getEmail(token);

      petService.deletePet(petId, email);
      return ResponseEntity.ok("펫이 성공적으로 삭제(비활성화)되었습니다.");

    } catch (EntityNotFoundException e) {
      log.warn("펫 삭제 실패 ({}): {}", petId, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (UsernameNotFoundException e) {
      log.error("사용자 찾기 오류 (펫 삭제): {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 사용자 정보입니다.");
    } catch (Exception e) {
      log.error("펫 삭제 중 서버 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 삭제 중 오류가 발생했습니다.");
    }
  }

}
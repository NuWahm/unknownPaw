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
public class PetController {
  private final PetService petService;
  private final MemberService memberService;
  private final JWTUtil jwtUtil;

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


  @PutMapping("/{petId}")
  public ResponseEntity<?> updatePet(
      @PathVariable("petId") Long petId,
      @RequestBody PetUpdateRequestDTO updateRequestDTO, // 업데이트 요청 DTO 사용
      HttpServletRequest request) {

    log.info("update pet................. petId: {}", petId);
    log.info("pet controller updateRequestDTO: {}", updateRequestDTO);

    try {
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
      }
      String token = authHeader.substring(7);
      String email = jwtUtil.getEmail(token);

      Member member = memberService.findByEmail(email)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

      // ✨ 중요: PetUpdateRequestDTO의 정보를 PetDTO로 변환합니다.
      // PetDTO는 모든 펫 정보를 담고, PetUpdateRequestDTO는 업데이트 가능한 필드만 담습니다.
      // 따라서 PetUpdateRequestDTO의 필드를 PetDTO에 매핑하여 넘겨줍니다.
      PetDTO petDTOForUpdate = PetDTO.builder()
          .petId(petId) // 업데이트할 펫 ID를 DTO에 포함시킵니다. (서비스에서 활용될 수 있도록)
          .petName(updateRequestDTO.getPetName())
          .breed(updateRequestDTO.getBreed())
          .petBirth(updateRequestDTO.getPetBirth())
          .petGender(updateRequestDTO.isPetGender())
          .weight(updateRequestDTO.getWeight())
          .petMbti(updateRequestDTO.getPetMbti())
          .neutering(updateRequestDTO.isNeutering())
          .petIntroduce(updateRequestDTO.getPetIntroduce())
          // 나머지 필드 (regDate, modDate, status 등)는 업데이트 요청에 포함되지 않으므로,
          // PetDTO의 생성 시점에 null로 두거나, 서비스 계층에서 기존 값을 유지하도록 합니다.
          // 서비스 계층에서는 기존 Pet 엔티티를 조회하여 수정하는 방식이므로,
          // regDate, modDate, status 등은 DTO에서 넘어오지 않아도 무방합니다.
          .build();


      // 3. PetService를 통해 펫 정보 업데이트
      PetDTO updatedPetDTO = petService.updatePet(petId, member, petDTOForUpdate); // ✨ 수정된 부분

      // 4. 업데이트된 펫 정보 반환
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

  @PostMapping("/register/later")

  public ResponseEntity<Long> registerPet(

      @RequestBody PetDTO petDTO,

      @AuthenticationPrincipal UserDetails userDetails // 현재 로그인된 사용자 정보 주입

  ) {

    log.info("register pet for user: {}", userDetails.getUsername());


// userDetails에서 이메일을 가져와 해당 Member를 찾습니다.

    String loggedInUserEmail = userDetails.getUsername();

    Member member = memberService.findByEmail(loggedInUserEmail)

        .orElseThrow(() -> new EntityNotFoundException("로그인된 회원을 찾을 수 없습니다: " + loggedInUserEmail)); // 적절한 예외 처리


    return new ResponseEntity<>(petService.registerPetLater(petDTO, member), HttpStatus.OK);

  }
}
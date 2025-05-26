package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.EditProfile.PetUpdateRequestDTO;
import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
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

  //  DTO → Entity 변환
  private Pet dtoToEntity(PetDTO dto) {
    return Pet.builder()
        .petId(dto.getPetId())
        .petName(dto.getPetName())
        .breed(dto.getBreed())
        .petBirth(dto.getPetBirth())
        .weight(dto.getWeight())
        .petMbti(dto.getPetMbti())
        .petIntroduce(dto.getPetIntroduce())
        .build();
  }

  //  Entity → DTO 변환
  private PetDTO entityToDTO(Pet pet) {
    return PetDTO.builder()
        .petId(pet.getPetId())
        .petName(pet.getPetName())
        .breed(pet.getBreed())
        .petBirth(pet.getPetBirth())
        .weight(pet.getWeight())
        .petMbti(pet.getPetMbti())
        .petIntroduce(pet.getPetIntroduce())
        .build();
  }

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

  @DeleteMapping("/{petId}")
  public ResponseEntity<?> deletePet(
      @PathVariable("petId") Long petId,
      HttpServletRequest request) {

    log.info("DELETE 요청 수신: 펫 ID: {}", petId);

    try {
      // 1. JWT 토큰을 통해 사용자 이메일 추출
      String authHeader = request.getHeader("Authorization");
      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 헤더가 누락되었거나 유효하지 않습니다.");
      }
      String token = authHeader.substring(7);
      String email = jwtUtil.getEmail(token); // JWT에서 이메일 추출

      // 2. PetService를 호출하여 펫 상태를 'DELETED'로 변경
      petService.deletePet(petId, email);

      // 3. 성공 응답 반환
      return ResponseEntity.ok("펫이 성공적으로 삭제(비활성화)되었습니다.");

    } catch (EntityNotFoundException e) {
      // 펫을 찾을 수 없거나 (ID 오류, 이미 삭제됨) 소유권이 없는 경우
      log.warn("펫 삭제 실패 ({}): {}", petId, e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (UsernameNotFoundException e) {
      // 토큰은 유효하나 해당 이메일의 Member를 찾을 수 없는 경우
      log.error("사용자 찾기 오류 (펫 삭제): {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 사용자 정보입니다.");
    } catch (Exception e) {
      // 그 외 예상치 못한 서버 내부 오류
      log.error("펫 삭제 중 서버 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("펫 삭제 중 오류가 발생했습니다.");
    }
  }
}
package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/pet")
@CrossOrigin(origins = "http://localhost:3000")
public class PetController {

  private final PetService petService;

  // 펫 등록
  @PostMapping("/register")
  public ResponseEntity<Long> register(@RequestBody PetDTO petDTO) {
    log.info("🐾 펫 등록 요청: {}", petDTO);
    return new ResponseEntity<>(petService.registerPet(petDTO), HttpStatus.OK);
  }

  // 펫 조회
  @GetMapping(value = "/get/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PetDTO> read(@PathVariable("petId") Long petId) {
    return new ResponseEntity<>(petService.getPet(petId), HttpStatus.OK);
  }

  // 펫 수정
  @PutMapping("/update")
  public ResponseEntity<Long> update(@RequestBody PetDTO petDTO) {
    log.info("🛠️ 펫 수정 요청: {}", petDTO);
    return new ResponseEntity<>(petService.updatePet(petDTO), HttpStatus.OK);
  }

  // 펫 삭제
  @DeleteMapping("/delete/{petId}")
  public ResponseEntity<Void> remove(@PathVariable("petId") Long petId) {
    log.info("🗑️ 펫 삭제 요청 ID: {}", petId);
    petService.removePet(petId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
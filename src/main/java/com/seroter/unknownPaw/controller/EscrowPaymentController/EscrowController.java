package com.seroter.unknownPaw.controller.EscrowPaymentController;

import com.seroter.unknownPaw.dto.EscrowDTO.escrowCreateRequestDTO;
import com.seroter.unknownPaw.service.EscrowPayment.EscrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/escrow")
public class EscrowController {

    private final EscrowService escrowService;

    /**
     * 에스크로 결제 생성 API
     * 오너가 결제할 때 호출
     */
    @PostMapping("/create")
    public ResponseEntity<escrowCreateRequestDTO> createEscrow(@RequestBody escrowCreateRequestDTO request) {
        // 결제 생성 요청
        escrowCreateRequestDTO response = escrowService.createEscrow(request);
        return ResponseEntity.ok(response);  // 생성된 결제 정보를 반환
    }

    /**
     * 서비스 증거를 승인하는 API
     * 오너가 증거를 보고 승인할 때 호출
     */
    @PostMapping("/approve")
    public ResponseEntity<String> approveProof(@RequestBody escrowApprovalRequest request) {
        escrowService.approveProof(request);  // 승인 처리
        return ResponseEntity.ok("승인이 완료되어 자금이 해제되었습니다.");
    }

    /**
     * 결제 완료 후 에스크로 해제 API
     * 오너가 결제를 승인하고 자금을 해제하는 API
     */
    @PostMapping("/release")
    public ResponseEntity<String> releaseEscrow(@RequestBody Long escrowId) {
        escrowService.releaseEscrow(escrowId);  // 결제 해제 처리
        return ResponseEntity.ok("자금이 성공적으로 해제되었습니다.");
    }
}

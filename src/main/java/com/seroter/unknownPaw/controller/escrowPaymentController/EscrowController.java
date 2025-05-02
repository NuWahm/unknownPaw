package com.seroter.unknownPaw.controller.escrowPaymentController;

import com.seroter.unknownPaw.dto.EscrowDTO.EscrowApprovalRequestDTO;
import com.seroter.unknownPaw.dto.EscrowDTO.EscrowCreateRequestDTO;
import com.seroter.unknownPaw.dto.EscrowDTO.EscrowPaymentDTO;
import com.seroter.unknownPaw.service.escrowPayment.EscrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/escrow")
public class EscrowController {

    private final EscrowService escrowService;

    /**
     * 예약 확정 API (CREAT 상태)
     * 오너와 시터가 약속을 확정할 때 호출
     */
    @PostMapping("/reserve")
    public ResponseEntity<EscrowPaymentDTO> reserveEscrow(@RequestBody EscrowCreateRequestDTO request) {
        EscrowPaymentDTO response = escrowService.createEscrowReservation(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 진행 API (WAITING 상태)
     * 오너가 실제 결제를 진행할 때 호출
     */
    @PostMapping("/pay")
    public ResponseEntity<EscrowPaymentDTO> payEscrow(
            @RequestParam("escrowId") Long escrowId,
            @RequestParam("amount") Long amount
    ) {
        EscrowPaymentDTO response = escrowService.makePayment(escrowId, amount);
        return ResponseEntity.ok(response);
    }

    /**
     * 시터가 서비스 증거를 제출했음을 표시 (PROOF_SUBMITTED 상태로 변경)
     */
    @PostMapping("/submit-proof")
    public ResponseEntity<String> submitProof(@RequestParam("escrowId") Long escrowId) {
        escrowService.updateEscrowStatusToProofSubmitted(escrowId);
        return ResponseEntity.ok("증거가 성공적으로 제출되었습니다.");
    }

    /**
     * 오너가 증거를 승인할 때 호출 (APPROVED 상태로 변경)
     */
    @PostMapping("/approve")
    public ResponseEntity<String> approveProof(@RequestBody EscrowApprovalRequestDTO request) {
        escrowService.approveProof(request);
        return ResponseEntity.ok("승인이 완료되었습니다.");
    }

    /**
     * 자금 해제 (RELEASED 상태)
     * 결제 최종 승인 후 시터에게 자금 전달
     */
    @PostMapping("/release")
    public ResponseEntity<String> releaseEscrow(@RequestParam("escrowId") Long escrowId) {
        escrowService.releaseEscrow(escrowId);
        return ResponseEntity.ok("자금이 성공적으로 해제되었습니다.");
    }
}
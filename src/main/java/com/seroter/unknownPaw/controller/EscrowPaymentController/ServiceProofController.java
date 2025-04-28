package com.seroter.unknownPaw.controller.EscrowPaymentController;

import com.seroter.unknownPaw.dto.EscrowDTO.ServiceProofRequestDTO;
import com.seroter.unknownPaw.service.EscrowPayment.EscrowService;
import com.seroter.unknownPaw.service.EscrowPayment.ServiceProofService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service-proof")
public class ServiceProofController {

    private final ServiceProofService serviceProofService;
    private final EscrowService escrowService;

    /**
     * 서비스 증거 제출 API
     * 시터가 서비스 증거를 제출할 때 호출
     */
    @PostMapping("/submit")
    public ResponseEntity<String> submitServiceProof(@RequestBody ServiceProofRequestDTO request) {
        // 서비스 증거 제출 처리
        serviceProofService.submitProof(request);

        // 에스크로 결제 상태를 'PROOF_SUBMITTED'로 변경
        escrowService.updateEscrowStatusToProofSubmitted(request.getEscrowPaymentId());

        return ResponseEntity.ok("서비스 증거가 제출되었습니다.");
    }
}

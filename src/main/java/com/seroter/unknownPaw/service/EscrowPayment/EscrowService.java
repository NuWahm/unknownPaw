package com.seroter.unknownPaw.service.EscrowPayment;

import com.seroter.unknownPaw.dto.EscrowDTO.EscrowCreateRequestDTO;
import com.seroter.unknownPaw.entity.EscrowEntity.EscrowPayment;
import com.seroter.unknownPaw.entity.EscrowEntity.EscrowStatus;
import com.seroter.unknownPaw.repository.EscrowRepository.EscrowPaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EscrowService {

    private final EscrowPaymentRepository escrowPaymentRepository;

    /**
     * 에스크로 결제 생성
     * 오너가 결제할 때 호출
     */
    @Transactional
    public EscrowPaymentDTO createEscrow(EscrowCreateRequestDTO request) {
        // 결제 생성
        EscrowPayment escrowPayment = EscrowPayment.builder()
                .postId(request.getPostId())
                .amount(request.getAmount())
                .sitterMid(request.getSitterMid())
                .ownerMid(request.getOwnerMid())
                .status(EscrowStatus.WAITING)
                .build();

        escrowPayment = escrowPaymentRepository.save(escrowPayment);  // DB에 저장

        return new EscrowPaymentDTO(escrowPayment.getId(), escrowPayment.getStatus(), escrowPayment.getPaidAt());
    }

    /**
     * 증거 승인
     * 오너가 증거를 보고 승인할 때 호출
     */
    @Transactional
    public void approveProof(EscrowApprovalRequest request) {
        // 해당 에스크로 결제 조회
        EscrowPayment escrowPayment = escrowPaymentRepository.findById(request.getEscrowId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 에스크로 ID입니다."));

        // 증거 제출 상태인지 확인
        if (escrowPayment.getStatus() != EscrowStatus.PROOF_SUBMITTED) {
            throw new IllegalStateException("증거가 제출되지 않았습니다.");
        }

        escrowPayment.setStatus(EscrowStatus.APPROVED);  // 상태 변경
        escrowPaymentRepository.save(escrowPayment);
    }

    /**
     * 결제 승인 후 자금을 해제하는 API
     * 오너가 결제를 승인하고 자금을 해제하는 API
     */
    @Transactional
    public void releaseEscrow(Long escrowId) {
        // 결제 정보 조회
        EscrowPayment escrowPayment = escrowPaymentRepository.findById(escrowId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 에스크로 ID입니다."));

        // 결제가 승인 상태여야 자금 해제 가능
        if (escrowPayment.getStatus() != EscrowStatus.APPROVED) {
            throw new IllegalStateException("승인되지 않은 결제입니다.");
        }

        escrowPayment.setStatus(EscrowStatus.RELEASED);  // 상태 변경
        escrowPaymentRepository.save(escrowPayment);
    }

    /**
     * 결제 상태를 'PROOF_SUBMITTED'로 변경
     */
    @Transactional
    public void updateEscrowStatusToProofSubmitted(Long escrowId) {
        // 결제 정보 조회
        EscrowPayment escrowPayment = escrowPaymentRepository.findById(escrowId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 에스크로 ID입니다."));

        // 결제 상태가 이미 'PROOF_SUBMITTED'인지 확인
        if (escrowPayment.getStatus() == EscrowStatus.PROOF_SUBMITTED) {
            throw new IllegalStateException("이미 증거가 제출되었습니다.");
        }

        // 상태를 'PROOF_SUBMITTED'로 변경
        escrowPayment.setStatus(EscrowStatus.PROOF_SUBMITTED);
        escrowPaymentRepository.save(escrowPayment); // 상태 업데이트
    }
}

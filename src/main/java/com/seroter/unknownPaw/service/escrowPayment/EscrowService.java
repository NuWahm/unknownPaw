package com.seroter.unknownPaw.service.escrowPayment;

import com.seroter.unknownPaw.dto.EscrowDTO.EscrowApprovalRequestDTO;
import com.seroter.unknownPaw.dto.EscrowDTO.EscrowCreateRequestDTO;
import com.seroter.unknownPaw.dto.EscrowDTO.EscrowPaymentDTO;
import com.seroter.unknownPaw.entity.escrowEntity.EscrowPayment;
import com.seroter.unknownPaw.entity.escrowEntity.EscrowStatus;
import com.seroter.unknownPaw.entity.escrowEntity.ProofStatus;
import com.seroter.unknownPaw.entity.escrowEntity.ServiceProof;
import com.seroter.unknownPaw.repository.escrowRepository.EscrowPaymentRepository;
import com.seroter.unknownPaw.repository.escrowRepository.ServiceProofRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Escrow 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class EscrowService {


    private final EscrowPaymentRepository escrowPaymentRepository;
    private final ServiceProofRepository serviceProofRepository;

    /**
     * 예약 확정 (CREAT 상태)
     */
    @Transactional
    public EscrowPaymentDTO createEscrowReservation(EscrowCreateRequestDTO request) {
        EscrowPayment escrowPayment = EscrowPayment.builder()
                .postId(request.getPostId())
                .sitterMid(request.getSitterMid())
                .ownerMid(request.getOwnerMid())
                .status(EscrowStatus.CREAT)
                .build();

        escrowPayment = escrowPaymentRepository.save(escrowPayment);

        return convertToEscrowPaymentDTO(escrowPayment);
    }

    /**
     * 결제 진행 (WAITING 상태)
     */
    @Transactional
    public EscrowPaymentDTO makePayment(Long escrowId, Long amount) {
        EscrowPayment escrowPayment = getEscrowPaymentById(escrowId);

        if (escrowPayment.getStatus() != EscrowStatus.CREAT) {
            throw new IllegalStateException("예약이 확정된 상태에서만 결제가 가능합니다.");
        }

        escrowPayment.setAmount(amount);
        escrowPayment.setStatus(EscrowStatus.WAITING);
        escrowPayment = escrowPaymentRepository.save(escrowPayment);

        return convertToEscrowPaymentDTO(escrowPayment);
    }

    /**
     * 시터가 증거 제출했을 때 상태를 PROOF_SUBMITTED로 변경
     */
    @Transactional
    public void updateEscrowStatusToProofSubmitted(Long escrowId) {
        EscrowPayment escrowPayment = getEscrowPaymentById(escrowId);

        if (escrowPayment.getStatus() == EscrowStatus.PROOF_SUBMITTED) {
            throw new IllegalStateException("이미 증거가 제출된 상태입니다.");
        }

        escrowPayment.setStatus(EscrowStatus.PROOF_SUBMITTED);
        escrowPaymentRepository.save(escrowPayment);
    }

    /**
     * 오너가 증거를 승인할 때 상태를 APPROVED로 변경
     */
    @Transactional
    public void approveProof(EscrowApprovalRequestDTO request) {
        EscrowPayment escrowPayment = getEscrowPaymentById(request.getEscrowPaymentId());

        if (escrowPayment.getStatus() != EscrowStatus.PROOF_SUBMITTED) {
            throw new IllegalStateException("시터가 아직 증거를 제출하지 않았습니다.");
        }

        escrowPayment.setStatus(EscrowStatus.APPROVED);
        escrowPaymentRepository.save(escrowPayment);
    }

    /**
     * 오너가 승인 후, 실제 결제를 시터에게 전달할 때 상태를 RELEASED로 변경
     */
    @Transactional
    public void releaseEscrow(Long escrowId) {
        EscrowPayment escrowPayment = getEscrowPaymentById(escrowId);

        if (escrowPayment.getStatus() != EscrowStatus.APPROVED) {
            throw new IllegalStateException("결제가 승인되지 않은 상태입니다.");
        }

        escrowPayment.setStatus(EscrowStatus.RELEASED);
        escrowPaymentRepository.save(escrowPayment);
    }

    // EscrowService 수정본
    @Transactional
    public void rejectEscrow(Long escrowPaymentId) {
        EscrowPayment escrowPayment = getEscrowPaymentById(escrowPaymentId);

        updateEscrowStatusAndServiceProof(escrowPayment, EscrowStatus.REJECTED, ProofStatus.REJECTED);
        sendRejectionNotification(escrowPayment);
    }


    /**
     * EscrowPayment ID로 에스크로 결제 찾기
     */
    private EscrowPayment getEscrowPaymentById(Long escrowId) {
        return escrowPaymentRepository.findById(escrowId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 에스크로 ID입니다."));
    }

    /**
     * EscrowPaymentDTO로 변환
     */
    private EscrowPaymentDTO convertToEscrowPaymentDTO(EscrowPayment escrowPayment) {
        return new EscrowPaymentDTO(
                escrowPayment.getPaymentId(),
                escrowPayment.getPostId(),
                escrowPayment.getSitterMid(),
                escrowPayment.getOwnerMid(),
                escrowPayment.getAmount(),
                escrowPayment.getStatus().name()
        );
    }

    /**
     * Escrow 상태와 관련된 ServiceProof 상태 업데이트
     */
    private void updateEscrowStatusAndServiceProof(EscrowPayment escrowPayment, EscrowStatus escrowStatus, ProofStatus proofStatus) {
        escrowPayment.setStatus(escrowStatus);
        escrowPaymentRepository.save(escrowPayment);

        if (escrowPayment.getServiceProofs() != null) {
            ServiceProof serviceProof = (ServiceProof) escrowPayment.getServiceProofs();
            serviceProof.setProofStatus(proofStatus);
            serviceProofRepository.save(serviceProof);
        }
    }

    /**
     * 반려 알림 처리
     */
    private void sendRejectionNotification(EscrowPayment escrowPayment) {
        // 실제 알림 전송 로직
        System.out.println("Notification: Payment Rejected for Escrow " + escrowPayment.getPostId());
    }
}
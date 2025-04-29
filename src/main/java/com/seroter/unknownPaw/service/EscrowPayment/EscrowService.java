package com.seroter.unknownPaw.service.EscrowPayment;

import com.seroter.unknownPaw.dto.EscrowDTO.EscrowApprovalRequestDTO;
import com.seroter.unknownPaw.dto.EscrowDTO.EscrowCreateRequestDTO;
import com.seroter.unknownPaw.dto.EscrowDTO.EscrowPaymentDTO;
import com.seroter.unknownPaw.entity.EscrowEntity.EscrowPayment;
import com.seroter.unknownPaw.entity.EscrowEntity.EscrowStatus;
import com.seroter.unknownPaw.repository.EscrowRepository.EscrowPaymentRepository;
import com.seroter.unknownPaw.repository.EscrowRepository.ServiceProofRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EscrowService {

    private final EscrowPaymentRepository escrowPaymentRepository;
    private final ServiceProofRepository serviceProofRepository;

    /**
     * 예약 확정 (CREAT 상태)
     * 오너와 시터가 약속을 확정할 때 호출됨 (결제는 아직 아님)
     */
    @Transactional
    public EscrowPaymentDTO createEscrowReservation(EscrowCreateRequestDTO request) {
        EscrowPayment escrowPayment = EscrowPayment.builder()
                .postId(request.getPostId())
                .sitterMid(request.getSitterMid())
                .ownerMid(request.getOwnerMid())
                .status(EscrowStatus.CREAT)  // 초기 상태는 예약 확정
                .build();

        escrowPayment = escrowPaymentRepository.save(escrowPayment);

        return new EscrowPaymentDTO(
                escrowPayment.getPaymentid(),
                escrowPayment.getPostId(),
                escrowPayment.getSitterMid(),
                escrowPayment.getOwnerMid(),
                escrowPayment.getAmount(),
                escrowPayment.getStatus().name()
        );
    }

    /**
     * 결제 진행 (WAITING 상태)
     * 오너가 실제 결제를 수행할 때 호출됨
     */
    @Transactional
    public EscrowPaymentDTO makePayment(Long escrowId, Long amount) {
        EscrowPayment escrowPayment = escrowPaymentRepository.findById(escrowId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 에스크로 ID입니다."));

        if (escrowPayment.getStatus() != EscrowStatus.CREAT) {
            throw new IllegalStateException("예약이 확정된 상태에서만 결제가 가능합니다.");
        }

        escrowPayment.setAmount(amount);
        escrowPayment.setStatus(EscrowStatus.WAITING); // 결제 완료 상태로 전환
        escrowPayment = escrowPaymentRepository.save(escrowPayment);

        return new EscrowPaymentDTO(
                escrowPayment.getPaymentid(),
                escrowPayment.getPostId(),
                escrowPayment.getSitterMid(),
                escrowPayment.getOwnerMid(),
                escrowPayment.getAmount(),
                escrowPayment.getStatus().name()
        );
    }

    /**
     * 시터가 증거 제출했을 때 상태를 PROOF_SUBMITTED로 변경
     */
    @Transactional
    public void updateEscrowStatusToProofSubmitted(Long escrowId) {
        EscrowPayment escrowPayment = escrowPaymentRepository.findById(escrowId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 에스크로 ID입니다."));

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
        EscrowPayment escrowPayment = escrowPaymentRepository.findById(request.getEscrowPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 에스크로 ID입니다."));

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
        EscrowPayment escrowPayment = escrowPaymentRepository.findById(escrowId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 에스크로 ID입니다."));

        if (escrowPayment.getStatus() != EscrowStatus.APPROVED) {
            throw new IllegalStateException("결제가 승인되지 않은 상태입니다.");
        }

        escrowPayment.setStatus(EscrowStatus.RELEASED);
        escrowPaymentRepository.save(escrowPayment);
    }
}

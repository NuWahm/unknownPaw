package com.seroter.unknownPaw.service.EscrowPayment;

import com.seroter.unknownPaw.dto.EscrowDTO.DisputeCreateRequestDTO;
import com.seroter.unknownPaw.entity.EscrowEntity.Dispute;
import com.seroter.unknownPaw.repository.EscrowRepository.DisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 분쟁 관리 서비스
 */
@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;

    /**
     * 분쟁 생성
     */
    @Transactional
    public Long createDispute(DisputeCreateRequestDTO request) {
        // Dispute 엔티티 생성
        Dispute dispute = Dispute.builder()
                .escrowPaymentId(request.getEscrowPaymentId())
                .ownerMid(request.getOwnerMid())
                .sitterMid(request.getSitterMid())
                .reason(request.getReason())
                .build();

        // 분쟁 저장
        dispute = disputeRepository.save(dispute);

        // 저장된 분쟁 ID 반환
        return dispute.getDisputeId();
    }

    /**
     * 분쟁 조회 (예시: 필요 시 분쟁 조회 메서드 추가 가능)
     */
    @Transactional(readOnly = true)
    public Dispute getDisputeById(Long disputeId) {
        return disputeRepository.findById(disputeId)
                .orElseThrow(() -> new RuntimeException("Dispute not found with id: " + disputeId));
    }
}

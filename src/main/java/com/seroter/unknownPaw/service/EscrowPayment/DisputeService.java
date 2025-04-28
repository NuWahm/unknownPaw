package com.seroter.unknownPaw.service.EscrowPayment;

import com.seroter.unknownPaw.dto.DisputeCreateRequest;
import com.seroter.unknownPaw.entity.Dispute;
import com.seroter.unknownPaw.repository.DisputeRepository;
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
    public Long createDispute(DisputeCreateRequest request) {
        Dispute dispute = Dispute.builder()
                .escrowPaymentId(request.getEscrowPaymentId())
                .ownerMid(request.getOwnerMid())
                .sitterMid(request.getSitterMid())
                .reason(request.getReason())
                .build();

        dispute = disputeRepository.save(dispute);
        return dispute.getId();
    }
}

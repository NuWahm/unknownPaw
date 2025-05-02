package com.seroter.unknownPaw.repository.escrowRepository;

import com.seroter.unknownPaw.entity.escrowEntity.ServiceProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceProofRepository extends JpaRepository<ServiceProof, Long> {

    // 특정 에스크로 결제에 해당하는 모든 서비스 증거 조회
    List<ServiceProof> findByEscrowPayment_PaymentId(Long paymentId);
}
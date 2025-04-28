package com.seroter.unknownPaw.repository.EscrowRepository;

import com.seroter.unknownPaw.entity.EscrowEntity.EscrowPayment;
import com.seroter.unknownPaw.entity.EscrowEntity.EscrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscrowPaymentRepository extends JpaRepository<EscrowPayment, Long> {

    // 특정 게시글(postId)에 대한 에스크로 결제 리스트 조회
    List<EscrowPayment> findByPostId(Long postId);

    // 특정 상태(WAITING, PROOF_SUBMITTED 등)에 해당하는 결제 리스트 조회
    List<EscrowPayment> findByStatus(EscrowStatus status);
}

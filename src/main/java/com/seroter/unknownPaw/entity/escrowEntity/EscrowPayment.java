package com.seroter.unknownPaw.entity.escrowEntity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "escrow_payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EscrowPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId; // 결제 고유 번호 (PK) - 카멜케이스로 수정

    @Column(nullable = false)
    private Long postId; // 해당 결제와 관련된 게시글 ID (Post)

    @Column(nullable = false)
    private Long amount; // 결제 금액

    @Column(nullable = false)
    private Long sitterMid; // 지원자(시터)의 member ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EscrowStatus status; // 결제 상태 (WAITING, PROOF_SUBMITTED, APPROVED, RELEASED)

    private LocalDateTime paidAt; // 결제 완료 시간
    private LocalDateTime releasedAt; // 시터에게 입금된 시간

    @Column(nullable = false)
    private Long ownerMid; // 오너의 member ID (게시물의 주인)

    // 시터가 제출한 증거 목록
    @OneToMany(mappedBy = "escrowPayment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ServiceProof> serviceProofs; // 여러 개의 증거를 저장
}
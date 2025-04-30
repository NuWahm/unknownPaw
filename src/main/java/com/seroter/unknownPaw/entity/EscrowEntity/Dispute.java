package com.seroter.unknownPaw.entity.EscrowEntity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Dispute (분쟁) 엔티티
 */
@Entity
@Table(name = "disputes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long disputeId; // 분쟁 ID (변경)

    @Column(nullable = false)
    private Long escrowPaymentId; // 관련 에스크로 결제 ID

    @Column(nullable = false)
    private Long ownerMid; // 오너 MID

    @Column(nullable = false)
    private Long sitterMid; // 시터 MID

    @Column(nullable = false, length = 1000)
    private String reason; // 분쟁 발생 이유

    private LocalDateTime createdAt; // 분쟁 생성일시

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}

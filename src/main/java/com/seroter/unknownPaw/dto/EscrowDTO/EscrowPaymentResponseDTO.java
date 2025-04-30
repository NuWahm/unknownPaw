package com.seroter.unknownPaw.dto.EscrowDTO;

import com.seroter.unknownPaw.entity.EscrowEntity.EscrowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EscrowPaymentResponseDTO {
    private Long escrowId;           // 생성된 에스크로 ID
    private EscrowStatus status;     // 상태 (기본 WAITING)
    private LocalDateTime paidAt;    // 결제 시간
}

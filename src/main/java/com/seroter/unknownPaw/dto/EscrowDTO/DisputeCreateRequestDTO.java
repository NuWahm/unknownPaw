package com.seroter.unknownPaw.dto.EscrowDTO;

import lombok.Getter;
import lombok.Setter;

/**
 * 분쟁 생성 요청 DTO
 */
@Getter
@Setter
public class DisputeCreateRequestDTO {

    private Long escrowPaymentId; // 관련 에스크로 ID
    private Long ownerMid;         // 오너 MID
    private Long sitterMid;        // 시터 MID
    private String reason;         // 분쟁 이유
}
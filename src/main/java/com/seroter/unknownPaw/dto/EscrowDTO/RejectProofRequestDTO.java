package com.seroter.unknownPaw.dto.EscrowDTO;

import lombok.Getter;
import lombok.Setter;

/**
 * 증거 거절 요청 DTO
 */
@Getter
@Setter
public class RejectProofRequestDTO {

    private Long escrowId; // 에스크로 결제 ID
    private Long ownerMid; // 거절하는 오너 MID
    private String reason; // 거절 사유
}
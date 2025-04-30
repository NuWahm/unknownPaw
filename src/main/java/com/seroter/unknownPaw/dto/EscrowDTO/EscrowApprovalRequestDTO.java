// 오너가 승인할 때 사용하는 요청 DTO
package com.seroter.unknownPaw.dto.EscrowDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EscrowApprovalRequestDTO {
    private Long escrowPaymentId; // 승인 대상 에스크로 결제 ID
    private Long ownerMid;        // 오너의 MID (권한 확인용)
}

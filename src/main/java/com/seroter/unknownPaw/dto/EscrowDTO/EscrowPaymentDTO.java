package com.seroter.unknownPaw.dto.EscrowDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EscrowPaymentDTO {

    private Long escrowId;  // 생성된 에스크로 결제 ID
    private Long postId;    // 게시글 ID
    private Long sitterMid; // 시터의 회원 ID
    private Long ownerMid;  // 오너의 회원 ID
    private Long amount;    // 결제 금액
    private String status;  // 결제 상태
}

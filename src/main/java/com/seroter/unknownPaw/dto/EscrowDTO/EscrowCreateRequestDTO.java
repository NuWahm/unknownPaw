// 에스크로 결제 요청 시 클라이언트가 보내는 데이터 구조
package com.seroter.unknownPaw.dto.EscrowDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
public class EscrowCreateRequestDTO {

    private Long paymentId;
    private Long postId;      // 해당 게시글 ID (오너가 작성한 글)
    private Long sitterMid;   // 시터의 회원 ID
    private Long ownerMid;    // 오너의 회원 ID
    private Long amount;      // 결제 금액 (예: 시간당 요금 * 시간 수)
}

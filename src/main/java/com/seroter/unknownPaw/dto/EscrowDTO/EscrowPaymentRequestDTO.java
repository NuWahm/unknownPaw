package com.seroter.unknownPaw.dto.EscrowDTO;
// 사용자가 결제 요청을 보낼 때 필요한 정보를 담는 DTO


import lombok.Getter;
import lombok.Setter;

// DTO 클래스 선언
@Getter
@Setter
public class EscrowPaymentRequestDTO {

    private Long postId;       // 게시글 ID - 어떤 산책/돌봄 요청에 대한 결제인지 구분
    private Long amount;       // 결제 금액
    private Long sitterMid;    // 시터 회원 ID - 지원자
    private Long ownerMid;     // 오너 회원 ID - 게시글 작성자
}
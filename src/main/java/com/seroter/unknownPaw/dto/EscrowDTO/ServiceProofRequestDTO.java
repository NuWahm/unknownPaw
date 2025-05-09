// 증거 제출 시 사용하는 요청 DTO
package com.seroter.unknownPaw.dto.EscrowDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProofRequestDTO {

    private Long escrowPaymentId;   // 어떤 에스크로 결제에 대한 증거인지 식별
    private String photoPath;       // 증거 사진 경로
    private Double latitude;        // 위도
    private Double longitude;       // 경도
}
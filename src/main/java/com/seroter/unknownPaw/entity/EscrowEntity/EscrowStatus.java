package com.seroter.unknownPaw.entity.EscrowEntity;

public enum EscrowStatus {
    CREAT, // 예약 확정(오너,시터 약속 완료/ 시간,위치,시급 합의완료 상태)
    WAITING, // 결제 대기 (오너가 결제 완료, 시터가 아직 증거 제출 안 한 상태)
    PROOF_SUBMITTED, // 시터가 증거 제출한 상태 (사진, GPS 등)
    APPROVED, // 오너가 증거 승인한 상태
    REJECTED, RELEASED,// 결제가 완료되어 시터에게 입금된 상태
    DISPUTE
}

package com.seroter.unknownPaw.entity.EscrowEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "service_proof")
public class ServiceProof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escrow_payment_id", nullable = false)
    private EscrowPayment escrowPayment; // 해당 결제와 연결된 증거

    private String photoPath; // 증거 이미지 경로 (사진)

    private Double latitude; // 위치 정보 (예: 위도)
    private Double longitude; // 위치 정보 (경도)

    private LocalDateTime submittedAt; // 증거 제출 시간

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }
}

/*
🧩 이런 식으로도 활용 가능해
이미지 여러 장 제출 가능하게 하려면 → OneToMany<ServiceProof> 구조로 구성
위치 추적 여러 번 → location_log 같은 테이블 분리 가능
증거 유효성 판단은 status 필드 추가해서 "검토 중 / 승인 / 반려" 등도 가능

👉 이 테이블이 왜 필요하냐면?
시터가 증거를 제출하지 않으면 오너가 승인할 근거가 없음
승인 조건을 시스템적으로 잡으려면 증거가 DB에 남아야 함
오너도 앱에서 사진/위치 보고 승인할 수 있어야 함.
	*/


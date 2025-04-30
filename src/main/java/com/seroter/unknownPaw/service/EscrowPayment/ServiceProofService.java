package com.seroter.unknownPaw.service.EscrowPayment;

import com.seroter.unknownPaw.dto.EscrowDTO.ServiceProofRequestDTO;
import com.seroter.unknownPaw.entity.EscrowEntity.EscrowPayment;
import com.seroter.unknownPaw.entity.EscrowEntity.EscrowStatus;
import com.seroter.unknownPaw.entity.EscrowEntity.ServiceProof;
import com.seroter.unknownPaw.repository.EscrowRepository.EscrowPaymentRepository;
import com.seroter.unknownPaw.repository.EscrowRepository.ServiceProofRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 생성자 자동 주입
public class ServiceProofService {

    private final EscrowPaymentRepository escrowPaymentRepository;
    private final ServiceProofRepository serviceProofRepository;

    /**
     * 서비스 증거 제출
     */
    @Transactional
    public void submitProof(ServiceProofRequestDTO request) {

        // 1. 에스크로 결제 정보 조회 (없는 경우 예외 발생)
        EscrowPayment escrowPayment = escrowPaymentRepository.findById(request.getEscrowPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 정보를 찾을 수 없습니다."));

        // 2. 증거 엔티티 생성
        ServiceProof proof = ServiceProof.builder()
                .escrowPayment(escrowPayment)         // 어떤 결제에 대한 증거인지 연결
                .photoPath(request.getPhotoPath())     // 사진 경로
                .latitude(request.getLatitude())       // 위도
                .longitude(request.getLongitude())     // 경도
                .build();

        // 3. 증거 저장
        serviceProofRepository.save(proof);

        // 4. 결제 상태를 '증거 제출됨'으로 변경
        escrowPayment.setStatus(EscrowStatus.PROOF_SUBMITTED);
    }
}

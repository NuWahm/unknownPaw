package com.seroter.unknownPaw.repository.escrowRepository;

import com.seroter.unknownPaw.entity.escrowEntity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Dispute (분쟁) 레포지토리
 */
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
}

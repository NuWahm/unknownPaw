package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Entry; // ✅ 반드시 import 필요
import lombok.*;

/**
 * 📌 [DTO] 클라이언트에게 보여줄 게시글 정보 전달 객체
 * Entity ↔ DTO 변환용
 */

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntryDTO {

    private Long id;
    private String title;
    private String content;

    /**
     * Entity → DTO 변환 메서드
     */
    public static EntryDTO fromEntity(Entry entry) {
        return new EntryDTO(entry.getId(), entry.getTitle(), entry.getContent());
    }
}

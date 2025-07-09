package com.seroter.unknownPaw.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {
    private int page; // 요청한 페이지 번호
    private int size; // 한페이지당 갯수
    private String type;
    private String keyword;
    private String sortBy;
    private String sortOrder;

    public PageRequestDTO() {
        this.page = 1;
        this.size = 10;
        this.sortBy = "regDate";
        this.sortOrder = "DESC";
    }

    public Pageable getPageable() {
        int safePage = page < 1 ? 0 : page - 1;
        String sortColumn = (sortBy == null || sortBy.isBlank()) ? "regDate" : sortBy;
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(safePage, size, Sort.by(direction, sortColumn));
    }
}
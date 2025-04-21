package com.seroter.unknownPaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PageRequestDTO {
  private int page; // 요청한 페이지 번호
  private int size; // 한페이지당 갯수
  private String type;
  private String keyword;

  public Pageable getPageable(Sort sort) {
    return PageRequest.of(page - 1, size, sort);
  }
}

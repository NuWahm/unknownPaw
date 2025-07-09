package com.seroter.unknownPaw.repository.search;

import com.seroter.unknownPaw.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPostRepository {
  Page<? extends Post> searchDynamic(String role, String searchType, String keyword, String defaultLocation, String category, Pageable pageable);
  // String searchType 파라미터가 추가되었습니다.
}

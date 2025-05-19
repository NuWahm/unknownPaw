package com.seroter.unknownPaw.repository.search;

import com.seroter.unknownPaw.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchPostRepository {
    Page<? extends Post> searchDynamic(String role, String keyword, String location, String category, Pageable pageable);
}

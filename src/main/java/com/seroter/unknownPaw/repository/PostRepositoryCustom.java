package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {
    Optional<Post> findByPostId(Long postId);

    List<Post> findByMember_Mid(Long mid);

    Page<Object[]> getPostListByMember(Pageable pageable, Long mid);

    List<Object[]> getPostWithAll(Long postId);
}

package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface PostRepository<T extends Post> extends Repository<T, Long> {

    // 게시글 ID로 게시글을 조회하는 메서드
    Optional<T> findByPostId(Long postId);

    // 특정 멤버의 게시글을 조회하는 메서드
    List<T> findByMember_Mid(Long mid);

    // 특정 멤버의 게시글 목록을 페이징 처리하여 조회하는 메서드
    @Query("select p, p.member, p.likes, p.chatCount " +
            "from #{#entityName} p " + // 동적 엔티티 이름 사용
            "where p.member.mid = :mid") // 멤버의 mid에 해당하는 게시글 조회
    Page<Object[]> getPostListByMember(Pageable pageable, @Param("mid") Long mid);

    // 게시글 ID로 해당 게시글과 관련된 멤버 정보를 조회하는 메서드
    @Query("select p, p.member " +
            "from #{#entityName} p " +
            "join fetch p.member " + // 멤버 정보를 함께 조회
            "where p.postId = :postId") // postId로 게시글 조회
    List<Object[]> getPostWithAll(@Param("postId") Long postId);

   // 📌 무한스크롤 및 페이지네이션 처리를 위한 쿼리 메서드
    @Query("SELECT p FROM Post p WHERE (:keyword IS NULL OR p.title LIKE %:keyword%) " +
            "AND (:location IS NULL OR p.defaultLocation LIKE %:location%) " +
            "AND (:category IS NULL OR p.serviceCategory = :category) " +
            "ORDER BY p.regDate DESC")

    // 키워드, 지역, 카테고리 기반 무한스크롤/페이지 처리
    Page<Post> scrollSearch(@Param("keyword") String keyword,
                            @Param("location") String location,
                            @Param("category") String category,
                            Pageable pageable);


}

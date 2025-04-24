package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepositoryImpl implements PostRepository<Post> {

    @PersistenceContext // EntityManager를 사용하여 JPA 쿼리 실행
    private EntityManager em;

    // 게시글 ID로 게시글을 찾는 메서드
    @Override
    public Optional<Post> findByPostId(Long postId) {
        // EntityManager를 사용하여 해당 postId를 가진 게시글을 조회
        return Optional.ofNullable(em.find(Post.class, postId));
    }

    // 특정 멤버의 게시글을 조회하는 메서드
    @Override
    public List<Post> findByMember_Mid(Long mid) {
        // JPQL 쿼리로 member의 mid를 통해 게시글을 조회
        String jpql = "select p from Post p where p.member.mid = :mid";
        return em.createQuery(jpql, Post.class) // JPQL을 실행할 Query 객체 생성
                .setParameter("mid", mid) // mid 파라미터 설정
                .getResultList(); // 결과 반환
    }

    // 특정 멤버의 게시글 리스트를 페이지네이션과 함께 조회하는 메서드
    @Override
    public Page<Object[]> getPostListByMember(Pageable pageable, Long mid) {
        // JPQL 쿼리로 멤버의 게시글 목록을 조회 (게시글, 멤버, 좋아요, 채팅 수 포함)
        String jpql = "select p, p.member, p.likes, p.chatCount from Post p where p.member.mid = :mid";
        List<Object[]> resultList = em.createQuery(jpql, Object[].class) // 쿼리 실행
                .setParameter("mid", mid) // mid 파라미터 설정
                .setFirstResult((int) pageable.getOffset()) // 페이지 오프셋 설정
                .setMaxResults(pageable.getPageSize()) // 페이지 크기 설정
                .getResultList(); // 결과 반환

        // 총 게시글 수 계산 (페이징 처리 시 필요한 총 갯수)
        long total = em.createQuery("select count(p) from Post p where p.member.mid = :mid", Long.class)
                .setParameter("mid", mid) // mid 파라미터 설정
                .getSingleResult(); // 결과 반환

        // PageImpl을 사용하여 페이징된 결과 반환
        return new PageImpl<>(resultList, pageable, total);
    }

    // 게시글 ID로 해당 게시글과 관련된 멤버 정보를 조회하는 메서드
    @Override
    public List<Object[]> getPostWithAll(Long postId) {
        // JPQL 쿼리로 해당 postId를 가진 게시글과 관련된 멤버 정보를 조회
        String jpql = "select p, p.member from Post p join fetch p.member where p.postId = :postId";
        return em.createQuery(jpql, Object[].class) // 쿼리 실행
                .setParameter("postId", postId) // postId 파라미터 설정
                .getResultList(); // 결과 반환
    }

    @Override
    public Page<Post> scrollSearch(String keyword, String location, String category, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("select p from Post p where 1=1 ");

        if (keyword != null && !keyword.isBlank()) {
            jpql.append("and p.title like :keyword ");
        }
        if (location != null && !location.isBlank()) {
            jpql.append("and p.defaultLocation like :location ");
        }
        if (category != null && !category.isBlank()) {
            jpql.append("and p.serviceCategory = :category ");
        }

        jpql.append("order by p.regDate desc");

        var query = em.createQuery(jpql.toString(), Post.class);

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        if (location != null && !location.isBlank()) {
            query.setParameter("location", "%" + location + "%");
        }
        if (category != null && !category.isBlank()) {
            query.setParameter("category", category);
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Post> resultList = query.getResultList();

        StringBuilder countJpql = new StringBuilder("select count(p) from Post p where 1=1 ");
        if (keyword != null && !keyword.isBlank()) {
            countJpql.append("and p.title like :keyword ");
        }
        if (location != null && !location.isBlank()) {
            countJpql.append("and p.defaultLocation like :location ");
        }
        if (category != null && !category.isBlank()) {
            countJpql.append("and p.serviceCategory = :category ");
        }

        var countQuery = em.createQuery(countJpql.toString(), Long.class);
        if (keyword != null && !keyword.isBlank()) {
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }
        if (location != null && !location.isBlank()) {
            countQuery.setParameter("location", "%" + location + "%");
        }
        if (category != null && !category.isBlank()) {
            countQuery.setParameter("category", category);
        }

        Long totalCount = countQuery.getSingleResult();
        return new PageImpl<>(resultList, pageable, totalCount);
    }
}

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
public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext // EntityManager를 사용하여 JPA 쿼리 실행
    private EntityManager em;

    // 게시글 ID로 게시글을 찾는 메서드
    @Override
    public Optional<Post> findByPostId(Long postId) {
        return Optional.ofNullable(em.find(Post.class, postId));
    }

    // 특정 멤버의 게시글을 조회하는 메서드
    @Override
    public List<Post> findByMember_Mid(Long mid) {
        String jpql = "select p from Post p where p.member.mid = :mid";
        return em.createQuery(jpql, Post.class)
                .setParameter("mid", mid)
                .getResultList();
    }

    // 특정 멤버의 게시글 리스트를 페이지네이션과 함께 조회하는 메서드
    @Override
    public Page<Object[]> getPostListByMember(Pageable pageable, Long mid) {
        String jpql = "select p, p.member, p.likes, p.chatCount from Post p where p.member.mid = :mid";
        List<Object[]> resultList = em.createQuery(jpql, Object[].class)
                .setParameter("mid", mid)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = em.createQuery("select count(p) from Post p where p.member.mid = :mid", Long.class)
                .setParameter("mid", mid)
                .getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }

    // 게시글 ID로 해당 게시글과 관련된 멤버 정보를 조회하는 메서드
    @Override
    public List<Object[]> getPostWithAll(Long postId) {
        String jpql = "select p, p.member from Post p join fetch p.member where p.postId = :postId";
        return em.createQuery(jpql, Object[].class)
                .setParameter("postId", postId)
                .getResultList();
    }
}

package com.seroter.unknownPaw.repository.search;

import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.entity.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchPostRepositoryImpl implements SearchPostRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<? extends Post> searchDynamic(String role, String keyword, String location, String category, Pageable pageable) {
        Class<? extends Post> postClass = "petOwner".equals(role) ? PetOwner.class : PetSitter.class;
        String alias = "p";

        StringBuilder jpql = new StringBuilder("select ").append(alias).append(" from ")
                .append(postClass.getSimpleName()).append(" ").append(alias).append(" where 1=1 ");

        if (keyword != null && !keyword.isEmpty()) {
            jpql.append("and ").append(alias).append(".title like :keyword ");
        }

        if (location != null && !location.isEmpty()) {
            jpql.append("and ").append(alias).append(".location = :location ");
        }

        if (category != null && !category.isEmpty()) {
            jpql.append("and ").append(alias).append(".petType = :category ");
        }

        TypedQuery<? extends Post> query = em.createQuery(jpql.toString(), postClass);
        TypedQuery<Long> countQuery = em.createQuery(jpql.toString().replaceFirst("select .* from", "select count(p) from"), Long.class);

        if (keyword != null && !keyword.isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }

        if (location != null && !location.isEmpty()) {
            query.setParameter("location", location);
            countQuery.setParameter("location", location);
        }

        if (category != null && !category.isEmpty()) {
            query.setParameter("category", category);
            countQuery.setParameter("category", category);
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<? extends Post> resultList = query.getResultList();
        Long total = countQuery.getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }
}

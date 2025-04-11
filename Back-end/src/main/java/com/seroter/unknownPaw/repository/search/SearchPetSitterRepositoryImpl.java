package com.seroter.unknownPaw.repository.search;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.entity.QPetSitter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchPetSitterRepositoryImpl implements SearchPetSitterRepository {

    private final EntityManager em;

    @Override
    public Page<PetSitter> searchDynamic(String keyword, String location, String category, Pageable pageable) {
        QPetSitter qPetSitter = QPetSitter;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        var query = queryFactory
                .selectFrom(qPetSitter)
                .where(
                        keywordContains(qPetSitter, keyword),
                        locationContains(qPetSitter, location),
                        categoryEquals(qPetSitter, category)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<PetSitter> results = query.fetch();

        long count = queryFactory
                .select(qPetSitter.count())
                .from(qPetSitter)
                .where(
                        keywordContains(qPetSitter, keyword),
                        locationContains(qPetSitter, location),
                        categoryEquals(qPetSitter, category)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, count);
    }

    private BooleanExpression keywordContains(qPetSitter q, String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        return q.title.containsIgnoreCase(keyword)
                .or(q.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression locationContains(qPetSitter q, String location) {
        if (location == null || location.isEmpty()) return null;
        return q.defaultLocation.containsIgnoreCase(location);
    }

    private BooleanExpression categoryEquals(qPetSitter q, String category) {
        if (category == null || category.isEmpty()) return null;
        return q.serviceCategory.stringValue().eq(category);
    }
}

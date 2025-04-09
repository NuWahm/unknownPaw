package com.seroter.unknownPaw.repository.search;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.QPetOwner;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchPetOwnerRepositoryImpl implements SearchPetOwnerRepository {

    private final EntityManager em;

    @Override
    public Page<PetOwner> searchDynamic(String keyword, String location, String category, Pageable pageable) {
        QPetOwner qPetOwner = QPetOwner.petOwner;
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        var query = queryFactory
                .selectFrom(qPetOwner)
                .where(
                        keywordContains(qPetOwner, keyword),
                        locationContains(qPetOwner, location),
                        categoryEquals(qPetOwner, category)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<PetOwner> results = query.fetch();

        long count = queryFactory
                .select(qPetOwner.count())
                .from(qPetOwner)
                .where(
                        keywordContains(qPetOwner, keyword),
                        locationContains(qPetOwner, location),
                        categoryEquals(qPetOwner, category)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, count);
    }

    private Predicate keywordContains(QPetOwner q, String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;
        return q.title.containsIgnoreCase(keyword)
                .or(q.content.containsIgnoreCase(keyword));
    }

    private BooleanExpression locationContains(QPetOwner q, String location) {
        if (location == null || location.isEmpty()) return null;
        return q.defaultLocation.containsIgnoreCase(location);
    }

    private BooleanExpression categoryEquals(QPetOwner q, String category) {
        if (category == null || category.isEmpty()) return null;
        return q.serviceCategory.stringValue().eq(category);
    }
}

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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Repository
public class SearchPostRepositoryImpl implements SearchPostRepository {

  private static final Logger log = LogManager.getLogger(SearchPostRepositoryImpl.class);

  @PersistenceContext
  private EntityManager em;

  @Override
  // 시그니처 변경: searchType 파라미터 추가
  public Page<? extends Post> searchDynamic(String role, String searchType, String keyword, String defaultLocation, String category, Pageable pageable) {
    log.info("Starting searchDynamic with role: {}, searchType: {}, keyword: {}, defaultLocation: {}, category: {}, pageable: {}", role, searchType, keyword, defaultLocation, category, pageable);

    try {
      Class<? extends Post> postClass = "petOwner".equalsIgnoreCase(role) ? PetOwner.class : PetSitter.class;
      String alias = "p";
      String memberAlias = "m";

      StringBuilder jpql = new StringBuilder("select ").append(alias).append(" from ")
          .append(postClass.getSimpleName()).append(" ").append(alias)
          .append(" LEFT JOIN FETCH ").append(alias).append(".member ").append(memberAlias)
          .append(" where 1=1 ");

      // --- 검색 조건 추가 (searchType 파라미터 활용) ---
      if (keyword != null && !keyword.isEmpty()) {
        if (searchType != null && !searchType.isEmpty()) {
          if ("title".equals(searchType)) {
            jpql.append(" and ").append(alias).append(".title like :keyword");
          } else if ("content".equals(searchType)) {
            jpql.append(" and ").append(alias).append(".content like :keyword");
          } else if ("author".equals(searchType)) {
            jpql.append(" and ").append(memberAlias).append(".nickname like :keyword");
          } else { // 예상치 못한 searchType 값일 경우 기본 검색 (제목 + 내용)
            jpql.append(" and (").append(alias).append(".title like :keyword or ").append(alias).append(".content like :keyword)");
          }
        } else { // searchType이 없을 경우 기본 (제목 + 내용)
          jpql.append(" and (").append(alias).append(".title like :keyword or ").append(alias).append(".content like :keyword)");
        }
      }

      if (defaultLocation != null && !defaultLocation.isEmpty()) {
        jpql.append(" and ").append(alias).append(".defaultLocation = :defaultLocation ");
      }

      if (category != null && !category.isEmpty()) {
        jpql.append(" and ").append(alias).append(".serviceCategory.name() = :category ");
      }

      // ====== 정렬 로직 (이전 답변과 동일, `author` 정렬 포함) ======
      StringBuilder orderByJpql = new StringBuilder();

      if (pageable.getSort().isSorted()) {
        for (Sort.Order order : pageable.getSort()) {
          String frontendProperty = order.getProperty(); // 여기서 `getProperty()`는 `Sort.Order`의 메서드입니다.
          String backendJpqlPath = null;

          if ("regDate".equals(frontendProperty)) {
            backendJpqlPath = "regDate";
          } else if ("likes".equals(frontendProperty)) {
            backendJpqlPath = "likes";
          } else if ("hourlyRate".equals(frontendProperty)) {
            backendJpqlPath = "hourlyRate";
          } else if ("author".equals(frontendProperty)) {
            backendJpqlPath = memberAlias + ".nickname";
          }

          if (backendJpqlPath != null) {
            if (orderByJpql.length() > 0) {
              orderByJpql.append(", ");
            }
            if ("author".equals(frontendProperty)) {
              orderByJpql.append(backendJpqlPath).append(" ").append(order.getDirection().name().toLowerCase(Locale.ROOT));
            } else {
              orderByJpql.append(alias).append(".").append(backendJpqlPath).append(" ").append(order.getDirection().name().toLowerCase(Locale.ROOT));
            }
          } else {
            log.warn("Unsupported sort property requested: {}", frontendProperty);
          }
        }
      }

      if (orderByJpql.length() > 0) {
        jpql.append(" ORDER BY ").append(orderByJpql);
      } else {
        jpql.append(" ORDER BY ").append(alias).append(".regDate DESC");
      }

      TypedQuery<? extends Post> query = em.createQuery(jpql.toString(), postClass);

      String countJpql = jpql.toString()
          .replaceFirst("select " + alias + " from", "select count(distinct " + alias + ") from")
          .replace(" LEFT JOIN FETCH " + alias + ".member " + memberAlias, " LEFT JOIN " + alias + ".member " + memberAlias);

      int orderByPos = countJpql.indexOf(" ORDER BY");
      if (orderByPos != -1) {
        countJpql = countJpql.substring(0, orderByPos);
      }

      log.debug("Constructed Count JPQL: {}", countJpql);
      TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

      // --- 파라미터 바인딩 ---
      if (keyword != null && !keyword.isEmpty()) {
        String paramKeyword = "%" + keyword + "%";
        query.setParameter("keyword", paramKeyword);
        countQuery.setParameter("keyword", paramKeyword);
        log.debug("Bound keyword parameter: {}", paramKeyword);
      }

      if (defaultLocation != null && !defaultLocation.isEmpty()) {
        query.setParameter("defaultLocation", defaultLocation);
        countQuery.setParameter("defaultLocation", defaultLocation);
        log.debug("Bound defaultLocation parameter: {}", defaultLocation);
      }

      if (category != null && !category.isEmpty()) {
        query.setParameter("category", category);
        countQuery.setParameter("category", category);
        log.debug("Bound category parameter: {}", category);
      }

      query.setFirstResult((int) pageable.getOffset());
      query.setMaxResults(pageable.getPageSize());
      log.debug("Pagination applied: offset={}, limit={}", pageable.getOffset(), pageable.getPageSize());

      List<? extends Post> resultList = query.getResultList();
      log.debug("Repository result list size: {}", resultList.size());

      Long total = countQuery.getSingleResult();

      Page<? extends Post> pageResult = new PageImpl<>(resultList, pageable, total);
      log.info("Finished searchDynamic successfully. Total elements: {}", total);
      return pageResult;

    } catch (Exception e) {
      log.error("Error occurred in searchDynamic method: {}", e.getMessage(), e);
      throw new RuntimeException("Error fetching posts", e);
    }
  }
}
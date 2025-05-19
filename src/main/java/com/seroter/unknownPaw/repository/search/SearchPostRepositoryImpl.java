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
import org.springframework.data.domain.Sort; // Sort import 추가
import org.springframework.stereotype.Repository;

import java.util.ArrayList; // Predicate 사용 시 필요 (Criteria API 예시 코드에서 왔을 수 있으나 남겨둠)
import java.util.List;
import java.util.Locale; // getDirection().name() 사용 시 필요

// Log4j2 로거 인스턴스 생성
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Repository
public class SearchPostRepositoryImpl implements SearchPostRepository {

  // 로거 인스턴스 선언
  private static final Logger log = LogManager.getLogger(SearchPostRepositoryImpl.class);

  @PersistenceContext
  private EntityManager em;

  @Override
  public Page<? extends Post> searchDynamic(String role, String keyword, String defaultLocation, String category, Pageable pageable) {
    log.info("Starting searchDynamic with role: {}, keyword: {}, defaultLocation: {}, category: {}, pageable: {}", role, keyword, defaultLocation, category, pageable);

    try {
      Class<? extends Post> postClass = "petOwner".equalsIgnoreCase(role) ? PetOwner.class : PetSitter.class;
      String alias = "p";
      String memberAlias = "m"; // 멤버 엔티티에 대한 별칭 부여

      StringBuilder jpql = new StringBuilder("select ").append(alias).append(" from ")
          .append(postClass.getSimpleName()).append(" ").append(alias)
          .append(" LEFT JOIN ").append(alias).append(".member ").append(memberAlias) // LEFT JOIN FETCH 구문은 별도의 쿼리로 분리하거나 fetch join으로 직접 추가해야 N+1을 막을 수 있으나, 현재 코드 구조에서는 이렇게 유지


          .append(" where 1=1 "); // 기본 WHERE 절 시작

      // ... (검색 조건 추가 기존 코드 유지: keyword, defaultLocation, category) ...
      if (keyword != null && !keyword.isEmpty()) {
        jpql.append(" and (").append(alias).append(".title like :keyword or ").append(alias).append(".content like :keyword)");
      }

      if (defaultLocation != null && !defaultLocation.isEmpty()) {
        jpql.append(" and ").append(alias).append(".defaultLocation = :defaultLocation ");
      }

      if (category != null && !category.isEmpty()) {
        jpql.append(" and ").append(alias).append(".serviceCategory.name() = :category ");
      }


      // ====== 수정된 정렬 로직 ======
      StringBuilder orderByJpql = new StringBuilder(); // ORDER BY 절을 별도로 빌드할 StringBuilder

      if (pageable.getSort().isSorted()) {
        for (Sort.Order order : pageable.getSort()) {
          String frontendProperty = order.getProperty(); // 프론트에서 넘겨준 속성명 (예: "hourlyRate", "likes", "author")
          String backendJpqlPath = null; // 백엔드 엔티티의 실제 JPQL 경로 (예: "desiredHourlyRate", "likes", "member.nickname")

          // --- 프론트 속성명을 백엔드 JPQL 경로로 매핑 ---
          if ("regDate".equals(frontendProperty)) {
            backendJpqlPath = "regDate"; // Post 엔티티에 'regDate' 필드가 있다고 가정
          } else if ("likes".equals(frontendProperty)) {
          } else if ("hourlyRate".equals(frontendProperty)) {
            backendJpqlPath = "desiredHourlyRate";
          } else if ("author".equals(frontendProperty)) {
            backendJpqlPath = "member.nickname"; // 'p.member.nickname' 경로
          }
          // 다른 정렬 기준이 있다면 여기에 else if 블록으로 추가 매핑합니다.

          // --- 매핑된 백엔드 경로가 있다면 ORDER BY 절에 추가 ---
          if (backendJpqlPath != null) {
            if (orderByJpql.length() > 0) {
              orderByJpql.append(", "); // 첫 번째 정렬 기준이 아니면 콤마 추가
            }
            // 엔티티 별칭과 실제 JPQL 경로 및 정렬 방향 추가
            orderByJpql.append(alias).append(".").append(backendJpqlPath).append(" ").append(order.getDirection().name().toLowerCase(Locale.ROOT));
          } else {
            // 지원하지 않는 정렬 속성이 요청된 경우 경고 로깅
            log.warn("Unsupported sort property requested: {}", frontendProperty);
            // 해당 정렬 기준은 무시하고 다음 기준으로 넘어갑니다.
          }
        } // for loop 끝
      }
      // ====== 수정된 정렬 로직 끝 ======

      // ORDER BY 절이 비어있지 않다면 JPQL에 추가
      if (orderByJpql.length() > 0) {
        jpql.append(" ORDER BY ").append(orderByJpql);
      } else {
        // 정렬 기준이 전혀 없거나 지원되지 않는 기준만 있는 경우, 기본 정렬 적용 (선택 사항) 예: 최신순으로 기본 정렬
        jpql.append(" ORDER BY ").append(alias).append(".regDate DESC");
      }


      // 데이터 조회를 위한 TypedQuery 생성 (기존 코드 유지)
      TypedQuery<? extends Post> query = em.createQuery(jpql.toString(), postClass);

      // 전체 카운트를 위한 Count 쿼리 생성 (기존 코드 유지 - ORDER BY 제거 로직 포함)
      String countJpql = jpql.toString().replaceFirst("select .* from", "select count(" + alias + ") from");
      // Count 쿼리에서 ORDER BY 절 제거 (기존 로직 유지)
      int orderByPos = countJpql.indexOf(" ORDER BY");
      if (orderByPos != -1) {
        countJpql = countJpql.substring(0, orderByPos);
      }
      // Count 쿼리에서 LEFT JOIN FETCH 부분도 제거하는 것이 안전할 수 있습니다.
      // 예: String countJpql = "select count(p.postId) from " + postClass.getSimpleName() + " p where ..."; 와 같이 Where 절만 복사하여 사용
      // 현재 코드는 JOIN을 남겨두지만 ORDER BY는 제거합니다. count(p)는 보통 JOIN이 있어도 잘 동작합니다.


      log.debug("Constructed Count JPQL: {}", countJpql);
      TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);


      // ... (파라미터 바인딩 기존 코드 유지) ...
      if (keyword != null && !keyword.isEmpty()) {
        query.setParameter("keyword", "%" + keyword + "%");
        countQuery.setParameter("keyword", "%" + keyword + "%");
        log.debug("Bound keyword parameter: {}", "%" + keyword + "%");
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


      // ... (페이지네이션 적용 및 쿼리 실행 기존 코드 유지) ...
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
      throw new RuntimeException("Error fetching posts", e); // 예외 스택 트레이스 포함하여 다시 던지기 (Controller에서 이 예외를 받게 됩니다)
    }
  }
}
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
      // 역할(role)에 따라 조회할 구체적인 엔티티 클래스 결정 (PetOwner 또는 PetSitter)
      Class<? extends Post> postClass = "petOwner".equalsIgnoreCase(role) ? PetOwner.class : PetSitter.class;
      String alias = "p";
      System.out.println("searchpost role 롤:" + role);

      // JPQL 쿼리 문자열 빌드 시작
      // FROM 절에서 동적으로 결정된 구체 클래스를 사용
      StringBuilder jpql = new StringBuilder("select ").append(alias).append(" from ")
          .append(postClass.getSimpleName()).append(" ").append(alias);

      // ** 여기에서 LEFT JOIN FETCH 구문 추가 **
      // 게시글(p)과 연관된 멤버(member)를 함께 가져오도록 LEFT JOIN FETCH 적용
      // 이렇게 하면 PostDTO 변환 시 post.getMember() 호출 시 추가 쿼리가 발생하지 않음
      String memberAlias = "m"; // 멤버 엔티티에 대한 별칭 부여
      jpql.append(" LEFT JOIN ").append(alias).append(".member ").append(memberAlias);

      jpql.append(" where 1=1 "); // 기본 WHERE 절 시작

      // 검색 조건 추가 (keyword, defaultLocation, category)
      if (keyword != null && !keyword.isEmpty()) {
        jpql.append(" and (").append(alias).append(".title like :keyword or ").append(alias).append(".content like :keyword)"); // 제목 또는 내용 검색
      }

      if (defaultLocation != null && !defaultLocation.isEmpty()) {
        jpql.append(" and ").append(alias).append(".defaultLocation = :defaultLocation ");
      }

      if (category != null && !category.isEmpty()) {
        // 서비스 카테고리 Enum 비교 (DTO에서 String으로 넘어온 Enum 이름과 비교)
        jpql.append(" and ").append(alias).append(".serviceCategory.name() = :category ");
      }

      // ====== 정렬 로직 추가 ======
      if (pageable.getSort().isSorted()) {
        jpql.append(" ORDER BY ");
        boolean first = true;
        for (Sort.Order order : pageable.getSort()) {
          if (!first) {
            jpql.append(", ");
          }
          // 정렬 속성과 방향 추가 (방향은 소문자로 변환)
          jpql.append(alias).append(".").append(order.getProperty()).append(" ").append(order.getDirection().name().toLowerCase(Locale.ROOT));
          first = false;
        }
      }
      // ====== 여기까지 정렬 로직 추가 ======

      log.debug("Constructed JPQL: {}", jpql.toString());

      // 데이터 조회를 위한 TypedQuery 생성
      // 결과 타입은 동적으로 결정된 구체 클래스 타입
      TypedQuery<? extends Post> query = em.createQuery(jpql.toString(), postClass); // 결과 타입은 Post 엔티티 그대로 유지

      // 전체 카운트를 위한 Count 쿼리 생성
      // Count 쿼리는 SELECT count(p) FROM {ConcreteEntity} p WHERE ... 형태가 되어야 합니다.
      // LEFT JOIN FETCH는 Count 쿼리에 불필요하며, 기존 replaceFirst 로직은 "select ... from" 부분을 제거하므로
      // LEFT JOIN FETCH 구문이 FROM 바로 뒤에 있으면 함께 제거됩니다.
      String countJpql = jpql.toString().replaceFirst("select .* from", "select count(" + alias + ") from");
      // Count 쿼리에서 ORDER BY 절 제거
      int orderByPos = countJpql.indexOf(" ORDER BY");
      if (orderByPos != -1) {
        countJpql = countJpql.substring(0, orderByPos);
      }

      log.debug("Constructed Count JPQL: {}", countJpql);
      TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);

      // 파라미터 바인딩
      if (keyword != null && !keyword.isEmpty()) {
        // 키워드 검색 시 두 파라미터 모두 바인딩
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

      // 페이지네이션 적용 (Offset 기반)
      query.setFirstResult((int) pageable.getOffset()); // 현재 페이지의 시작 행 인덱스
      query.setMaxResults(pageable.getPageSize()); // 페이지 당 결과 수
      log.debug("Pagination applied: offset={}, limit={}", pageable.getOffset(), pageable.getPageSize());

      // 쿼리 실행 및 결과 가져오기
      List<? extends Post> resultList = query.getResultList(); // 데이터 목록 조회
      log.debug("Repository result list size: {}", resultList.size());
      System.out.println("밖 멤버"+resultList.get(0).getMember());
      if (!resultList.isEmpty()) {
        log.debug("First post entity from repository: {}", resultList.get(0)); // Post 엔티티 전체 로깅 (ToString 확인)
        log.debug("First post's member entity from repository: {}", resultList.get(0).getMember()); // 멤버 엔티티가 null인지 확인
      }
      Long total = countQuery.getSingleResult(); // 전체 결과 수 조회


      // Service 계층에서 멤버 정보 초기화 필요


      // Spring의 Page 객체 생성 및 반환
      Page<? extends Post> pageResult = new PageImpl<>(resultList, pageable, total);
      log.info("Finished searchDynamic successfully. Total elements: {}", total);
      return pageResult;

    } catch (Exception e) {
      // 예외 발생 시 상세 로깅 후 RuntimeException으로 래핑하여 던짐
      log.error("Error occurred in searchDynamic method: {}", e.getMessage(), e);
      throw new RuntimeException("Error fetching posts", e); // 예외 스택 트레이스 포함하여 다시 던지기
    }
  }
}
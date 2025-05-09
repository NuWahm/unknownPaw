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
  public Page<? extends Post> searchDynamic(String role, String keyword, String location, String category, Pageable pageable) {

    // 요청 파라미터 로깅 (INFO 레벨)
    log.info("Starting searchDynamic with role: {}, keyword: {}, location: {}, category: {}, pageable: {}", role, keyword, location, category, pageable);

    // try-catch 블록으로 쿼리 실행 중 발생할 수 있는 예외를 잡습니다.
    try {
      Class<? extends Post> postClass = "petOwner".equals(role) ? PetOwner.class : PetSitter.class;
      String alias = "p";

      StringBuilder jpql = new StringBuilder("select ").append(alias).append(" from ")
          .append(postClass.getSimpleName()).append(" ").append(alias).append(" where 1=1 ");

      // 검색 조건 추가
      if (keyword != null && !keyword.isEmpty()) {
        jpql.append(" and ").append(alias).append(".title like :keyword ");
      }

      if (location != null && !location.isEmpty()) {
        jpql.append(" and ").append(alias).append(".location = :location ");
      }

      if (category != null && !category.isEmpty()) {
        jpql.append(" and ").append(alias).append(".petType = :category ");
      }

      // ====== 정렬 로직 추가 ======
      if (pageable.getSort().isSorted()) {
        jpql.append(" ORDER BY ");
        boolean first = true;
        for (Sort.Order order : pageable.getSort()) {
          if (!first) {
            jpql.append(", ");
          }
          jpql.append(alias).append(".").append(order.getProperty()).append(" ").append(order.getDirection().name().toLowerCase(Locale.ROOT)); // 대소문자 구분 없이 toLowerCase() 사용
          first = false;
        }
      }
      // ====== 여기까지 정렬 로직 추가 ======

      // 생성된 JPQL 쿼리 로깅 (DEBUG 레벨 - 상세 확인용)
      log.debug("Constructed JPQL: {}", jpql.toString());


      // 데이터 조회를 위한 TypedQuery 생성
      TypedQuery<? extends Post> query = em.createQuery(jpql.toString(), postClass);

      // 전체 카운트를 위한 TypedQuery 생성 (정렬 조건 제거)
      String countJpql = jpql.toString().replaceFirst("select .* from", "select count(" + alias + ") from");
      // 만약 ORDER BY 절이 있다면 count 쿼리에서는 이를 제거합니다.
      int orderByPos = countJpql.indexOf(" ORDER BY");
      if (orderByPos != -1) {
        countJpql = countJpql.substring(0, orderByPos);
      }
      // 생성된 Count JPQL 로깅 (DEBUG 레벨)
      log.debug("Constructed Count JPQL: {}", countJpql);
      TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);


      // 파라미터 바인딩
      if (keyword != null && !keyword.isEmpty()) {
        query.setParameter("keyword", "%" + keyword + "%");
        countQuery.setParameter("keyword", "%" + keyword + "%");
        log.debug("Bound keyword parameter: {}", "%" + keyword + "%");
      }

      if (location != null && !location.isEmpty()) {
        query.setParameter("location", location);
        countQuery.setParameter("location", location);
        log.debug("Bound location parameter: {}", location);
      }

      if (category != null && !category.isEmpty()) {
        query.setParameter("category", category);
        countQuery.setParameter("category", category);
        log.debug("Bound category parameter: {}", category);
      }

      // 페이지네이션 적용
      query.setFirstResult((int) pageable.getOffset());
      query.setMaxResults(pageable.getPageSize());
      log.debug("Pagination applied: offset={}, limit={}", pageable.getOffset(), pageable.getPageSize());


      // 쿼리 실행 및 결과 가져오기
      List<? extends Post> resultList = query.getResultList(); // 이 라인에서 오류가 자주 발생합니다.
      log.info("Fetched {} results for the current page.", resultList.size());


      Long total = countQuery.getSingleResult(); // 이 라인에서도 오류가 발생할 수 있습니다.
      log.info("Fetched total count: {}.", total);


      // Page 객체 생성 및 반환
      Page<Post> pageResult = new PageImpl<>((List<Post>) resultList, pageable, total); // 필요에 따라 캐스팅
      log.info("Finished searchDynamic successfully.");
      return pageResult;

    } catch (Exception e) {
      // 예외 발생 시 ERROR 레벨로 상세 로깅하고, 예외를 다시 던져 상위(Controller)로 전달합니다.
      log.error("Error occurred in searchDynamic method.", e);
      throw new RuntimeException("Error fetching posts", e); // 새로운 RuntimeException으로 감싸서 던질 수도 있습니다.
    }
  }
}
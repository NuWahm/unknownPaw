package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Image;
import com.seroter.unknownPaw.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  // 📌 [1] 소셜 여부와 이메일로 회원 조회 (로그인 시 사용)
  @EntityGraph(attributePaths = {"role"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m WHERE m.email = :email AND m.fromSocial = :fromSocial")
  Optional<Member> findByEmailAndFromSocial(@Param("email") String email, @Param("fromSocial") boolean fromSocial);

  // 📌 [2] 이메일로 회원 조회 (소셜 여부 무시)
  @EntityGraph(attributePaths = {"role"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m WHERE m.email = :email")
  Optional<Member> findByEmail(@Param("email") String email);

  // 📌 [3] 회원 + PetOwner(댕댕이) 글목록 연관 조회
  @EntityGraph(attributePaths = {"role", "status"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH PetOwner po ON po.member = m WHERE m.id = :mid")
  Optional<Member> findMemberWithPetOwners(@Param("mid") Long mid);

  // 📌 [4] 회원 + PetSitter(오댕이) 글목록 연관 조회
  @EntityGraph(attributePaths = {"role", "status"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH PetSitter ps ON ps.member = m WHERE m.id = :mid")
  Optional<Member> findMemberWithPetSitters(@Param("mid") Long mid);

  // 📌 [5] 회원 + PetOwner + PetSitter + DateAppoint 통합 조회 (대시보드 용)
  @Query("""
          SELECT m, po, ps, da 
          FROM Member m
          LEFT JOIN PetOwner po ON po.member = m
          LEFT JOIN PetSitter ps ON ps.member = m
          LEFT JOIN DateAppoint da ON da.petOwnerId = po.petOwnerId OR da.petSitterId = ps.petSitterId
          WHERE m.id = :mid
      """)
  List<Object[]> findMemberWithAllData(@Param("mid") Long mid);

  // NativeQuery 버전
//  @Query(value = """
//    SELECT m.*, po.*, ps.*, da.*
//    FROM member m
//    LEFT JOIN pet_owner po ON po.member_id = m.id
//    LEFT JOIN pet_sitter ps ON ps.member_id = m.id
//    LEFT JOIN date_appoint da ON da.pet_owner_id = po.id OR da.pet_sitter_id = ps.id
//    WHERE m.id = :mid
//    """, nativeQuery = true)
//  List<Object[]> findMemberWithAllDataNative(@Param("mid") Long mid);

  // 📌 [6] 마이페이지용 활동 내역 조회
  // 댕댕이 글 수, 오댕이 글 수, 약속 수 같은 것들을 한 번에 조회
  @Query("""
      SELECT m, COUNT(DISTINCT po), COUNT(DISTINCT ps), COUNT(DISTINCT da)
      FROM Member m 
      LEFT JOIN PetOwner po ON po.member = m 
      LEFT JOIN PetSitter ps ON ps.member = m 
      LEFT JOIN DateAppoint da ON da.petOwnerId = po.petOwnerId OR da.petSitterId = ps.petSitterId
      WHERE m.id = :mid
      GROUP BY m
      """)
  Object[] findMyActivityStats(@Param("mid") Long mid);

  //  📌 [7] 평점
  // 특정 회원의 pawRate 조회 ,  마이페이지 또는 상대방 프로필에서 해당 회원의 평점 조회
  @Query("SELECT m.pawRate FROM Member m WHERE m.id = :mid")
  Float findPawRateByMemberId(@Param("mid") Long mid);

  // 모든 회원의 id, email, pawRate 조회,  관리자 페이지, 추천 알고리즘, 대시보드 등에서 사용 가능
  @Query("SELECT m.id, m.email, m.pawRate FROM Member m")
  List<Object[]> findAllMemberPawRates();

  // 📌 [8] 상대방 프로필 요약 정보 조회
  @Query("""
      SELECT m.id, m.nickname, m.pawRate, i.path 
      FROM Member m 
      LEFT JOIN Image i ON i.member = m AND i.role = 1 
      WHERE m.id = :mid
      """)
  Optional<Object[]> findSimpleProfileInfo(@Param("mid") Long mid);

}

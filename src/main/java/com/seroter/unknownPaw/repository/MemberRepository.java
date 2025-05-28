package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.dto.MemberResponseDTO;
import com.seroter.unknownPaw.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

  // 📌 [0] 회원 ID로 조회
  Optional<Member> findByMid(Long mid);

  // 📌 [1] 소셜 여부 + 이메일로 회원 조회 (로그인)
  @EntityGraph(attributePaths = {"role"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m WHERE m.email = :email AND m.fromSocial = :fromSocial")
  Optional<Member> findByEmailAndFromSocial(@Param("email") String email, @Param("fromSocial") boolean fromSocial);

  // 📌 [2] 이메일로 회원 조회 (소셜 여부 무시)
  @EntityGraph(attributePaths = {"role"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m WHERE m.email = :email")
  Optional<Member> findByEmail(@Param("email") String email);

  // 📌 [3] 닉네임으로 회원 조회
  Optional<Member> findByNickname(String nickname);

  // 📌 [4] 이메일/휴대폰 중복 검사
  boolean existsByEmail(String email);
  boolean existsByPhoneNumber(String phoneNumber);

  // 📌 [5] 회원 + PetOwner 연관 조회
  @EntityGraph(attributePaths = {"role", "status"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH PetOwner po ON po.member = m WHERE m.mid = :mid")
  Optional<Member> findMemberWithPetOwners(@Param("mid") Long mid);

  // 📌 [6] 회원 + PetSitter 연관 조회
  @EntityGraph(attributePaths = {"role", "status"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH PetSitter ps ON ps.member = m WHERE m.mid = :mid")
  Optional<Member> findMemberWithPetSitters(@Param("mid") Long mid);

  // 📌 [7] 회원 + PetOwner + PetSitter + DateAppoint 통합 조회 (대시보드 용)
  @Query("""
        SELECT m, po, ps, da
        FROM Member m
        LEFT JOIN PetOwner po ON po.member = m
        LEFT JOIN PetSitter ps ON ps.member = m
        LEFT JOIN DateAppoint da ON da.petOwnerPost = po OR da.petSitterPost = ps
        WHERE m.mid = :mid
        """)
  List<Object[]> findMemberWithAllData(@Param("mid") Long mid);

  // 📌 [8] 마이페이지용 활동 내역 조회
  @Query("""
        SELECT m.mid,
               COUNT(DISTINCT po),
               COUNT(DISTINCT ps),
               COUNT(DISTINCT da.rno)
        FROM Member m
        LEFT JOIN PetOwner po ON po.member = m
        LEFT JOIN PetSitter ps ON ps.member = m
        LEFT JOIN DateAppoint da ON da.petOwnerPost = po OR da.petSitterPost = ps
        WHERE m.mid = :mid
        GROUP BY m.mid
        """)
  Object[] findMyActivityStats(@Param("mid") Long mid);

  // 📌 [9] 평점만 조회
  @Query("SELECT m.pawRate FROM Member m WHERE m.mid = :mid")
  Float findPawRateByMemberId(@Param("mid") Long mid);

  // 📌 [10] 모든 회원의 mid, email, pawRate 조회 (관리자용)
  @Query("SELECT m.mid, m.email, m.pawRate FROM Member m")
  List<Object[]> findAllMemberPawRates();

  // 📌 [11] 상대방 프로필 요약 정보 조회 (간략)
  @Query("SELECT new com.seroter.unknownPaw.dto.MemberResponseDTO$Simple(m.mid, m.nickname, m.pawRate, i.path) " +
          "FROM Member m LEFT JOIN Image i ON i.member = m AND i.imageType = 1 WHERE m.mid = :mid")
  Optional<MemberResponseDTO.Simple> findSimpleProfileInfo(@Param("mid") Long mid);

  // 📌 [12] 상대방 프로필 요약 정보 조회 (DTO 사용 시)
  @Query("""
        SELECT new com.seroter.unknownPaw.dto.MemberResponseDTO$Simple(
            m.mid, m.nickname, m.pawRate, i.path
        )
        FROM Member m
        LEFT JOIN Image i ON i.member = m AND i.imageType = 1
        WHERE m.mid = :mid
        """)
  Optional<MemberResponseDTO.Simple> findSimpleProfileInfoDto(@Param("mid") Long mid);

  // 📌 [13] 회원 + 좋아요 게시글 전체 fetch (PetOwner, PetSitter, Community)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH m.likedPetOwner " +
          "LEFT JOIN FETCH m.likedPetSitter " +
          "LEFT JOIN FETCH m.likedCommunity " +
          "WHERE m.mid = :mid")
  Optional<Member> fetchWithLikes(@Param("mid") Long mid);

  // 📌 [14] 상세 프로필 (펫 포함)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH m.pets WHERE m.mid = :mid")
  Optional<Member> findSimpleProfile(@Param("mid") Long mid);

  // 📌 [15] 이메일로 펫 포함 회원 조회
  @Query("SELECT m FROM Member m LEFT JOIN FETCH m.pets WHERE m.email = :email")
  Optional<Member> findByEmailWithPets(@Param("email") String email);

}
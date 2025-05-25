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

  Optional<Member> findByMid(Long mid);

  @EntityGraph(attributePaths = {"role"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m WHERE m.email = :email AND m.fromSocial = :fromSocial")
  Optional<Member> findByEmailAndFromSocial(@Param("email") String email, @Param("fromSocial") boolean fromSocial);

  @EntityGraph(attributePaths = {"role"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m WHERE m.email = :email")
  Optional<Member> findByEmail(@Param("email") String email);

  Optional<Member> findByNickname(String nickname);

  boolean existsByEmail(String email);
  boolean existsByPhoneNumber(String phoneNumber);

  @EntityGraph(attributePaths = {"role", "status"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH PetOwner po ON po.member = m WHERE m.mid = :mid")
  Optional<Member> findMemberWithPetOwners(@Param("mid") Long mid);

  @EntityGraph(attributePaths = {"role", "status"}, type = EntityGraph.EntityGraphType.LOAD)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH PetSitter ps ON ps.member = m WHERE m.mid = :mid")
  Optional<Member> findMemberWithPetSitters(@Param("mid") Long mid);

  @Query("""
            SELECT m, po, ps, da
            FROM Member m
            LEFT JOIN PetOwner po ON po.member = m
            LEFT JOIN PetSitter ps ON ps.member = m
            LEFT JOIN DateAppoint da ON da.petOwnerPost = po OR da.petSitterPost = ps
            WHERE m.mid = :mid
            """)
  List<Object[]> findMemberWithAllData(@Param("mid") Long mid);

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

  @Query("SELECT m.pawRate FROM Member m WHERE m.mid = :mid")
  Float findPawRateByMemberId(@Param("mid") Long mid);

  @Query("SELECT m.mid, m.email, m.pawRate FROM Member m")
  List<Object[]> findAllMemberPawRates();

  @Query("""
        SELECT new com.seroter.unknownPaw.dto.MemberResponseDTO$Simple(
            m.mid, m.nickname, m.pawRate, i.path
        )
        FROM Member m
        LEFT JOIN Image i ON i.member = m AND i.imageType = 1
        WHERE m.mid = :mid
        """)
  Optional<MemberResponseDTO.Simple> findSimpleProfileInfo(@Param("mid") Long mid);

  @Query("SELECT m FROM Member m LEFT JOIN FETCH m.likedPetOwner " +
          "LEFT JOIN FETCH m.likedPetSitter " +
          "LEFT JOIN FETCH m.likedCommunity " +
          "WHERE m.mid = :mid")
  Optional<Member> fetchWithLikes(@Param("mid") Long mid);

  // [9-1] 상세 프로필 (펫 포함)
  @Query("SELECT m FROM Member m LEFT JOIN FETCH m.pets WHERE m.mid = :mid")
  Optional<Member> findSimpleProfile(@Param("mid") Long mid);

  @Query("SELECT m FROM Member m LEFT JOIN FETCH m.pets WHERE m.email = :email")
  Optional<Member> findByEmailWithPets(@Param("email") String email);
}

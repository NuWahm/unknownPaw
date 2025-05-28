// ✅ 수정된 ImageRepository (Post 기반으로 통합)
package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByImageType(int imageType);

    @Query("SELECT i FROM Image i WHERE i.member.mid = :mid AND i.imageType = 1")
    Image findMemberProfileImage(@Param("mid") Long mid);

    @Query("SELECT i FROM Image i WHERE i.pet.petId = :petId AND i.imageType = 2")
    List<Image> findPetImages(@Param("petId") Long petId);

    @Query("SELECT i FROM Image i WHERE i.post.postId = :postId AND i.imageType = 3")
    List<Image> findPostImages(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.member.mid = :mid")
    void deleteByMemberId(@Param("mid") Long mid);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.pet.petId = :petId")
    void deleteByPetId(@Param("petId") Long petId);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.post.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.uuid = :uuid")
    void deleteByUuid(@Param("uuid") String uuid);

    @Modifying
    @Query("DELETE FROM Image i WHERE i.path = :path")
    void deleteByPath(@Param("path") String path);

    @Modifying
    @Query("UPDATE Image i SET i.profileImg = :fileName, i.path = :path WHERE i.imgId = :imgId")
    void updateImageInfo(@Param("imgId") Long imgId,
                         @Param("fileName") String fileName,
                         @Param("path") String path);

    @Query("SELECT i FROM Image i WHERE i.post.postId IN :postIds")
    List<Image> findByPostIdIn(@Param("postIds") List<Long> postIds);
}

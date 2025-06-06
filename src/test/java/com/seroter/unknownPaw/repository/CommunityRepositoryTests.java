package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.entity.Community;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.service.CommunityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@SpringBootTest
@Transactional
public class CommunityRepositoryTests {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void insertCommunityPostsWithCommentsAndLikes() {
        // 모든 회원 조회
        List<Member> members = memberRepository.findAll();
        if (members.isEmpty()) {
            throw new IllegalStateException("테스트를 위한 회원이 없습니다. 먼저 PostRepositoryImplTests를 실행해주세요.");
        }

        Random random = new Random();
        CommunityCategory[] categories = CommunityCategory.values();

        // 30개의 커뮤니티 게시글 생성
        for (int i = 0; i < 30; i++) {
            // 랜덤 회원 선택
            Member writer = members.get(random.nextInt(members.size()));
            Member commenter = members.get(random.nextInt(members.size()));
            Member liker = members.get(random.nextInt(members.size()));

            // 랜덤 카테고리 선택
            CommunityCategory category = categories[random.nextInt(categories.length)];

            // 커뮤니티 게시글 생성
            CommunityRequestDTO dto = new CommunityRequestDTO();
            dto.setTitle("테스트 게시글 " + (i + 1));
            dto.setContent("테스트 내용 " + (i + 1));
            dto.setCommunityCategory(category);

            Long communityId = communityService.createCommunityPost(writer.getMid(), dto);

            // 1-5개의 댓글 생성
            int commentCount = random.nextInt(5) + 1;
            for (int j = 0; j < commentCount; j++) {
                communityService.createComment(communityId, commenter.getMid(), "테스트 댓글 " + j);
            }

            // 1-10개의 좋아요 생성
            int likeCount = random.nextInt(10) + 1;
            for (int j = 0; j < likeCount; j++) {
                communityService.likePost(communityId, liker.getMid());
            }
        }
    }
}

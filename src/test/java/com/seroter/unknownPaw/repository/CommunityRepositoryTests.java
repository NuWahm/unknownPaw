//package com.seroter.unknownPaw.repository;
//
//import com.seroter.unknownPaw.dto.CommunityRequestDTO;
//import com.seroter.unknownPaw.entity.Community;
//import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
//import com.seroter.unknownPaw.entity.Member;
//import com.seroter.unknownPaw.service.CommunityService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//import java.util.Random;
//import java.util.stream.IntStream;
//
//@SpringBootTest
//public class CommunityRepositoryTests {
//
//    @Autowired
//    private MemberRepository memberRepository;        // 회원 정보 조회용
//
//    @Autowired
//    private CommunityService communityService;        // 커뮤니티 생성 및 댓글 등록용
//
//    @Autowired
//    private CommunityRepository communityRepository;  // 커뮤니티 엔티티 저장소
//
//    @Test
//    public void insertCommunityPostsWithCommentsAndLikes() {
//         ===== [1. DB에서 전체 멤버 리스트 조회] =====
//        List<Member> members = memberRepository.findAll();
//        Random random = new Random();
//
//        // ===== [2. 더미 게시글 30개 생성] =====
//        IntStream.rangeClosed(1, 30).forEach(i -> {
//            // [2-1] 랜덤한 멤버 선택 (작성자)
//            Member writer = members.get(random.nextInt(members.size()));
//
//            // [2-2] 랜덤한 카테고리 선택
//            CommunityCategory category = CommunityCategory.values()[random.nextInt(CommunityCategory.values().length)];
//
//            // [2-3] 게시글 생성 요청 DTO 생성
//            CommunityRequestDTO dto = CommunityRequestDTO.builder()
//                    .title("테스트 게시글 제목 " + i)
//                    .content("테스트 게시글 내용입니다. 게시글 번호: " + i)
//                    .communityCategory(category)
//                    .build();
//
//            // [2-4] 커뮤니티 게시글 생성 (서비스 계층 이용)
//            Long communityId = communityService.createCommunityPost(writer.getMid(), dto);
//
//            // [2-5] 댓글 2개 생성 (각 게시글당)
//            communityService.createComment(communityId, writer.getMid(), "첫 번째 댓글입니다. 게시글 번호: " + i);
//            communityService.createComment(communityId, writer.getMid(), "두 번째 댓글입니다. 게시글 번호: " + i);
//
//            // [2-6] 게시글 다시 조회 후 좋아요 / 댓글 수 설정
//            communityRepository.findById(communityId).ifPresent(community -> {
//                int randomLikes = random.nextInt(101); // 0~100
//                int commentCount = 2; // 위에서 생성한 댓글 수
//
//                community.setLikes(randomLikes);       // 좋아요 수 설정
//                community.setCommentCount(commentCount);    // 댓글 수 설정
//
//                communityRepository.save(community);   // 변경사항 저장
//            });
//        });
//
//        // ===== [3. 완료 메시지 출력] =====
//        System.out.println("✅ 커뮤니티 게시글 30개, 댓글 포함하여 생성 완료.");
//    }
//}

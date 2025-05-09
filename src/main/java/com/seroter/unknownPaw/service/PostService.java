package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.CursorRequestDTO;
import com.seroter.unknownPaw.dto.CursorResultDTO;
import com.seroter.unknownPaw.dto.PageResultDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.repository.*;
import com.seroter.unknownPaw.repository.search.SearchPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository; // 회원 정보 조회
    private final PetOwnerRepository petOwnerRepository; // 펫오너 게시글 조회 및 관리
    private final PetSitterRepository petSitterRepository; // 펫시터 게시글 조회 및 관리
    private final SearchPostRepository searchPostRepository; // 동적 게시글 검색 기능
    private PostRepository<Post> postRepository;  //


    // 게시글 등록 메서드
    public Long register(String type, PostDTO dto, Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")); // 회원이 없으면 예외 발생

        // DTO를 엔티티로 변환
        Post entity = dtoToEntity(dto, type);
        entity.setMember(member); // 게시글에 멤버 연결

        // 역할에 맞게 게시글 저장 후 ID 반환
        return savePostBytype(type, entity);
    }

    // 게시글 조회 메서드
    public PostDTO get(String type, Long postId) {
        // 역할에 맞는 게시글 조회
        return findPostBytype(type, postId)
                .map(entity -> entityToDto(entity, isSitter(type))) // 게시글을 DTO로 변환
                .orElseThrow(() -> new EntityNotFoundException(type + " 게시글을 찾을 수 없습니다.")); // 없으면 예외 발생
    }

    // 게시글 수정 메서드
    public void modify(String type, PostDTO dto) {
        // 게시글 조회
        Post entity = findPostBytype(type, dto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(type + " 게시글을 찾을 수 없습니다.")); // 없으면 예외 발생

        // 게시글 수정
        updateCommonFields(entity, dto);
        savePostBytype(type, entity); // 수정된 게시글 저장
    }

    // 게시글 삭제 메서드
    public void remove(String type, Long postId) {
        // 역할에 따라 게시글 삭제
        if ("petOwner".equals(type)) {
            petOwnerRepository.deleteById(postId); // 펫오너 게시글 삭제
        } else if ("petSitter".equals(type)) {
            petSitterRepository.deleteById(postId); // 펫시터 게시글 삭제
        } else {
            throw new IllegalArgumentException("잘못된 역할입니다."); // 잘못된 역할 처리
        }
    }

    // 게시글 동적 검색 메서드
    public Page<? extends Post> searchPosts(String type, String keyword, String location, String category, Pageable pageable) {
        return searchPostRepository.searchDynamic(type, keyword, location, category, pageable);
    }


    // 특정 멤버의 게시글 조회 메서드
    public List<PostDTO> getPostsByMember(String type, Long memberId) {
        if ("petOwner".equals(type)) {
            // 펫오너 게시글 조회
            return petOwnerRepository.findByMember_Mid(memberId)
                    .stream()
                    .map(post -> entityToDto(post, false)) // DTO로 변환
                    .toList();
        } else if ("petSitter".equals(type)) {
            // 펫시터 게시글 조회
            return petSitterRepository.findByMember_Mid(memberId)
                    .stream()
                    .map(post -> entityToDto(post, true)) // DTO로 변환
                    .toList();
        } else {
            throw new IllegalArgumentException("잘못된 역할입니다."); // 잘못된 역할 처리
        }
    }

    // 특정 위치에 맞는 펫시터 게시글 조회 메서드
    public List<PostDTO> findSittersByLocation(String location) {
        // 지정된 위치에 맞는 펫시터 게시글 조회
        return petSitterRepository.findByDefaultLocation(location)
                .stream()
                .map(post -> entityToDto(post, true)) // DTO로 변환
                .toList();
    }

    // DTO를 엔티티로 변환하는 메서드
    private Post dtoToEntity(PostDTO dto, String type) {
        // 역할에 맞는 엔티티 생성
        return "petOwner".equals(type) ? createPetOwnerEntity(dto) : createPetSitterEntity(dto);
    }

    // 펫오너 게시글 엔티티 생성
    private Post createPetOwnerEntity(PostDTO dto) {
        return PetOwner.builder()
                .title(dto.getTitle()) // 제목
                .content(dto.getContent()) // 내용
                .serviceCategory(ServiceCategory.valueOf(dto.getServiceCategory())) // 서비스 카테고리
                .desiredHourlyRate(dto.getHourlyRate()) // 원하는 시간당 요금
                .likes(dto.getLikes()) // 좋아요 수
                .chatCount(dto.getChatCount()) // 채팅 수
                .defaultLocation(dto.getDefaultLocation()) // 기본 위치
                .flexibleLocation(dto.getFlexibleLocation()) // 유연한 위치
                .build();
    }

    // 펫시터 게시글 엔티티 생성
    private Post createPetSitterEntity(PostDTO dto) {
        return PetSitter.builder()
                .title(dto.getTitle()) // 제목
                .content(dto.getContent()) // 내용
                .serviceCategory(ServiceCategory.valueOf(dto.getServiceCategory())) // 서비스 카테고리
                .desiredHourlyRate(dto.getHourlyRate()) // 원하는 시간당 요금
                .likes(dto.getLikes()) // 좋아요 수
                .chatCount(dto.getChatCount()) // 채팅 수
                .defaultLocation(dto.getDefaultLocation()) // 기본 위치
                .flexibleLocation(dto.getFlexibleLocation()) // 유연한 위치
                .build();
    }

    // 엔티티를 DTO로 변환하는 메서드
    private PostDTO entityToDto(Post entity, boolean isSitter) {
        return PostDTO.builder()
                .postId(entity.getPostId()) // 게시글 ID
                .title(entity.getTitle()) // 제목
                .content(entity.getContent()) // 내용
                .serviceCategory(entity.getServiceCategory().name()) // 서비스 카테고리
                .hourlyRate(entity.getDesiredHourlyRate()) // 원하는 시간당 요금
                .likes(entity.getLikes()) // 좋아요 수
                .chatCount(entity.getChatCount()) // 채팅 수
                .defaultLocation(entity.getDefaultLocation()) // 기본 위치
                .flexibleLocation(entity.getFlexibleLocation()) // 유연한 위치
                .regDate(entity.getRegDate()) // 등록일
                .modDate(entity.getModDate()) // 수정일
                .email(entity.getMember() != null ? entity.getMember().getEmail() : null) // 회원 이메일
                .isPetSitterPost(isSitter) // 펫시터 게시글 여부
                .build();
    }

    // 게시글의 공통 필드를 업데이트하는 메서드
    private void updateCommonFields(Post entity, PostDTO dto) {
        entity.setTitle(dto.getTitle()); // 제목
        entity.setContent(dto.getContent()); // 내용
        entity.setServiceCategory(ServiceCategory.valueOf(dto.getServiceCategory())); // 서비스 카테고리
        entity.setDesiredHourlyRate(dto.getHourlyRate()); // 원하는 시간당 요금
        entity.setDefaultLocation(dto.getDefaultLocation()); // 기본 위치
        entity.setFlexibleLocation(dto.getFlexibleLocation()); // 유연한 위치
    }

    // 역할에 맞는 게시글을 조회하는 메서드
    private Optional<Post> findPostBytype(String type, Long postId) {
        if ("petOwner".equals(type)) {
            // 펫오너 게시글 조회
            return petOwnerRepository.findById(postId).map(post -> (Post) post);
        } else if ("petSitter".equals(type)) {
            // 펫시터 게시글 조회
            return petSitterRepository.findById(postId).map(post -> (Post) post);
        } else {
            throw new IllegalArgumentException("잘못된 역할입니다."); // 잘못된 역할 처리
        }
    }

    // 역할에 맞게 게시글을 저장하는 메서드
    private Long savePostBytype(String type, Post entity) {
        if ("petOwner".equals(type)) {
            // 펫오너 게시글 저장
            return petOwnerRepository.save((PetOwner) entity).getPostId();
        } else if ("petSitter".equals(type)) {
            // 펫시터 게시글 저장
            return petSitterRepository.save((PetSitter) entity).getPostId();
        } else {
            throw new IllegalArgumentException("잘못된 역할입니다."); // 잘못된 역할 처리
        }
    }

    // 펫시터 여부를 확인하는 메서드
    private boolean isSitter(String type) {
        return "petSitter".equals(type); // 역할이 펫시터이면 true 반환
    }




    public PostDTO getRandomPostByType(String postType) {
        Post post = switch (postType.toLowerCase()) {
            case "petowner" -> petOwnerRepository.findRandomPetOwnerPosts(PostType.PETOWNER, PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);
            case "petsitter" -> petSitterRepository.findRandomPetSitterPosts(PostType.PETSITTER, PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);
//            case "community" -> postRepository.findRandomByType(PostRole.COMMUNITY, PageRequest.of(0, 1))
//                    .stream().findFirst().orElse(null);
            default -> null;
        };

        if (post == null) {
            return PostDTO.builder()
                .postId(-1L)
                .title("게시글이 없습니다")
                .content("작성된 게시글이 없습니다.")
                .build();
        }

        return PostDTO.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .content(post.getContent())
            .serviceCategory(post.getServiceCategory().name())
            .hourlyRate(post.getDesiredHourlyRate())
            .likes(post.getLikes())
            .chatCount(post.getChatCount())
            .defaultLocation(post.getDefaultLocation())
            .flexibleLocation(post.getFlexibleLocation())
            .regDate(post.getRegDate())
            .modDate(post.getModDate())
            .email(post.getMember() != null ? post.getMember().getEmail() : null)
            .isPetSitterPost(postType.equalsIgnoreCase("petsitter"))
            .build();
    }

    public List<PostDTO> getRandom6PetOwnerPosts() {
        return petOwnerRepository.findRandom6Posts()
                .stream()
                .map(post -> entityToDto(post, false))  // 여기서 DTO 변환
                .toList();
    }


    public List<PostDTO> getRandom6PetSitterPosts() {
        return petSitterRepository.findRandom6Posts()
                .stream()
                .map(post -> entityToDto(post, true))  // 여기서 DTO 변환
                .toList();
    }







    // 🖱️ 무한 스크롤
//    public CursorResultDTO<PostDTO> getPostList(CursorRequestDTO request) {
//        List<Post> posts = postRepository.findNextPosts(request.getLastPostId(), request.getSize());
//        return new CursorResultDTO<>(posts, request.getSize(), PostDTO::fromEntity);
//    }

}
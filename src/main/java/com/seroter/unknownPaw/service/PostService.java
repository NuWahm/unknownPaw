package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.*;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetOwnerRepository;
import com.seroter.unknownPaw.repository.PetSitterRepository;
import com.seroter.unknownPaw.repository.search.SearchPostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository; // 회원 정보 조회
    private final PetOwnerRepository petOwnerRepository; // 펫오너 게시글 조회 및 관리
    private final PetSitterRepository petSitterRepository; // 펫시터 게시글 조회 및 관리
    private final SearchPostRepository searchPostRepository; // 동적 게시글 검색 기능

    // 게시글 등록 메서드
    public Long register(String postType, PostDTO dto, Long memberId) {
        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다.")); // 회원이 없으면 예외 발생

        // DTO를 엔티티로 변환
        Post entity = dtoToEntity(dto, PostType.valueOf(postType));
        entity.setMember(member); // 게시글에 멤버 연결

        // 사용자가 게시글을 등록할 수 있도록, 역할 검증 로직을 수정
        if (member.getRole() == Member.Role.USER) {
            // 정상적으로 게시글을 등록
            return savePostbyPostType(postType, entity);
        } else {
            throw new IllegalArgumentException("잘못된 역할입니다. 사용자만 게시글을 작성할 수 있습니다.");
        }
    }

    // 게시글 조회 메서드
    public PostDTO get(String postType, Long postId) {
        // 역할에 맞는 게시글 조회
        return findPostbyPostType(postType, postId)
                .map(entity -> entityToDto(entity, isSitter(postType))) // 게시글을 DTO로 변환
                .orElseThrow(() -> new EntityNotFoundException(postType + " 게시글을 찾을 수 없습니다.")); // 없으면 예외 발생
    }

    // 게시글 수정 메서드
    public void modify(String postType, PostDTO dto) {
        // 게시글 조회
        Post entity = findPostbyPostType(postType, dto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(postType + " 게시글을 찾을 수 없습니다.")); // 없으면 예외 발생

        // 게시글 수정
        updateCommonFields(entity, dto);
        savePostbyPostType(postType, entity); // 수정된 게시글 저장
    }

    // 게시글 삭제 메서드
    public void remove(String postType, Long postId) {
        // 게시글 삭제 시 역할 검증을 수정
        if (PostType.PET_OWNER.name().equals(postType)) {
            petOwnerRepository.deleteById(postId); // 펫오너 게시글 삭제
        } else if (PostType.PET_SITTER.name().equals(postType)) {
            petSitterRepository.deleteById(postId); // 펫시터 게시글 삭제
        } else {
            throw new IllegalArgumentException("잘못된 게시글 타입입니다."); // 잘못된 타입 처리
        }
    }

    // 게시글 동적 검색 메서드
    @Transactional
    public Page<? extends Post> searchPosts(String postType, String keyword, String location, String category, Pageable pageable) {
        log.info("Searching posts with type: {}", postType);
        // Repository에서 LEFT JOIN FETCH로 멤버 정보까지 가져옴
        Page<? extends Post> result = searchPostRepository.searchDynamic(postType, keyword, location, category, pageable);

        // Controller의 list 메서드는 이 결과를 받아서 result.map(PostDTO::fromEntity) 호출
        // 그러므로 PostDTO.fromEntity가 제대로 수정되어야 함
        log.info("Finished searching posts. Found {} elements.", result.getTotalElements());
        return result;
    }


    // 특정 멤버의 게시글 조회 메서드
    public List<PostDTO> getPostsByMember(String postType, Long memberId) {
        if ("petOwner".equals(postType)) {
            // 펫오너 게시글 조회
            return petOwnerRepository.findByMember_Mid(memberId)
                    .stream()
                    .map(post -> entityToDto(post, false)) // DTO로 변환
                    .toList();
        } else if ("petSitter".equals(postType)) {
            // 펫시터 게시글 조회
            return petSitterRepository.findByMember_Mid(memberId)
                    .stream()
                    .map(post -> entityToDto(post, true)) // DTO로 변환
                    .toList();
        } else {
            throw new IllegalArgumentException(
                    "4잘못된 역할입니다."); // 잘못된 역할 처리
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

    // PostService.java 안 dtoToEntity 메서드 시그니처
    private Post dtoToEntity(PostDTO dto, PostType postType) {

        try { ServiceCategory.valueOf(dto.getServiceCategory()); }
        catch (Exception e) {
            throw new IllegalArgumentException("잘못된 서비스 카테고리");
        }
        // postType Enum으로 받음
        if (postType == PostType.PET_SITTER) {
            return PetSitter.builder()
                    .postId(dto.getPostId())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .serviceCategory(ServiceCategory.valueOf(dto.getServiceCategory()))
                    .desiredHourlyRate(dto.getHourlyRate())
                    .likes(dto.getLikes())
                    .chatCount(dto.getChatCount())
                    .defaultLocation(dto.getDefaultLocation())
                    .flexibleLocation(dto.getFlexibleLocation())
                    .member(null) // 멤버는 register에서 세팅함
                    .postType(PostType.PET_SITTER)
                    .build();
        } else {
            return PetOwner.builder()
                    .postId(dto.getPostId())
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .serviceCategory(ServiceCategory.valueOf(dto.getServiceCategory()))
                    .hourlyRate(dto.getHourlyRate())
                    .likes(dto.getLikes())
                    .chatCount(dto.getChatCount())
                    .defaultLocation(dto.getDefaultLocation())
                    .flexibleLocation(dto.getFlexibleLocation())
                    .member(null)
                    .postType(PostType.PET_OWNER)
                    .build();
        }
    }

    // 펫오너 게시글 엔티티 생성
    private Post createPetOwnerEntity(PostDTO dto) {
        return PetOwner.builder()
                .title(dto.getTitle()) // 제목
                .content(dto.getContent()) // 내용
                .serviceCategory(ServiceCategory.valueOf(dto.getServiceCategory())) // 서비스 카테고리
                .hourlyRate(dto.getHourlyRate()) // 원하는 시간당 요금
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
        PostDTO.PostDTOBuilder builder = PostDTO.builder()
                .postId(entity.getPostId()) // 게시글 ID
                .title(entity.getTitle()) // 제목
                .content(entity.getContent()) // 내용
                .serviceCategory(entity.getServiceCategory().name()) // 서비스 카테고리
                .hourlyRate(entity.getHourlyRate()) // 원하는 시간당 요금
                .likes(entity.getLikes()) // 좋아요 수
                .chatCount(entity.getChatCount()) // 채팅 수
                .defaultLocation(entity.getDefaultLocation()) // 기본 위치
                .flexibleLocation(entity.getFlexibleLocation()) // 유연한 위치
                .regDate(entity.getRegDate()) // 등록일
                .modDate(entity.getModDate()) // 수정일
                // email 필드는 필요하면 남겨두거나 제거
                // .email(entity.getMember() != null ? entity.getMember().getEmail() : null)
                // 이미지는 필요에 따라 여기서도 매핑 로직 추가 (현재는 fromEntity에만 있음)
                .isPetSitterPost(isSitter); // 펫시터 게시글 여부

        // ** 수정: 로딩된 멤버 정보가 있다면 MemberResponseDTO 객체를 생성하여 빌더에 설정 **
        if (entity.getMember() != null) {
            Member memberEntity = entity.getMember(); // 로딩된 Member 엔티티 가져오기
            log.debug("Mapping member for post ID {}", entity.getPostId());

            // 이미 존재하는 MemberResponseDTO 클래스의 builder 또는 fromEntity static 메서드 사용
            // MemberResponseDTO에 fromEntity static 메서드를 만들었다면:
            // MemberResponseDTO memberDTO = MemberResponseDTO.fromEntity(memberEntity);

            // MemberResponseDTO에 fromEntity static 메서드가 없다면 builder 사용:
            MemberResponseDTO memberDTO = MemberResponseDTO.builder()
                    .mid(memberEntity.getMid())
                    .email(memberEntity.getEmail()) // Member 엔티티에서 이메일 가져오기
                    .nickname(memberEntity.getNickname()) // Member 엔티티에서 닉네임 가져오기
                    // Member 엔티티에 profileImagePath 필드가 있다고 가정하고 가져오기
                    .profileImagePath(memberEntity.getProfileImagePath())
                    .pawRate(memberEntity.getPawRate())
                    .build(); // MemberResponseDTO 객체 생성 완료

            // ** PostDTO 빌더의 member 필드에 생성한 memberDTO 객체를 설정 **
            builder.member(memberDTO);
            log.debug("Mapped and set Member DTO for post ID: {}", entity.getPostId());
        } else {
            log.warn("Post entity with ID {} has a null member during entityToDto mapping.", entity.getPostId());
        }

        // 빌더를 사용하여 최종 PostDTO 객체 생성 및 반환
        PostDTO builtDto = builder.build();
        log.debug("Finished building PostDTO for post ID: {}", builtDto.getPostId());
        return builtDto; // 최종적으로 member 필드가 설정된 PostDTO 반환
    }

    // 게시글의 공통 필드를 업데이트하는 메서드
    private void updateCommonFields(Post entity, PostDTO dto) {
        entity.setTitle(dto.getTitle()); // 제목
        entity.setContent(dto.getContent()); // 내용
        entity.setServiceCategory(ServiceCategory.valueOf(dto.getServiceCategory())); // 서비스 카테고리
        entity.setHourlyRate(dto.getHourlyRate()); // 원하는 시간당 요금
        entity.setDefaultLocation(dto.getDefaultLocation()); // 기본 위치
        entity.setFlexibleLocation(dto.getFlexibleLocation()); // 유연한 위치
    }

    // 역할에 맞는 게시글을 조회하는 메서드
    private Optional<Post> findPostbyPostType(String postType, Long postId) {
        log.debug("Finding post by type {} and ID {}", postType, postId);
        if (PostType.PET_OWNER.name().equals(postType)) {
            return petOwnerRepository.findById(postId).map(post -> (Post) post);
        } else if (PostType.PET_SITTER.name().equals(postType)) {
            return petSitterRepository.findById(postId).map(post -> (Post) post);
        } else {
            throw new IllegalArgumentException("알 수 없는 게시글 타입 문자열입니다." + postType);
        }
    }

    // 역할에 맞게 게시글을 저장하는 메서드
    private Long savePostbyPostType(String postType, Post entity) {
        // 게시글의 postType에 맞게 저장
        if (PostType.PET_OWNER.name().equals(postType)) {
            return petOwnerRepository.save((PetOwner) entity).getPostId();
        } else if (PostType.PET_SITTER.name().equals(postType)) {
            return petSitterRepository.save((PetSitter) entity).getPostId();
        } else {
            // 올바른 postType이 아닌 경우 처리
            throw new IllegalArgumentException("잘못된 게시글 타입입니다.");
        }
    }

    // 펫시터 여부를 확인하는 메서드
    private boolean isSitter(String postType) {
        return PostType.PET_SITTER.name().equals(postType);
    }


    // 🖱️ 무한 스크롤
//    public CursorResultDTO<PostDTO> getPostList(CursorRequestDTO request) {
//        List<Post> posts = postRepository.findNextPosts(request.getLastPostId(), request.getSize());
//        return new CursorResultDTO<>(posts, request.getSize(), PostDTO::fromEntity);
//    }

}
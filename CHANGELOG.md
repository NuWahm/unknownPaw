# 📓 CHANGELOG

## [2025-04-11] Member 엔티티 구조 개선
💡 Lombok @ToString 관련 경고 제거

1. Post, PetOwner, PetSitter Entity에서 @ToString(exclude = "member") -> 제거. 
  -ToString을 적용시 member는 제외시키는 어노테이션.
   Post에서 petOwner와 petSitter를 상속해서 member를 가져와야하는데, 
   해당 구문은 member의 ToString화를 제외하기 때문에
   Lombok이 Owner와 Sitter에서 member값을 찾지 못함.

   
3. DateAppoint Entity에서 @ToString(exclude = {"petOwner", "petSitter"}) -> 제거.
   -위와 같은 이유로 제거

💡 Lombok @AllArgsConstructor와 @Builder 충돌 제거
두 어노테이션 모두 생성자 기능인데, 
@Builder와 @AllArgsConstructor를 동시에 사용하면 생성자 중복으로 컴파일 에러 발생.
@Builder가 내부적으로 생성자를 자동 생성하므로, 
수동 생성자 @AllArgsConstructor를 제거

@Builder 사용시, 아래와 같이 원하는 항목에만 생성자 적용 가능
PetOwner petOwner = PetOwner.builder()
                           .title("제목")
                           .content("내용")
                           .member(member)
                           .build();

@AllArgsConstructor 사용시 아래와 같이 매개변수 안에 
모든 값에 대해 적용해줘야 에러 방지
PetOwner petOwner = new PetOwner("제목", "내용", member);
-> 매개변수의 타입, 개수, 순서가 다 일치해야만 작동


==================================================================================================================================================================


## [2025-04-04] Member 엔티티 구조 개선

💡 Members → Member 엔티티명 변경 및 구조 개선

- 기존 `Members` 엔티티를 `Member`로 변경했습니다. (단수형이 객체를 의미하므로 표준에 맞게 수정)
- 엔티티 필드명 오타(`adress → address`, `phonenumber → phoneNumber`) 수정
- `Member` 엔티티에 role, status, fromSocial, profileImagePath 등 주요 필드 추가
- 엔티티에 상세한 주석 추가 (각 필드의 역할 명시)
- RequestDto / ResponseDto 로 구분하여 역할을 명확히 함
  - Request: 회원가입 등 사용자 입력용
  - Response: 회원 조회 응답용 (보안/유지보수 목적)

📌 바꾼 이유

- DTO 분리가 되어 있지 않아 보안 위험 및 가독성 문제 있음
- Entity 명이 복수형이어서 자바 컨벤션에 맞지 않음
- 필드명이 camelCase 컨벤션에서 벗어나 있음

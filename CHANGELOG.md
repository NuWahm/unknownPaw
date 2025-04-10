# 📓 CHANGELOG

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

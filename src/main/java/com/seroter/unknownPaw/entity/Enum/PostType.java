package com.seroter.unknownPaw.entity.Enum;

public enum PostType {
    PET_OWNER,
    PET_SITTER;

    // 문자열을 PostType으로 변환하는 정적 헬퍼 메서드
    public static PostType from(String roleString) {
        if (roleString == null) {
            throw new IllegalArgumentException("Role string cannot be null");
        }
        switch (roleString.toLowerCase()) { // 입력 문자열을 소문자로 변환하여 비교
            case "petowner": // petowner petOwner PetOwner pPETOWnER
                return PET_OWNER;
            case "petsitter":
                return PET_SITTER;
            default:
                // 일치하는 문자열이 없을 경우 예외 발생 또는 null 반환 등 처리
                throw new IllegalArgumentException("Unknown PostType string: " + roleString);
        }
    }

    // 필요하다면 Enum 값을 문자열로 변환하는 메서드 추가
    public String getValue() {
        return this.name().toLowerCase().replace("_", ""); // 예: PET_OWNER -> petowner
    }

}
package com.seroter.unknownPaw.entity.Enum;

public enum PostType {
    PET_OWNER,
    PET_SITTER;

    /**
     * 문자열을 PostType으로 변환하는 정적 헬퍼 메서드
     * - 소문자, 공백, 특수문자 제거
     * - 디버깅 로그 포함
     */
    public static PostType from(String roleString) {
        if (roleString == null) {
            System.out.println("❌ PostType.from(): null 값");
            throw new IllegalArgumentException("Role string cannot be null");
        }

        switch (roleString.toLowerCase()) { // 입력 문자열을 소문자로 변환하여 비교
            case "petowner": // petowner petOwner PetOwner pPETOWnER

                return PET_OWNER;
            case "petsitter":
                System.out.println("✅ 매칭 성공 → PETSITTER");
                return PET_SITTER;
            default:
                System.out.println("❌ PostType 매핑 실패: '" + cleaned + "'");
                throw new IllegalArgumentException("Unknown PostType string: " + cleaned);
        }
    }

    /**
     * Enum 값을 문자열로 변환하는 메서드
     * 예: PET_OWNER → "petowner"
     */
    public String getValue() {
        return this.name().toLowerCase().replace("_", "");
    }

}


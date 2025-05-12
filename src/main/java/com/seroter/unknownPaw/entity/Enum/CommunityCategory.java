package com.seroter.unknownPaw.entity.Enum;

public enum CommunityCategory {
    GENERAL,       // 일반 게시글
    EVENT,         // 이벤트 게시글
    ANNOUNCEMENT,  // 공지 게시글
    COMMUNITY;     // 커뮤니티 관련 게시글

    // 문자열을 Enum으로 변환하는 메서드
    public static CommunityCategory fromString(String category) {
        try {
            // 대소문자 구분 없이 Enum으로 변환
            return CommunityCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 잘못된 값이 들어올 경우 기본값을 설정
            return CommunityCategory.GENERAL; // 기본값 설정
        }
    }
}

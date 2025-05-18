package com.seroter.unknownPaw.entity.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PostType {
    PET_OWNER,
    PET_SITTER;

    /** JSON → Enum 역직렬화 시 이 메서드를 호출하도록 Jackson 에게 알려줌 */
    @JsonCreator
    public static PostType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("PostType string cannot be null");
        }
        switch (value.toLowerCase()) {
            case "petowner":
            case "pet_owner":
                return PET_OWNER;
            case "petsitter":
            case "pet_sitter":
                return PET_SITTER;
            default:
                throw new IllegalArgumentException("Unknown PostType string: " + value);
        }
    }

    /** Enum → JSON 직렬화 시, 이 값을 쓰도록 Jackson 에게 알려줌 */
    @JsonValue
    public String toValue() {
        return this.name().toLowerCase().replace("_", "");
    }
}
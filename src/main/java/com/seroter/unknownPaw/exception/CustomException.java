package com.seroter.unknownPaw.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException의 메시지로 ErrorCode의 메시지를 사용
        this.errorCode = errorCode;
    }

    // 필요하다면 추가적인 생성자나 메서드를 정의할 수 있습니다.
    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

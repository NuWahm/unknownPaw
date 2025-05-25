package com.seroter.unknownPaw.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * CustomException 발생 시 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        // CustomException으로부터 ErrorCode를 가져와서 응답을 생성
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode, e.getMessage()); // e.getMessage()는 CustomException의 생성자에서 설정된 메시지
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /**
     * 그 외 모든 예상치 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        // 예상치 못한 예외는 INTERNAL_SERVER_ERROR로 처리
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        // 실제 운영 환경에서는 에러 메시지를 클라이언트에게 너무 자세히 노출하지 않는 것이 좋습니다.
        ErrorResponse response = ErrorResponse.of(errorCode, errorCode.getMessage() + ": " + e.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    // 에러 응답 형식 DTO (클라이언트에게 반환될 형식)
    @Getter
    @NoArgsConstructor // Lombok을 사용하면 @AllArgsConstructor 대신 @NoArgsConstructor와 @Builder를 함께 쓸 수 있습니다.
    public static class ErrorResponse {
        private String code;
        private String message;
        private int status; // HTTP 상태 코드

        // of 메서드를 통해 ErrorCode와 메시지를 받아 ErrorResponse 객체 생성
        public static ErrorResponse of(ErrorCode errorCode, String message) {
            ErrorResponse response = new ErrorResponse();
            response.code = errorCode.name(); // Enum 이름 (예: MEMBER_NOT_FOUND)
            response.message = message;
            response.status = errorCode.getHttpStatus().value(); // HTTP 상태 코드 값 (예: 404)
            return response;
        }

        public static ErrorResponse of(ErrorCode errorCode) {
            return of(errorCode, errorCode.getMessage());
        }
    }

}
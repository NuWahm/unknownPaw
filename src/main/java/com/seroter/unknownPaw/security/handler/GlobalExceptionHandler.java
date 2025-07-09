//package com.seroter.unknownPaw.security.handler;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//
//@RestControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(Exception e) {
//        log.error("Unexpected error occurred", e);
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ErrorResponse("서버 오류가 발생했습니다."));
//    }
//
//    @ExceptionHandler(WebClientResponseException.class)
//    public ResponseEntity<ErrorResponse> handleWebClientException(WebClientResponseException e) {
//        log.error("Naver Maps API error", e);
//        return ResponseEntity
//                .status(e.getStatusCode())
//                .body(new ErrorResponse("지도 서비스 오류가 발생했습니다."));
//    }
//}
//
//@Getter
//@AllArgsConstructor
//class ErrorResponse {
//    private String message;
//}
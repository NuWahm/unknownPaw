package com.seroter.unknownPaw.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // Common Errors
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "Invalid Input Value"),
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed"),
  HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access Denied"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"),

  // Member Specific Errors
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
  PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요."),
  EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
  NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),


  // Add more specific error codes as needed
   EXAMPLE_ERROR(HttpStatus.BAD_REQUEST, "This is an example error message.");

  private final HttpStatus httpStatus;
  private final String message;

  }

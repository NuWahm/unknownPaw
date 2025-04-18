//package com.seroter.unknownPaw.dto;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//
///**
// * 커서 기반 클라이언트가 요청할 때 사용하는 값을 담는 DTO
// * 프론트가 “이전 요청에서 마지막으로 받은 글 ID가 82였어. 그다음 10개만 더 줘”라고 말하려면
// * → 백엔드에 lastId=82, size=10 같이 알려줘야 함.
// * ✅ 기능 요약
// * 스크롤을 내릴 때 마지막이 뭐였는지, 얼마나 더 필요한지 알려주는 역할
// */
//@Data
//@Builder
//@AllArgsConstructor
//public class CursorRequestDTO {
//  private Long lastPostId; // 커서 기준이 되는 마지막 데이터 ID
//  private int size; // 몇 개 가져올지 (기본값: 10 정도로 생각)
//
//  public CursorRequestDTO() {
//    this.size = 10;
//  }
//
//  /**
//   * 커서가 존재하는지 여부
//   * @return true if lastId exists
//   */
//  public boolean hasCursor() {
//    return lastPostId != null;
//  }
//}
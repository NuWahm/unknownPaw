package com.seroter.unknownPaw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CursorResultDTO<T extends Identifiable> {
  // <T>인 이유 어떤 타입의 DTO든 받기 위해 유연하게 만듦. 나중에 확장성 고려
  private final List<T> dtoList;   // 화면에 전달할 DTO 리스트
  private final boolean hasNext;   // 다음 페이지 여부
  private final Long lastId;       // 다음 요청에 사용할 커서 값


  // 엔티티 → DTO 변환을 포함한 커서 응답 생성자
  public <EN> CursorResultDTO(List<EN> entities, int size, Function<EN, T> fn) {
    // JPA 엔티티 리스트를 프론트용 DTO로 바꾸는 부분
    this.dtoList = entities.stream().map(fn).collect(Collectors.toList());
    // List를 stream으로 변경. 각각의 Post를 PostDTO로 변환. 다시 List로 모음
    this.hasNext = entities.size() == size;
    // entities 요청에 대해 DB에서 가져온 결과 리스트. 10개  size: 클라이언트가 요청한 가져올 개수.요청해서 10개 받았는지 확인.
    // true 더 있을 가능성 있음.
    //
    // false 더 없을 듯
    this.lastId = hasNext ? extractLastId(entities) : null;
    // hasNext가 true일 때만 커서를 만든다.
    // extractLastId 엔티티 마지막 요소의 id를 꺼내 "다음 페이지의 시작점"으로 사용
  }

  /**
   * 마지막 요소의 ID 추출
   */
  @SuppressWarnings("unchecked") // 미확인 오퍼레이션 경고 억제
  private <EN> Long extractLastId(List<EN> entities) {
    // 스크롤을 할 때 받은 원본 엔티티 리스트
    Object lastEntity = entities.get(entities.size() - 1);
    // 리스트에서 마지막 요소를 꺼냄. 기준이 필요함
    if (lastEntity instanceof Identifiable) {
      return ((Identifiable) lastEntity).getId();
    }
    // lastEntity가 Identifiable을 구현했는지 확인. 맞으면 getId()로 ID 뽑음
    throw new IllegalArgumentException("Identifiable 인터페이스를 구현하지 않은 엔티티입니다.");
  }
  // Idntifiable을 구현하지 않았다면 에러 던짐. 실수 getId없는 타입이 들어온 걸 막기 위함.
}

/* 전체 흐름
    클라이언트가 lastId = null, size = 10으로 요청 보냄
    DB에서 게시글 10개 조회함 → entities.size() == 10
    → hasNext = true
    → 마지막 게시글의 ID 뽑아서 lastId = 82 저장
    → 프론트에 hasNext=true, lastId=82 응답함

    hasNext → 다음 페이지가 있을 가능성 판단
    lastId → 다음 페이지 요청할 때 기준이 되는 커서 = 마지막으로 본 데이터의 고유 ID
 */
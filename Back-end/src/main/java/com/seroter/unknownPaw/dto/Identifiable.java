package com.seroter.unknownPaw.dto;

// 🖱️ 다양한 목록도 무한 스크롤  하고 싶을 때
// 채팅이나 후기 등등의 상황을 고려해서 확장성을 고려
public interface Identifiable {
  Long getId();
}

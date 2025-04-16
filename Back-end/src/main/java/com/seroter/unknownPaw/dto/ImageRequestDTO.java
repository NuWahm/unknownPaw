package com.seroter.unknownPaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequestDTO {

  private String role;             // member, pet, petOwner, petSitter
  private String targetType;       // member, pet, petOwner, petSitter 각 엔티티 연결할때 사용
  private Long targetId;           // targetType 으로 지정한 엔티티의 PK(ID) 를 뜻함
  private String oldFileName;      // 교체 시 사용
  private MultipartFile file;      // 업로드/교체할 파일
}

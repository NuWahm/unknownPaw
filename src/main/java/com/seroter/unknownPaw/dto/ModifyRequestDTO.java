package com.seroter.unknownPaw.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ModifyRequestDTO {
  private PostDTO postDTO;
    private MultipartFile image;
    private Long postId;
}

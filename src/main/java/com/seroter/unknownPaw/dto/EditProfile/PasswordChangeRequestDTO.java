package com.seroter.unknownPaw.dto.EditProfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequestDTO {
    private String currentPassword; // 현재 비밀번호
    private String newPassword;     // 새로운 비밀번호

}
package com.timesheetspro_api.common.dto.user;

import lombok.Data;

@Data
public class ResetPasswordDto {
    String password;
    String currentPassword;
    String userId;
    String companyId;
    String token;
}

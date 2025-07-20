package com.timesheetspro_api.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDto {
    @NotEmpty(message = "Username is required")
    private String userName;
    @NotNull(message = "Company id is required")
    private String companyId;
    @NotEmpty(message = "Password is required")
    private String password;
    private Boolean noPassword = false;
}

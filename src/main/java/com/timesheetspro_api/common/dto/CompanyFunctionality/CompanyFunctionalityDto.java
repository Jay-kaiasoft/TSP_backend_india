package com.timesheetspro_api.common.dto.CompanyFunctionality;

import com.timesheetspro_api.common.dto.roles.RolesActionsDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CompanyFunctionalityDto {
    private Long functionalityId;
    @NotNull(message = "Functionality name is required")
    private String functionalityName;
    private List<RolesActionsDto> modules;
}

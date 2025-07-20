package com.timesheetspro_api.common.dto.functionality;

import com.timesheetspro_api.common.dto.roles.RolesActionsDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FunctionalityDto {
    private Long functionalityId;
    @NotNull(message = "Functionality name is required")
    private String functionalityName;
    private List<RolesActionsDto> modules;
}

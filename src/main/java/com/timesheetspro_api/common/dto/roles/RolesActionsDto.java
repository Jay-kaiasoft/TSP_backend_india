package com.timesheetspro_api.common.dto.roles;

import lombok.Data;

import java.util.List;

@Data
public class RolesActionsDto {
    private List<RoleFunctionalityDto> functionalities;
}

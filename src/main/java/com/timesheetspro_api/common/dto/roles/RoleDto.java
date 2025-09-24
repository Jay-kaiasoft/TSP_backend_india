package com.timesheetspro_api.common.dto.roles;

import lombok.Data;

@Data
public class RoleDto {
    private Long roleId;
    private String roleName;
    private RolesActionsDto rolesActions;
}

package com.timesheetspro_api.common.dto.CompanyEmployeeRoles;
import lombok.Data;

@Data
public class CompanyEmployeeRolesDto {
    private int roleId;
    private int companyId;
    private String roleName;
    private CompanyEmployeeRolesActionsDto rolesActions;
}

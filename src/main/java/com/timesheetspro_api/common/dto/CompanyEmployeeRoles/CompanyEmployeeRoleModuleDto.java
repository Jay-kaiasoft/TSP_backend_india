package com.timesheetspro_api.common.dto.CompanyEmployeeRoles;

import lombok.Data;

import java.util.List;

@Data
public class CompanyEmployeeRoleModuleDto {
    private int moduleId;
    private String moduleName;
    private List<Integer> moduleAssignedActions;
    private List<Integer> roleAssignedActions;
}

package com.timesheetspro_api.common.dto.CompanyEmployeeRoles;
import lombok.Data;

import java.util.List;

@Data
public class CompanyEmployeeRolesActionsDto {
    private List<CompanyEmployeeRoleFunctionalityDto> functionalities;
}

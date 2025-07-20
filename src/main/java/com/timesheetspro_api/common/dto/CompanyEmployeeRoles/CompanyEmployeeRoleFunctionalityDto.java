package com.timesheetspro_api.common.dto.CompanyEmployeeRoles;
import lombok.Data;

import java.util.List;

@Data
public class CompanyEmployeeRoleFunctionalityDto {
    private int functionalityId;
    private String functionalityName;
    private List<CompanyEmployeeRoleModuleDto> modules;
}

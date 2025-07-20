package com.timesheetspro_api.common.dto.CompanyModuleDto;

import lombok.Data;

import java.util.List;

@Data
public class AssignCompanyActionsToCompanyModuleDto {
    private int moduleId;
    private List<Integer> actionIds;
}

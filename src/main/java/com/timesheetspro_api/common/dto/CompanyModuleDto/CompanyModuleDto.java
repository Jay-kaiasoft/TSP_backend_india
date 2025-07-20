package com.timesheetspro_api.common.dto.CompanyModuleDto;

import lombok.Data;

import java.util.List;

@Data
public class CompanyModuleDto {
    private int moduleId;
    private String moduleName;
    private int functionalityId;
    private String functionalityName;
    private List<Integer> actions;
}

package com.timesheetspro_api.common.dto.module;

import lombok.Data;

import java.util.List;

@Data
public class ModuleDto {
    private Long moduleId;
    private String moduleName;
    private Long functionalityId;
    private String functionalityName;
    private List<Long> actions;
}

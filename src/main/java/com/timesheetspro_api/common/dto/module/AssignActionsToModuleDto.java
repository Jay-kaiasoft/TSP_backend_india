package com.timesheetspro_api.common.dto.module;

import lombok.Data;

import java.util.List;

@Data
public class AssignActionsToModuleDto {
    private Long moduleId;
    private List<Long> actionIds;
}

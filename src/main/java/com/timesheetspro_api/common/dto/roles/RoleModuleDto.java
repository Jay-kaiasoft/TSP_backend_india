package com.timesheetspro_api.common.dto.roles;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RoleModuleDto implements Serializable {
    private Long moduleId;
    private String moduleName;
    private List<Long> moduleAssignedActions;
    private List<Long> roleAssignedActions;
}

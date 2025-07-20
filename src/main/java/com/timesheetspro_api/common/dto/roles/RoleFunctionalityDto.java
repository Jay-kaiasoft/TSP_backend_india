package com.timesheetspro_api.common.dto.roles;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RoleFunctionalityDto implements Serializable {
    private Long functionalityId;
    private String functionalityName;
    private List<RoleModuleDto> modules;
}

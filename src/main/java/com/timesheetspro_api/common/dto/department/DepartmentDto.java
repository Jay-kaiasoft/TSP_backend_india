package com.timesheetspro_api.common.dto.department;

import lombok.Data;

@Data
public class DepartmentDto {
    private Long id;
    private String departmentName;
    private Integer companyId;
}

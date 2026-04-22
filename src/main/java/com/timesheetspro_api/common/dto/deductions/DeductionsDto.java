package com.timesheetspro_api.common.dto.deductions;

import lombok.Data;

@Data
public class DeductionsDto {
    private Integer id;
    private Integer employeeId;
    private String type;
    private String label;
    private Integer amount;
}

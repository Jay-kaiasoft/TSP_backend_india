package com.timesheetspro_api.common.dto.employeeStatement;

import lombok.Data;

import java.util.List;

@Data
public class SalaryStatementRequestDto {
    private List<Integer> employeeIds;
    private List<Integer> departmentIds;
    private Integer month;
}

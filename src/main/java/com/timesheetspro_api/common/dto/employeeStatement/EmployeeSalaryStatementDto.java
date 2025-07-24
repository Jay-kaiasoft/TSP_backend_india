package com.timesheetspro_api.common.dto.employeeStatement;

import lombok.Data;

@Data
public class EmployeeSalaryStatementDto {
    private Integer employeeId;
    private String employeeName;
    private String departmentName;
    private Integer basicSalary;
    private Integer overTime;
    private Integer otAmount;
    private Integer pfAmount;
    private Integer totalPfAmount;
    private Integer pfPercentage;
    private Integer ptAmount;
    private Integer totalEarnings;
    private Integer totalDeductions;
    private Integer netSalary;
}

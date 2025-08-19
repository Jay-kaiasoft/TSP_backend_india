package com.timesheetspro_api.common.dto.employeeStatement;

import lombok.Data;

@Data
public class EmployeeSalaryStatementDto {
    private Integer companyId;
    private Long clockInOutId;
    private Integer employeeId;
    private String employeeName;
    private Long departmentId;
    private String departmentName;
    private Integer basicSalary;
    private Integer totalEarnSalary;
    private Integer overTime;
    private Integer otAmount;
    private Integer pfAmount;
    private Integer totalPfAmount;
    private Integer pfPercentage;
    private Integer ptAmount;
    private Integer totalEarnings;
    private Integer otherDeductions;
    private Integer totalPenaltyAmount;
    private Integer totalDeductions;
    private Integer netSalary;
    private Integer totalPaidDays;
    private Integer totalWorkingDays;
    private Integer totalDays;
}

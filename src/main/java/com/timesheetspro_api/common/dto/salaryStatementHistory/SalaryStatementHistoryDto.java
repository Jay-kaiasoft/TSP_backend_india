package com.timesheetspro_api.common.dto.salaryStatementHistory;

import lombok.Data;

@Data
public class SalaryStatementHistoryDto {
    private Integer id;
    private Long clockInOutId;
    private Integer companyId;
    private Integer employeeId;
    private String employeeName;
    private Integer departmentId;
    private String departmentName;
    private Integer basicSalary;
    private Integer totalEarnSalary;
    private Integer otAmount;
    private Integer pfAmount;
    private Integer totalPfAmount;
    private Integer pfPercentage;
    private Integer ptAmount;
    private Integer totalEarnings;
    private Integer otherDeductions;
    private Integer totalDeductions;
    private Integer netSalary;
    private Integer year;
    private Integer monthNumber;
    private String monthYear;
    private Integer totalDays;
    private Integer totalWorkingDays;
    private String startDate;
    private String endDate;
    private String timeZone;
    private String note;
    private Integer generatedBy;
}

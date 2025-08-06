package com.timesheetspro_api.common.dto.employeeStatement;

import lombok.Data;

import java.util.List;

@Data
public class SalaryStatementRequestDto {
    private List<Integer> employeeIds;
    private List<Integer> departmentIds;
    private Integer month;
    private Integer year;
    private Integer companyId;
    private String startDate;
    private String endDate;
    private String timeZone;
}

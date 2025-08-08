package com.timesheetspro_api.common.dto.SalaryStatementMaster;

import lombok.Data;

@Data
public class SalaryStatementMasterDto {
    private Integer id;
    private Integer companyId;
    private Integer month;
    private Integer year;
    private Integer totalSalary;
    private Integer totalPf;
    private Integer totalPt;
    private String note;
}

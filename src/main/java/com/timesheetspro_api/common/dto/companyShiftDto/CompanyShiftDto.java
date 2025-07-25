package com.timesheetspro_api.common.dto.companyShiftDto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CompanyShiftDto {
    private int id;
    private int companyId;
    private String shiftName;
    private String shiftType;
    private Timestamp startTime;
    private Timestamp endTime;
    private float hours;
    private Integer totalHours;
}

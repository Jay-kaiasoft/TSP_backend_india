package com.timesheetspro_api.common.dto.companyShiftDto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CompanyShiftDto {
    private int id;
    private int companyId;
    private String shiftName;
    private String shiftType;
    private Timestamp timeStart;
    private Timestamp timeEnd;
    private float hours;
    private float totalHours;
}

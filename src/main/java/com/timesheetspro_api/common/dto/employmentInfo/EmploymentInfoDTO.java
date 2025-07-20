package com.timesheetspro_api.common.dto.employmentInfo;

import lombok.Data;

@Data
public class EmploymentInfoDTO {
    private int id;
    private String workPhone;
    private String ext;
    private String workEmail;
    private String hireDate;
    private String status;
    private String paidPension;
    private String statutoryEmployee;
    private String exclusionIndicator;
    private String keyEmployeeIndicator;
    private String hce;
    private String unionIndicator;
    private String eligibilityIndicator;
    private int employeeId;
}
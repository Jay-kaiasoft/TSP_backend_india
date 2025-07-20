package com.timesheetspro_api.common.dto.employeeBackAccountInfo;

import lombok.Data;

@Data
public class EmployeeBackAccountInfoDTO {
    private int id;
    private String accountType;
    private String ifscCode;
    private String bankName;
    private String branch;
    private String accountNumber;
    private String address;
    private int employeeId;
    private String passbookImage;
}
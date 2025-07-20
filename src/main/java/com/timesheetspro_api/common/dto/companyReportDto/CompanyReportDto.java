package com.timesheetspro_api.common.dto.companyReportDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReportDto {
    private int id;
    private String companyName;
    private String email;
    private String phone;
    private String registerDate;
    private int employeeCount;
}

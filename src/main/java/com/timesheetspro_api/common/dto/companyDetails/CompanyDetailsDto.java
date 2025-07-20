package com.timesheetspro_api.common.dto.companyDetails;

import com.timesheetspro_api.common.dto.CompanyEmployeeDto.CompanyEmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesDto;
import com.timesheetspro_api.common.dto.location.LocationDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CompanyDetailsDto {
    private int id;
    @NotBlank(message = "Company number is required")
    private String companyNo;
    @NotBlank(message = "Company name is required")
    private String companyName;
    private String dba;
    private String companyLogo;
    @NotBlank(message = "Company email is required")
    private String email;
    @NotBlank(message = "Company phone is required")
    private String phone;
    private String industryName;
    private String websiteUrl;
    private String registerDate;
    private String ein;
    private String organizationType;

    private List<LocationDto> locations;
    private CompanyEmployeeDto companyEmployeeDto;
    private List<CompanyEmployeeDto> employees;
    private List<Integer> deletedEmployeeId;

    private List<CompanyEmployeeRolesDto> roles;
}

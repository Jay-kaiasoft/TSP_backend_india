package com.timesheetspro_api.common.dto.CompanyEmployeeDto;

import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeDto {
    private int employeeId;
    @NotBlank(message = "Company is required")
    private int companyId;
    @NotBlank(message = "Role is required")
    private int roleId;
    @NotNull(message = "Username is required")
    private String userName;
    @NotNull(message = "Firstname is required")
    private String firstName;
    @NotNull(message = "Lastname is required")
    private String lastName;
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Password is required")
    private String password;
    @NotNull(message = "Phone is required")
    private String phone;
    private String profileImage;
    private CompanyEmployeeRolesDto companyEmployeeRolesDto;
    private String gender;
    private String dob;

    private String zipCode;
    private String city;
    private String state;
    private String country;
    @NotBlank(message = "Hourly rate is required")
    private Float hourlyRate;
    @NotNull(message = "Address is required")
    private String address1;
    private String address2;
    private String roleName;
    private String companyLocation;

}

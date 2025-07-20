package com.timesheetspro_api.common.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {

    private Long userId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;
    private String middleName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String phone;

    @NotBlank(message = "Password is required")
//    @Size(min = 8, message = "Password must be 8 characters")
    private String password;

    private Long hourlyRate;

    private String gender;

    @NotBlank(message = "Personal Identification Number is required")
    private String personalIdentificationNumber;

    private String address1;
    private String address2;
    private String city;
    private String zipCode;
    private String country;
    private String state;
    private String birthDate;
    private String emergencyContact;
    private String contactPhone;
    private String relationship;

    @NotNull(message = "Department is required")
    private Long departmentId;

    @NotNull(message = "Role is required")
    private Long roleId;

//    @NotNull(message = "User Shift is required")
    private Long userShiftId;
    private Long contractorId;

    private String roleName;
    private String profileImage;
    @NotBlank(message = "Username is required")
    private String userName;
}

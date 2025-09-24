package com.timesheetspro_api.common.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class LocationDto {
    private int id;
    @NotBlank(message = "Location name is required")
    private String locationName;
//    @NotBlank(message = "TimeZone is required")
//    private String timeZone;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;
    @NotBlank(message = "Country is required")
    private String country;
    @NotBlank(message = "Address is required")
    private String address1;
    private String address2;
    private String employeeCount;
    private String zipCode;
    @NotNull(message = "Company is required")
    private int companyId;
    private String externalId;
    private String geofenceId;
    private Integer isActive;
//    @NotNull(message = "Pay period is required")
    private Integer payPeriod;
    private String payPeriodStart;
    private String payPeriodEnd;

}

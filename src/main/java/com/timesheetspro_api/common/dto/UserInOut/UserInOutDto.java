package com.timesheetspro_api.common.dto.UserInOut;
import com.timesheetspro_api.common.dto.companyShiftDto.CompanyShiftDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInOutDto {
    private Long id;
    private String timeIn;
    private String timeOut;
    private int userId;
    private Float hourlyRate;
    private String userName;
    private String firstName;
    private String lastName;
    private String createdOn;
    private int locationId;
    private Integer companyId;
    private CompanyShiftDto companyShiftDto;
    private Integer isSalaryGenerate;
    private String timeZone;
    private String regular;
    private String breakTime;
    private String overtime;
    private String totalHours;
    private String workHours;
    private String status;
    private String department;
}

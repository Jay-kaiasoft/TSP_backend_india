package com.timesheetspro_api.common.dto.holidayTemplates;

import com.timesheetspro_api.common.dto.holidayTemplateDetails.HolidayTemplateDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class HolidayTemplatesDto {
    private Integer id;
    private String name;
    private Integer companyId;
    private Integer createdBy;
    private String createdByUserName;
    private List<HolidayTemplateDetailsDto> holidayTemplateDetailsList;
    private List<Integer> assignedEmployeeIds;
}

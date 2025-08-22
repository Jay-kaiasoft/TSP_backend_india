package com.timesheetspro_api.common.dto.holidayTemplateDetails;

import lombok.Data;

@Data
public class HolidayTemplateDetailsDto {
    private Integer id;
    private String name;
    private String date;
    private Integer holidayTemplateId;
}

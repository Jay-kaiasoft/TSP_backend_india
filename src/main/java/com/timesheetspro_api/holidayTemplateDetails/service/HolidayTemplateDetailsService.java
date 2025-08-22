package com.timesheetspro_api.holidayTemplateDetails.service;

import com.timesheetspro_api.common.dto.holidayTemplateDetails.HolidayTemplateDetailsDto;

import java.util.List;

public interface HolidayTemplateDetailsService {
    List<HolidayTemplateDetailsDto> getAllHolidayTemplateDetailsByTemplateId(Integer id);

    HolidayTemplateDetailsDto getHolidayTemplateDetailsById(Integer id);

    HolidayTemplateDetailsDto createHolidayTemplateDetails(HolidayTemplateDetailsDto holidayTemplateDetailsDto);

    HolidayTemplateDetailsDto updateHolidayTemplateDetails(Integer id, HolidayTemplateDetailsDto holidayTemplateDetailsDto);

    void deleteHolidayTemplateDetails(Integer id);
}

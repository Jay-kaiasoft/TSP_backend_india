package com.timesheetspro_api.holidayTemplates.service;

import com.timesheetspro_api.common.dto.holidayTemplates.HolidayTemplatesDto;

import java.util.List;

public interface HolidayTemplatesService {
    List<HolidayTemplatesDto> getAllHolidayTemplatesByCompanyId(Integer id);

    HolidayTemplatesDto getHolidayTemplateById(Integer id);

    HolidayTemplatesDto createHolidayTemplate(HolidayTemplatesDto holidayTemplatesDto);

    HolidayTemplatesDto updateHolidayTemplate(Integer id, HolidayTemplatesDto holidayTemplatesDto);

    void deleteHolidayTemplate(Integer id);

    boolean assignEmployees(Integer templateId, List<Integer> employeeIds, List<Integer> removeEmployeeIds);

    List<Integer> getAssignEmployees(Integer templateId);

}

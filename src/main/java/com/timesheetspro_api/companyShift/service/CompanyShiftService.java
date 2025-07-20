package com.timesheetspro_api.companyShift.service;

import com.timesheetspro_api.common.dto.companyShiftDto.CompanyShiftDto;

import java.util.List;

public interface CompanyShiftService {
    CompanyShiftDto getShiftById(int id);

    List<CompanyShiftDto> getAllShifts(int companyId);

    CompanyShiftDto createShift(CompanyShiftDto shiftDto);

    CompanyShiftDto updateShift(int id, CompanyShiftDto shiftDto);

    void deleteShift(int id);
}
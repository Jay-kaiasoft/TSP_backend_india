package com.timesheetspro_api.deductions.service;

import com.timesheetspro_api.common.dto.deductions.DeductionsDto;

import java.util.List;

public interface DeductionsService {
    List<DeductionsDto> findByEmployeeId(Integer id);
    DeductionsDto findById(Integer id);
    void saveDeductions(List<DeductionsDto> deductionsDto);
    void deleteById(Integer id);
}

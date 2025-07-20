package com.timesheetspro_api.employmentInfo.service;

import com.timesheetspro_api.common.dto.employmentInfo.EmploymentInfoDTO;

import java.util.List;

public interface EmploymentInfoService {
    EmploymentInfoDTO createEmploymentInfo(EmploymentInfoDTO dto);
    EmploymentInfoDTO getEmploymentInfoById(int id);
    List<EmploymentInfoDTO> getAllEmploymentInfo();
    EmploymentInfoDTO updateEmploymentInfo(int id, EmploymentInfoDTO dto);
    void deleteEmploymentInfo(int id);
}
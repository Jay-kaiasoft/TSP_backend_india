package com.timesheetspro_api.weeklyOff.serrvice;

import com.timesheetspro_api.common.dto.weeklyOff.WeeklyOffDto;

import java.util.List;

public interface WeeklyOffService {
    boolean assignEmployees(List<Integer> employeeIds, Integer weeklyOffId, List<Integer> removeEmployeeIds);

    List<WeeklyOffDto> getAllByCompany(Integer companyId);

    WeeklyOffDto getById(Integer id);

    WeeklyOffDto create(WeeklyOffDto dto);

    WeeklyOffDto update(Integer id, WeeklyOffDto dto);

    void delete(Integer id);

    void assignDefaultWeeklyOff(Integer id);

    List<Integer> getAssignedEmployees(Integer weeklyOffId);
}

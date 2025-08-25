package com.timesheetspro_api.companyEmployee.service;

import com.timesheetspro_api.common.dto.CompanyEmployeeDto.CompanyEmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeDto.EmployeeDto;

import java.util.List;
import java.util.Map;

public interface CompanyEmployeeService {

    List<Map<String, Object>> getReports(int companyId,String type,int month);

    List<Map<String, Object>> getAllEmployeeListByCompanyId(int companyId);

    List<CompanyEmployeeDto> getAllEmployeeByCompanyId(int companyId);

    CompanyEmployeeDto getEmployee(int id);

    CompanyEmployeeDto createEmployee(CompanyEmployeeDto companyEmployeeDto);

    CompanyEmployeeDto updateEmployee(int id, CompanyEmployeeDto companyEmployeeDto);

    void deleteEmployee(int id);

    String uploadEmployeeProfile(Integer companyId, Integer employeeId, String imagePath);

    boolean deleteEmployeeProfile(Integer companyId, Integer employeeId);

    EmployeeDto createEmployeeFromTSP(EmployeeDto employeeDto);

    EmployeeDto updateEmployeeFromTSP(int id, EmployeeDto employeeDto);

    String uploadEmployeeAadharImage(Integer companyId, Integer employeeId, String imagePath);

    boolean deleteEmployeeAadharImage(Integer companyId, Integer employeeId);

    Long getLastUserId();
}

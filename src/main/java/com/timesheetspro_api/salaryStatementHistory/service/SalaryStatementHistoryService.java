package com.timesheetspro_api.salaryStatementHistory.service;

import com.timesheetspro_api.common.dto.salaryStatementHistory.SalaryStatementHistoryDto;

import java.util.List;
import java.util.Map;

public interface SalaryStatementHistoryService {

    List<Map<String, Object>> filterSalaryStatementHistory(List<Integer> employeeId, List<Integer> departmentId, List<String> month,Integer companyId);

    SalaryStatementHistoryDto getSalaryStatementHistory(Integer id);

    Map<String, Object> addSalaryStatement(List<SalaryStatementHistoryDto> salaryStatement);

    SalaryStatementHistoryDto updateSalaryStatement(Integer id, SalaryStatementHistoryDto salaryStatement);

    void deleteSalaryStatement(Integer id);
}

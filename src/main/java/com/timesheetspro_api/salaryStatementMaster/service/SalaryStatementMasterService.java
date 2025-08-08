package com.timesheetspro_api.salaryStatementMaster.service;

import com.timesheetspro_api.common.dto.SalaryStatementMaster.SalaryStatementMasterDto;

import java.util.List;

public interface SalaryStatementMasterService {
    List<SalaryStatementMasterDto> getAllSalaryStatementMasters(int companyId);

    SalaryStatementMasterDto getSalaryStatementMastersByMonthAndYear(int companyId, int month, int year);

    SalaryStatementMasterDto getSalaryStatementMasterById(int id);

    SalaryStatementMasterDto createSalaryStatementMaster(SalaryStatementMasterDto salaryStatementMasterDto);

    SalaryStatementMasterDto updateSalaryStatementMaster(int id, SalaryStatementMasterDto salaryStatementMasterDto);

    void deleteSalaryStatementMaster(int id);
}

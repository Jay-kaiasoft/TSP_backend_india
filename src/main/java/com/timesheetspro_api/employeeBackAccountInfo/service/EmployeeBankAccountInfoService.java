package com.timesheetspro_api.employeeBackAccountInfo.service;

import com.timesheetspro_api.common.dto.employeeBackAccountInfo.EmployeeBackAccountInfoDTO;

import java.util.List;

public interface EmployeeBankAccountInfoService {
    EmployeeBackAccountInfoDTO createBankAccountInfo(EmployeeBackAccountInfoDTO dto);
    EmployeeBackAccountInfoDTO getBankAccountInfoById(int id);
    List<EmployeeBackAccountInfoDTO> getAllBankAccountInfo();
    EmployeeBackAccountInfoDTO updateBankAccountInfo(int id, EmployeeBackAccountInfoDTO dto);
    void deleteBankAccountInfo(int id);
    String uploadPassbookImage(Integer companyId, Integer id, String imagePath);
    boolean deletePassbookImage(Integer companyId, Integer id);
}
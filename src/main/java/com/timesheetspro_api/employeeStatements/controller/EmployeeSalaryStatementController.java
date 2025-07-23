package com.timesheetspro_api.employeeStatements.controller;

import com.timesheetspro_api.common.dto.employeeStatement.EmployeeSalaryStatementDto;
import com.timesheetspro_api.common.dto.employeeStatement.SalaryStatementRequestDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.employeeStatements.service.EmployeeSalaryStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee/statement")
public class EmployeeSalaryStatementController {

    @Autowired
    private EmployeeSalaryStatementService employeeSalaryStatementService;

    @PostMapping("/getEmployeeSalaryStatements")
    public ApiResponse<?> getEmployeeSalaryStatements(@RequestBody SalaryStatementRequestDto request) {
        try {
            List<EmployeeSalaryStatementDto> list = this.employeeSalaryStatementService.getEmployeeSalaryStatements(request);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch statement successfully", list);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch statement", null);
        }
    }

}

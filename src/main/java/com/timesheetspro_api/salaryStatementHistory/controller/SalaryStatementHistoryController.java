package com.timesheetspro_api.salaryStatementHistory.controller;

import com.timesheetspro_api.common.dto.overtimeRules.OvertimeRulesDto;
import com.timesheetspro_api.common.dto.salaryStatementHistory.SalaryStatementHistoryDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.salaryStatementHistory.service.SalaryStatementHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/salaryStatementHistory")
public class SalaryStatementHistoryController {

    @Autowired
    private SalaryStatementHistoryService salaryStatementHistoryService;

    @PostMapping("/getAllHistory")
    public ApiResponse<?> filterSalaryStatementHistory(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, Object> requestBody) {
        List<Integer> employeeId = (List<Integer>) requestBody.get("employeeIds");
        List<Integer> departmentId = (List<Integer>) requestBody.get("departmentIds");
        List<String> month = (List<String>) requestBody.get("month");

        Map<String, Object> resBody = new HashMap<>();

        try {
            List<Map<String, Object>> salaryList = this.salaryStatementHistoryService.filterSalaryStatementHistory(employeeId,departmentId,month);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch salary data successfully", salaryList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch salary data", resBody);
        }
    }

    @GetMapping("/getHistory/{id}")
    public ApiResponse<?> getSalaryStatementHistory(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();

        try {
            SalaryStatementHistoryDto salaryList = this.salaryStatementHistoryService.getSalaryStatementHistory(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch salary data successfully", salaryList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch salary data", resBody);
        }
    }

    @PostMapping("/addHistory")
    public ApiResponse<?> addSalaryStatement(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody List<SalaryStatementHistoryDto> salaryStatemento) {
        Map<String, Object> resBody = new HashMap<>();

        try {
            Map<String, Object> res = this.salaryStatementHistoryService.addSalaryStatement(salaryStatemento);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Salary data added successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PatchMapping("/updateHistory/{id}")
    public ApiResponse<?> updateSalaryStatement(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id, @RequestBody SalaryStatementHistoryDto salaryStatementHistoryDto) {
        Map<String, Object> resBody = new HashMap<>();

        try {
            SalaryStatementHistoryDto updatedSalaryStatement = this.salaryStatementHistoryService.updateSalaryStatement(id, salaryStatementHistoryDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Salary data updated successfully", updatedSalaryStatement);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update salary data", resBody);
        }
    }

    @DeleteMapping("/deleteHistory/{id}")
    public ApiResponse<?> deleteSalaryStatement(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();

        try {
            this.salaryStatementHistoryService.deleteSalaryStatement(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Salary data deleted successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete salary data", resBody);
        }
    }

}

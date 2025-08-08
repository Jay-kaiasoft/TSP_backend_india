package com.timesheetspro_api.salaryStatementMaster.controller;

import com.timesheetspro_api.common.dto.SalaryStatementMaster.SalaryStatementMasterDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.salaryStatementMaster.service.SalaryStatementMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statementMaster")
public class SalaryStatementMasterController {

    @Autowired
    private SalaryStatementMasterService salaryStatementMasterService;

    @GetMapping("/getAllStatementMasters/{id}")
    public ApiResponse<?> getAllStatementMasters(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<SalaryStatementMasterDto> res = this.salaryStatementMasterService.getAllSalaryStatementMasters(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch salary statement successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch salary statement", resBody);
        }
    }

    @GetMapping("/getAllStatementMasters")
    public ApiResponse<?> getAllStatementMasters(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                                 @RequestParam(required = false) Integer companyId,
                                                 @RequestParam(required = false) Integer month,
                                                 @RequestParam(required = false) Integer year) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            SalaryStatementMasterDto res = this.salaryStatementMasterService.getSalaryStatementMastersByMonthAndYear(companyId, month, year);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch salary statement successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch salary statement", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getSalaryStatementMasterById(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            SalaryStatementMasterDto res = this.salaryStatementMasterService.getSalaryStatementMasterById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch salary statement successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch salary statement", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createSalaryStatementMaster(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody SalaryStatementMasterDto salaryStatementMasterDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            SalaryStatementMasterDto res = this.salaryStatementMasterService.createSalaryStatementMaster(salaryStatementMasterDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Salary statement added successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to add salary statement", resBody);
        }
    }

    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateSalaryStatementMaster(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable int companyId, @RequestBody SalaryStatementMasterDto salaryStatementMasterDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            SalaryStatementMasterDto res = this.salaryStatementMasterService.updateSalaryStatementMaster(companyId, salaryStatementMasterDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Salary statement updated successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to updated salary statement", resBody);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteSalaryStatementMaster(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.salaryStatementMasterService.deleteSalaryStatementMaster(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Salary statement deleted successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete salary statement", resBody);
        }
    }
}

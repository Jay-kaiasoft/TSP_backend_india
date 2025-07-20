package com.timesheetspro_api.employeeType.controller;


import com.timesheetspro_api.common.dto.employeeType.EmployeeTypeDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.employeeType.service.EmployeeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/employeeType")
public class EmployeeTypeController {

    @Autowired
    private EmployeeTypeService employeeTypeService;

    @GetMapping("/get/All")
    public ApiResponse<?> getAllType() {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee type fetched successfully", this.employeeTypeService.getAllEmployeeType());
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), responseBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getEmployee(@PathVariable int id) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee type fetched successfully", this.employeeTypeService.getEmployeeType(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), responseBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createEmployeeType(@RequestBody EmployeeTypeDto dto) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            this.employeeTypeService.createEmployeeType(dto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Employee type created successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), responseBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateEmployeeType(@PathVariable int id, @RequestBody EmployeeTypeDto dto) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            this.employeeTypeService.updateEmployeeType(id, dto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee type updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), responseBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteEmployeeType(@PathVariable int id) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            this.employeeTypeService.deleteEmployeeType(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee type deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), responseBody);
        }
    }
}

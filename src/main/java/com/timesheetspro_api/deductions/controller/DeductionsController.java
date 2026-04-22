package com.timesheetspro_api.deductions.controller;

import com.timesheetspro_api.common.dto.country.CountryDto;
import com.timesheetspro_api.common.dto.deductions.DeductionsDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.country.service.CountryService;
import com.timesheetspro_api.deductions.service.DeductionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/deductions")
public class DeductionsController {

    @Autowired
    private DeductionsService  deductionsService;

    @GetMapping("/get/all")
    public ApiResponse<?> getAllDeductions(@RequestParam(required = true) Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch deductions details successfully", this.deductionsService.findByEmployeeId(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch deductions details", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getDeductions(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch deductions details successfully", this.deductionsService.findById(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch deductions details", resBody);
        }
    }

    @PostMapping("/save")
    public ApiResponse<?> saveDeductions(@RequestBody List<DeductionsDto> deductionsDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.deductionsService.saveDeductions(deductionsDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Deductions save successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to save deductions", resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteDeductions(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.deductionsService.deleteById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Deductions delete successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete deductions", resBody);
        }
    }
}

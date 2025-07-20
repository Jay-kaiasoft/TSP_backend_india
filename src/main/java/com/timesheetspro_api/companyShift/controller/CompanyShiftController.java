package com.timesheetspro_api.companyShift.controller;

import com.timesheetspro_api.common.dto.companyShiftDto.CompanyShiftDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyShift.service.CompanyShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/companyShift")
public class CompanyShiftController {
    @Autowired
    private CompanyShiftService companyShiftService;

    @GetMapping("/get/all/{companyId}")
    public ApiResponse<?> getAllShifts(@PathVariable int companyId) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Shifts fetched successfully", this.companyShiftService.getAllShifts(companyId));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch shifts", new HashMap<>());
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getShiftById(@PathVariable int id) {
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Shifts fetched successfully", this.companyShiftService.getShiftById(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch shifts", new HashMap<>());
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createShift(@RequestBody CompanyShiftDto companyShiftDto) {
        try {
            CompanyShiftDto companyShiftDto1 = this.companyShiftService.createShift(companyShiftDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Shifts created successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create shift", new HashMap<>());
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateShift(@PathVariable int id, @RequestBody CompanyShiftDto companyShiftDto) {
        try {
            CompanyShiftDto companyShiftDto1 = this.companyShiftService.updateShift(id, companyShiftDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Shifts updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create shift", new HashMap<>());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteShift(@PathVariable int id) {
        try {
            this.companyShiftService.deleteShift(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Shifts fetched successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch shifts", new HashMap<>());
        }
    }
}

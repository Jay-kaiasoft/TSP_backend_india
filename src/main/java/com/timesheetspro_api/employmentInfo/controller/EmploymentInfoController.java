package com.timesheetspro_api.employmentInfo.controller;

import com.timesheetspro_api.common.dto.employmentInfo.EmploymentInfoDTO;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.employmentInfo.service.EmploymentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employmentInfo")
public class EmploymentInfoController {

    @Autowired
    private EmploymentInfoService employmentInfoService;

    @GetMapping("/get/all")
    public ApiResponse<?> getAllEmploymentInfo() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<EmploymentInfoDTO> employmentInfoList = employmentInfoService.getAllEmploymentInfo();
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetched employment info successfully", employmentInfoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch employment info", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createEmploymentInfo(@RequestBody EmploymentInfoDTO dto) {
        try {
            EmploymentInfoDTO created = employmentInfoService.createEmploymentInfo(dto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Employment info created successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getEmploymentInfoById(@PathVariable int id) {
        try {
            EmploymentInfoDTO dto = employmentInfoService.getEmploymentInfoById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetched employment info successfully", dto);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Employment info not found", null);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateEmploymentInfo(@PathVariable int id, @RequestBody EmploymentInfoDTO dto) {
        try {
            EmploymentInfoDTO updated = employmentInfoService.updateEmploymentInfo(id, dto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employment info updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteEmploymentInfo(@PathVariable int id) {
        try {
            employmentInfoService.deleteEmploymentInfo(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employment info deleted successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete employment info", null);
        }
    }
}
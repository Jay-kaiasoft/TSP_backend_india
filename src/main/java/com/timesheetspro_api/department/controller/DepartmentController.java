package com.timesheetspro_api.department.controller;

import com.timesheetspro_api.common.dto.department.DepartmentDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.department.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


    @GetMapping("/get/all/{companyId}")
    public ApiResponse<?> getAllDepartment(@PathVariable Integer companyId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<DepartmentDto> departmentDtoList = this.departmentService.getALlDepartment(companyId);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch department details successfully", departmentDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch departments details", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getDepartment(@PathVariable Long id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            DepartmentDto department = this.departmentService.getDepartment(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch department details successfully", department);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch department details", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createDepartment(@Valid @RequestBody DepartmentDto departmentDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.departmentService.createDepartment(departmentDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Create department details successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to create department details", resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateDepartment(@Valid @RequestBody DepartmentDto departmentDto, @PathVariable Long id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.departmentService.updateDepartment(id, departmentDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Update department details successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update department details", resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteDepartment(@PathVariable Long id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.departmentService.deleteDepartment(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Delete department details successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete department details", resBody);
        }
    }
}
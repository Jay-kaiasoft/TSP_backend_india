package com.timesheetspro_api.companyRoleActions.controller;

import com.timesheetspro_api.common.dto.companyActionsDto.CompanyActionsDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyRoleActions.service.CompanyRoleActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/companyRoleActions")
public class CompanyRoleActionController {
    @Autowired
    private CompanyRoleActionService companyRoleActionService;

    @GetMapping("/get/all")
    public ApiResponse<?> getAllCompanyRoleActions() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<CompanyActionsDto> companyActionsDtos = this.companyRoleActionService.getCompanyActions();
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch actions details successfully", companyActionsDtos);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch actions details", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getAction(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch actions details successfully", this.companyRoleActionService.getActions(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch actions details", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createAction(@RequestBody CompanyActionsDto companyActionsDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Actions created successfully", this.companyRoleActionService.createActions(companyActionsDto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to create action", resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateAction(@PathVariable int id, @RequestBody CompanyActionsDto companyActionsDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Actions updated successfully", this.companyRoleActionService.updateActions(id,companyActionsDto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update action", resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteAction(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.companyRoleActionService.deleteActions(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Actions deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete action", resBody);
        }
    }
}

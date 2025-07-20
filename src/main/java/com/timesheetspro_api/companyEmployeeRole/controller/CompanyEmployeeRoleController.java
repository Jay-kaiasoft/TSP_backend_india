package com.timesheetspro_api.companyEmployeeRole.controller;


import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyEmployeeRole.service.CompanyEmployeeRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employeeRole")
public class CompanyEmployeeRoleController {

    @Autowired
    private CompanyEmployeeRoleService companyEmployeeRoleService;

    @GetMapping("/getAllRoleList")
    public ApiResponse<?> getAllRolesList() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Roles list fetched successfully", this.companyEmployeeRoleService.getAllRolesList());
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch roles list", resBody);
        }
    }

    @GetMapping("/rolesListPage")
    public ApiResponse<?> rolesList(@RequestParam(value = "searchKey", required = false) String searchKey, Pageable pageable) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            resBody = this.companyEmployeeRoleService.rolesList(searchKey, pageable);
            return new ApiResponse<>(HttpStatus.OK.value(), "Roles list fetched successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch roles list", resBody);
        }
    }

    @GetMapping("/getAllRoles")
    public ApiResponse<?> getAllRoles() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            resBody = this.companyEmployeeRoleService.getAllRoles();
            return new ApiResponse<>(HttpStatus.OK.value(), "Roles list fetched successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch roles list", resBody);
        }
    }

    @GetMapping("/getActions/{roleId}")
    public ApiResponse<?> getActions(@PathVariable int roleId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Role's policies fetched successfully", this.companyEmployeeRoleService.getPolicy(roleId));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch", resBody);
        }
    }


    @GetMapping("/getAllCompanyEmployeeRoles/{id}")
    public ApiResponse<?> getAllCompanyEmployeeRoles(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<CompanyEmployeeRolesDto> res = this.companyEmployeeRoleService.getAllRolesByCompanyId(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch employee roles successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch employee roles", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getEmployeeRoles(@PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyEmployeeRolesDto res = this.companyEmployeeRoleService.getRole(Integer.parseInt(id));
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch employee roles successfully", res);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch employee roles", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createEmployeeRoles(@RequestBody CompanyEmployeeRolesDto companyEmployeeRolesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyEmployeeRolesDto res = this.companyEmployeeRoleService.createRole(companyEmployeeRolesDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Employee role added successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to add employee roles", resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateEmployeeRoles(@PathVariable int id, @RequestBody CompanyEmployeeRolesDto companyEmployeeRolesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyEmployeeRolesDto res = this.companyEmployeeRoleService.updateRole(id, companyEmployeeRolesDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee role updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update employee roles", resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteEmployeeRoles(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.companyEmployeeRoleService.deleteRole(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee role deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete employee roles", resBody);
        }
    }
}

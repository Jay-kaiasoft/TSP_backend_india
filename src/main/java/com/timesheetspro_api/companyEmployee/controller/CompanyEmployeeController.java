package com.timesheetspro_api.companyEmployee.controller;


import com.timesheetspro_api.common.dto.CompanyEmployeeDto.CompanyEmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeDto.EmployeeDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyEmployee.service.CompanyEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/companyEmployee")
public class CompanyEmployeeController {

    @Autowired
    private CompanyEmployeeService companyEmployeeService;

    @GetMapping("/getAllCompanyEmployee/{companyId}")
    public ApiResponse<?> getAllEmployeeByCompanyId(@PathVariable int companyId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<CompanyEmployeeDto> companyEmployeeDtoList = this.companyEmployeeService.getAllEmployeeByCompanyId(companyId);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch employee details successfully", companyEmployeeDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch company details", resBody);
        }
    }

    @GetMapping("/getEmployeePFAndPTReport/{companyId}/{type}/{month}")
    public ApiResponse<?> getEmployeePFAndPTReport(@PathVariable int companyId, @PathVariable String type,@PathVariable String month) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<Map<String, Object>> companyEmployeeDtoList = this.companyEmployeeService.getReports(companyId, type,Integer.parseInt(month));
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch employee details successfully", companyEmployeeDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch company details", resBody);
        }
    }

    @GetMapping("/getAllEmployeeListByCompanyId/{companyId}")
    public ApiResponse<?> getAllEmployeeListByCompanyId(@PathVariable int companyId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<Map<String, Object>>  companyEmployeeDtoList = this.companyEmployeeService.getAllEmployeeListByCompanyId(companyId);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch employee details successfully", companyEmployeeDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch company details", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getEmployee(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyEmployeeDto companyEmployeeDto = this.companyEmployeeService.getEmployee(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch employee details successfully", companyEmployeeDto);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch employee details", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createEmployee(@RequestBody CompanyEmployeeDto companyEmployeeDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyEmployeeDto res = this.companyEmployeeService.createEmployee(companyEmployeeDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Employee added successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateEmployee(@PathVariable int id, @RequestBody CompanyEmployeeDto companyEmployeeDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyEmployeeDto res = this.companyEmployeeService.updateEmployee(id, companyEmployeeDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee updated successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteEmployee(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.companyEmployeeService.deleteEmployee(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to deleted employee", resBody);
        }
    }

    @PostMapping("/uploadEmployeeProfile")
    public ApiResponse<?> uploadEmployeeProfile(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, Object> req) {

        Map<String, Object> resBody = new HashMap<>();
        try {
            String path = this.companyEmployeeService.uploadEmployeeProfile(Integer.parseInt(req.get("companyId").toString()), Integer.parseInt(req.get("employeeId").toString()), req.get("employee").toString());
            if (path.equals("Error")) {
                return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Image does not exist in the directory", "");
            }
            return new ApiResponse<>(HttpStatus.OK.value(), "Profile image update successfully", path);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update profile image", resBody);
        }
    }

    @DeleteMapping("/deleteEmployeeImage/{companyId}/{employeeId}")
    public ApiResponse<?> deleteEmployeeImage(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer companyId, @PathVariable Integer employeeId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            if (this.companyEmployeeService.deleteEmployeeProfile(companyId, employeeId)) {
                return new ApiResponse<>(HttpStatus.OK.value(), "Profile image deleted successfully", "");
            }
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Profile image not found", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete profile image", resBody);
        }
    }

    @PostMapping("/uploadEmployeeAadharImage")
    public ApiResponse<?> uploadEmployeeAadharImage(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, Object> req) {

        Map<String, Object> resBody = new HashMap<>();
        try {
            String path = this.companyEmployeeService.uploadEmployeeAadharImage(Integer.parseInt(req.get("companyId").toString()), Integer.parseInt(req.get("employeeId").toString()), req.get("employee").toString());
            if (path.equals("Error")) {
                return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Image does not exist in the directory", "");
            }
            return new ApiResponse<>(HttpStatus.OK.value(), "Aadhar image update successfully", path);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update aadhar image", resBody);
        }
    }

    @DeleteMapping("/deleteEmployeeAadharImage/{companyId}/{employeeId}")
    public ApiResponse<?> deleteEmployeeAadharImage(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer companyId, @PathVariable Integer employeeId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            if (this.companyEmployeeService.deleteEmployeeAadharImage(companyId, employeeId)) {
                return new ApiResponse<>(HttpStatus.OK.value(), "Aadhar image deleted successfully", "");
            }
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Aadhar image not found", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete aadhar image", resBody);
        }
    }

    @PostMapping("/createEmployee")
    public ApiResponse<?> createEmployeeFromTSP(@RequestBody EmployeeDto employeeDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            EmployeeDto res = this.companyEmployeeService.createEmployeeFromTSP(employeeDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Employee added successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PutMapping("/updateEmployee/{id}")
    public ApiResponse<?> updateEmployeeFromTSP(@PathVariable int id, @RequestBody EmployeeDto companyEmployeeDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            EmployeeDto res = this.companyEmployeeService.updateEmployeeFromTSP(id, companyEmployeeDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Employee updated successfully", res);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }
}

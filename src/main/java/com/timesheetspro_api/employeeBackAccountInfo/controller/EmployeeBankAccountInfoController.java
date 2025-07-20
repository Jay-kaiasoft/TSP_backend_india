package com.timesheetspro_api.employeeBackAccountInfo.controller;

import com.timesheetspro_api.common.dto.employeeBackAccountInfo.EmployeeBackAccountInfoDTO;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.employeeBackAccountInfo.service.EmployeeBankAccountInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employeeBankInfo")
public class EmployeeBankAccountInfoController {

    @Autowired
    private EmployeeBankAccountInfoService employeeBankAccountInfoService;

    @PostMapping("/create")
    public ApiResponse<?> createBankAccountInfo(@RequestBody EmployeeBackAccountInfoDTO dto) {
        try {
            EmployeeBackAccountInfoDTO createdDTO = employeeBankAccountInfoService.createBankAccountInfo(dto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Bank Account Info added successfully", createdDTO);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to add bank account info", null);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getBankAccountInfoById(@PathVariable int id) {
        try {
            EmployeeBackAccountInfoDTO dto = employeeBankAccountInfoService.getBankAccountInfoById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch Bank Account Info successfully", dto);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch Bank Account Info", null);
        }
    }

    @GetMapping("/get/all")
    public ApiResponse<?> getAllBankAccountInfo() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<EmployeeBackAccountInfoDTO> bankAccountInfoList = employeeBankAccountInfoService.getAllBankAccountInfo();
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch all Bank Account Info successfully", bankAccountInfoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch Bank Account Info", resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateBankAccountInfo(@PathVariable int id, @RequestBody EmployeeBackAccountInfoDTO dto) {
        try {
            EmployeeBackAccountInfoDTO updatedDTO = employeeBankAccountInfoService.updateBankAccountInfo(id, dto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Bank Account Info updated successfully", updatedDTO);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update Bank Account Info", null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteBankAccountInfo(@PathVariable int id) {
        try {
            employeeBankAccountInfoService.deleteBankAccountInfo(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Bank Account Info deleted successfully", null);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete Bank Account Info", null);
        }
    }

    @PostMapping("/uploadPassbookImage")
    public ApiResponse<?> uploadPassbookImage(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, Object> req) {

        Map<String, Object> resBody = new HashMap<>();
        try {
            String path = this.employeeBankAccountInfoService.uploadPassbookImage(Integer.parseInt(req.get("companyId").toString()), Integer.parseInt(req.get("bankId").toString()), req.get("bank").toString());
            if (path.equals("Error")) {
                return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Image does not exist in the directory", "");
            }
            return new ApiResponse<>(HttpStatus.OK.value(), "Passbook image update successfully", path);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update passbook image", resBody);
        }
    }

    @DeleteMapping("/deletePassbookImage/{companyId}/{bankId}")
    public ApiResponse<?> deletePassbookImage(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer companyId, @PathVariable Integer bankId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            if (this.employeeBankAccountInfoService.deletePassbookImage(companyId, bankId)) {
                return new ApiResponse<>(HttpStatus.OK.value(), "Passbook image deleted successfully", "");
            }
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Passbook image not found", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete passbook image", resBody);
        }
    }
}
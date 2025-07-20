package com.timesheetspro_api.contractor.controller;

import com.timesheetspro_api.common.dto.contractor.ContractorDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.contractor.service.ContractorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contractor")
public class ContractorController {

    @Autowired
    private ContractorService contractorService;

    @GetMapping("/get/all")
    public ApiResponse<?> getAllContractor() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<ContractorDto> contractorDtoList = this.contractorService.getAllContractors();
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch contractor details successfully", contractorDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch contractor details", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getContractor(@PathVariable Long id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            ContractorDto contractorDto = this.contractorService.getContractor(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch contractor details successfully", contractorDto);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch contractor details", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createContractor(@Valid @RequestBody ContractorDto contractorDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.contractorService.createContractor(contractorDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Contractor details added successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to create contractor details", resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateContractor(@Valid @RequestBody ContractorDto contractorDto, @PathVariable Long id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.contractorService.updateContractor(id,contractorDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Contractor details updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update contractor details", resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteContractor(@PathVariable Long id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.contractorService.deleteContractor(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Contractor deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete contractor details", resBody);
        }
    }
}
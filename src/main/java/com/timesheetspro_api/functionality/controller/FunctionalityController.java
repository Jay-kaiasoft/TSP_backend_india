package com.timesheetspro_api.functionality.controller;

import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.dto.functionality.FunctionalityDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.functionality.service.FunctionalityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/functionality")
public class FunctionalityController {
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private FunctionalityService functionalityService;

    @GetMapping("/getAllFunctionality")
    public ApiResponse<?> getAllFunctionality(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Functionality fetched successfully", this.functionalityService.getAllFunctionality());
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getFunctionality(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Functionality fetched successfully", this.functionalityService.getFunctionality(Long.parseLong(id)));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createFunctionality(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody FunctionalityDto functionalityDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.functionalityService.createFunctionality(functionalityDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Functionality created successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateFunctionality(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id, @Valid @RequestBody FunctionalityDto functionalityDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.functionalityService.updateFunctionality(Long.parseLong(id), functionalityDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Functionality update successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteFunctionality(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.functionalityService.deleteFunctionality(Long.parseLong(id));
            return new ApiResponse<>(HttpStatus.OK.value(), "Functionality deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

}


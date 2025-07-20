package com.timesheetspro_api.companyTheme.controller;

import com.timesheetspro_api.common.dto.companyTheme.CompanyThemeDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyTheme.service.CompanyThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/companyTheme")
public class CompanyThemeController {

    @Autowired
    private CompanyThemeService companyThemeService;

    @GetMapping("/get/all/{id}")
    public ApiResponse<?> getAllCompanyTheme(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyThemeDto companyTheme = this.companyThemeService.getAllTheme(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch theme details successfully", companyTheme);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch theme details", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getCompanyTheme(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyThemeDto companyTheme = this.companyThemeService.getTheme(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch theme details successfully", companyTheme);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch theme details", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createCompanyTheme(@RequestBody CompanyThemeDto companyThemeDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyThemeDto companyTheme = this.companyThemeService.createTheme(companyThemeDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Theme created successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateCompanyTheme(@PathVariable int id,@RequestBody CompanyThemeDto companyThemeDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyThemeDto companyTheme = this.companyThemeService.updateTheme(id,companyThemeDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Theme updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteCompanyTheme(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.companyThemeService.deleteTheme(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Theme deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }
}

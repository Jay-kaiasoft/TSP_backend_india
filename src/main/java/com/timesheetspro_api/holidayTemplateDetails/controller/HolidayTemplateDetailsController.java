package com.timesheetspro_api.holidayTemplateDetails.controller;

import com.timesheetspro_api.common.dto.holidayTemplateDetails.HolidayTemplateDetailsDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.holidayTemplateDetails.service.HolidayTemplateDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/holidayTemplateDetails")
public class HolidayTemplateDetailsController {

    @Autowired
    private HolidayTemplateDetailsService holidayTemplatesService;

    @GetMapping("/get/all/{id}")
    public ApiResponse<?> getAllHolidayTemplatesByTemplateId(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Holiday template details fetched successfully", this.holidayTemplatesService.getHolidayTemplateDetailsById(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getHolidayTemplateDetails(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Holiday template details fetched successfully", this.holidayTemplatesService.getHolidayTemplateDetailsById(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createHolidayTemplateDetails(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody HolidayTemplateDetailsDto holidayTemplatesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Holiday template details created successfully", this.holidayTemplatesService.createHolidayTemplateDetails(holidayTemplatesDto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateHolidayTemplateDetails(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id, @RequestBody HolidayTemplateDetailsDto holidayTemplatesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Holiday template details updated successfully", this.holidayTemplatesService.updateHolidayTemplateDetails(id, holidayTemplatesDto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteHolidayTemplateDetails(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.holidayTemplatesService.deleteHolidayTemplateDetails(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Holiday template details updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }
}

package com.timesheetspro_api.holidayTemplates.controller;

import com.timesheetspro_api.common.dto.holidayTemplates.HolidayTemplatesDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.holidayTemplates.service.HolidayTemplatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/holidayTemplates")
public class HolidayTemplatesController {

    @Autowired
    private HolidayTemplatesService holidayTemplatesService;

    @GetMapping("/get/all/{id}")
    public ApiResponse<?> getAllHolidayTemplatesByCompanyId(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Holiday templates fetched successfully", this.holidayTemplatesService.getAllHolidayTemplatesByCompanyId(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getHolidayTemplates(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Holiday template fetched successfully", this.holidayTemplatesService.getHolidayTemplateById(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createHolidayTemplates(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody HolidayTemplatesDto holidayTemplatesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Holiday template created successfully", this.holidayTemplatesService.createHolidayTemplate(holidayTemplatesDto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateHolidayTemplates(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id, @RequestBody HolidayTemplatesDto holidayTemplatesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Holiday template updated successfully", this.holidayTemplatesService.updateHolidayTemplate(id, holidayTemplatesDto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteHolidayTemplates(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.holidayTemplatesService.deleteHolidayTemplate(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Holiday template deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/assignEmployees")
    public ApiResponse<?> assignEmployees(@RequestBody Map<String, Object> data) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            Integer id = (Integer) data.get("id");
            List<Integer> employeeIds = (List<Integer>) data.get("employeeIds");

            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Template assigned successfully",
                    this.holidayTemplatesService.assignEmployees(id, employeeIds)
            );

        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }
}

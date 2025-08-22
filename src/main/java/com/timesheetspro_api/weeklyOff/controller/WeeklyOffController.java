package com.timesheetspro_api.weeklyOff.controller;

import com.timesheetspro_api.common.dto.weeklyOff.WeeklyOffDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.weeklyOff.serrvice.WeeklyOffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weekly-off")
public class WeeklyOffController {

    @Autowired
    private WeeklyOffService weeklyOffService;

    @GetMapping("/get/all/{id}")
    public ApiResponse<?> getAllByCompany(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Template fetched successfully", this.weeklyOffService.getAllByCompany(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getById(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Template fetched successfully", this.weeklyOffService.getById(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/assignEmployees")
    public ApiResponse<?> assignEmployees(@RequestBody Map<String, Object> data) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            Integer weekOffId = (Integer) data.get("weekOffId");
            List<Integer> employeeIds = (List<Integer>) data.get("employeeIds");

            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Template assigned successfully",
                    this.weeklyOffService.assignEmployees(employeeIds, weekOffId)
            );

        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> create(@RequestBody WeeklyOffDto dto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Template created successfully", this.weeklyOffService.create(dto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PatchMapping("/update/{id}")
    public ApiResponse<?> update(@PathVariable Integer id, @RequestBody WeeklyOffDto dto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Template updated successfully", this.weeklyOffService.update(id, dto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> delete(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.weeklyOffService.delete(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Template deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @GetMapping("/assignDefaultTemplate/{id}")
    public ApiResponse<?> assignDefaultTemplate(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.weeklyOffService.assignDefaultWeeklyOff(id);
            return new ApiResponse<>(HttpStatus.OK.value(),"This template is set as default.", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }
}

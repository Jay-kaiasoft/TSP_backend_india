package com.timesheetspro_api.attendancePenaltyRules.controller;

import com.timesheetspro_api.attendancePenaltyRules.service.AttendancePenaltyRulesService;
import com.timesheetspro_api.common.dto.attendancePenaltyRules.AttendancePenaltyRulesDto;
import com.timesheetspro_api.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/attendancePenaltyRules")
public class AttendancePenaltyRulesController {
    @Autowired
    private AttendancePenaltyRulesService attendancePenaltyRulesService;

    @GetMapping("/get/all/{companyId}")
    public ApiResponse<?> findAllByCompanyId(@PathVariable String companyId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch attendance penalty rules successfully", this.attendancePenaltyRulesService.findAllByCompanyId(Integer.parseInt(companyId)));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch attendance penalty rules", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> findAllById(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch attendance penalty rule successfully", this.attendancePenaltyRulesService.findById(id));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch attendance penalty rule", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createAttendancePenaltyRule(@RequestBody AttendancePenaltyRulesDto attendancePenaltyRules) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Attendance penalty rules created successfully", this.attendancePenaltyRulesService.create(attendancePenaltyRules));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateAttendancePenaltyRule(@PathVariable Integer id, @RequestBody AttendancePenaltyRulesDto attendancePenaltyRulesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Attendance penalty rules update successfully", this.attendancePenaltyRulesService.update(id, attendancePenaltyRulesDto));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteAttendancePenaltyRule(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.attendancePenaltyRulesService.deleteById(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Attendance penalty rule deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete attendance penalty rule", resBody);
        }
    }
}

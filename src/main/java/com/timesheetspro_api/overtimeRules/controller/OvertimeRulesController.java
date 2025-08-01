package com.timesheetspro_api.overtimeRules.controller;

import com.timesheetspro_api.common.dto.overtimeRules.OvertimeRulesDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.overtimeRules.service.OvertimeRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/overtimerules")
public class OvertimeRulesController {

    @Autowired
    private OvertimeRulesService overtimeRulesService;

    @GetMapping("/getAllOvertimeRules/{id}")
    public ApiResponse<?> getAllOvertimeRules(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<OvertimeRulesDto> overTimeRules = this.overtimeRulesService.getAllOvertimeRules(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch overtime rules successfully", overTimeRules);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch overtime rules", resBody);
        }
    }

    @GetMapping("/getOvertimeRule/{id}")
    public ApiResponse<?> getOvertimeRule(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            OvertimeRulesDto overTimeRules = this.overtimeRulesService.getOvertimeRule(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch overtime rules successfully", overTimeRules);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch overtime rules", resBody);
        }
    }

    @PostMapping("/createOvertimeRule/{id}")
    public ApiResponse<?> createOvertimeRule(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id, @RequestBody OvertimeRulesDto overtimeRulesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            OvertimeRulesDto overTimeRules = this.overtimeRulesService.createOvertimeRule(overtimeRulesDto, id);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Overtime rule created successfully", overTimeRules);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PatchMapping("/updateOvertimeRule/{id}")
    public ApiResponse<?> updateOvertimeRule(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id, @RequestBody OvertimeRulesDto overtimeRulesDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            OvertimeRulesDto overTimeRules = this.overtimeRulesService.updateOvertimeRule(id, overtimeRulesDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Overtime rule updated successfully", overTimeRules);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/deleteOvertimeRule/{id}")
    public ApiResponse<?> deleteOvertimeRule(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.overtimeRulesService.deleteOvertimeRule(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Overtime rule deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }
}

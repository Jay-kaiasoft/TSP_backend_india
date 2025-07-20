package com.timesheetspro_api.userShift.controller;

import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.dto.userShift.UserShiftDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.userShift.service.UserShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/usershift")
public class UserShiftController {
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private UserShiftService userShiftService;

    @GetMapping("/getAllShift")
    public ApiResponse<?> getAllShift() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Shift fetched successfully", this.userShiftService.getAllUserShift());
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getUserShift(@PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Shift fetched successfully", this.userShiftService.getUserShift(Long.parseLong(id)));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createUserShift(@Valid @RequestBody UserShiftDto userShiftDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.userShiftService.createUserShift(userShiftDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Shift added successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateUserShift(@PathVariable String id, @Valid @RequestBody UserShiftDto userShiftDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.userShiftService.updateUserShift(Long.parseLong(id), userShiftDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Shift updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }
    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteUserShift(@PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.userShiftService.deleteUserShift(Long.parseLong(id));
            return new ApiResponse<>(HttpStatus.OK.value(), "Shift deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }
}

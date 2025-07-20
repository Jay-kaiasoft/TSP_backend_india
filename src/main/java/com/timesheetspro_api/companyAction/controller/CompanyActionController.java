package com.timesheetspro_api.companyAction.controller;

import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyAction.service.CompanyActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/companyActions")
public class CompanyActionController {

    @Autowired
    private CompanyActionService actionService;

    @GetMapping("/getAllActions")
    public ApiResponse<?> getAllActions() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch actions details successfully", this.actionService.getAllActions());
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch actions details", resBody);
        }
    }

}

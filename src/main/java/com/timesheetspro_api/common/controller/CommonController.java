package com.timesheetspro_api.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.common.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CommonController {
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Autowired
    private CommonService commonService;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @PostMapping("/uploadFile")
    public ApiResponse<?> uploadFiles(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam String folderName,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) MultipartFile[] files, // For multiple files
            @RequestParam(required = false) MultipartFile file // For a single file
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Integer loginUserId = null;
        try {

            if (authorizationHeader != null) {
                if (userId != null) {
                    loginUserId = userId;
                } else {
                    loginUserId = Integer.parseInt(jwtUtil.extractUserId(authorizationHeader.substring(7)).toString());
                }
            }
            // Handle both single and multiple files
            if (file != null) {
                files = new MultipartFile[]{file}; // Convert single file to array for consistency
            }

            if (files == null || files.length == 0) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "No files provided", "");
            }
            Map<String, Object> resBodyObjectMap = commonService.uploadFiles(files, loginUserId, folderName);

            if (resBodyObjectMap.get("status").equals("400")) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), resBodyObjectMap.get("message").toString(), "");
            }
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Files uploaded successfully",
                    resBodyObjectMap.get("uploadedFiles")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }


    @GetMapping("/getTimezones")
    public ApiResponse<?> getTimezones() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://timeapi.io/api/timezone/availabletimezones";

            String jsonString = restTemplate.getForObject(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            List<String> timezoneList = objectMapper.readValue(jsonString, List.class);

            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Time Zones fetched successfully",
                    timezoneList
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

}


package com.timesheetspro_api.userInOut.controller;

import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.dto.UserInOut.UserInOutDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.userInOut.service.UserInOutService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inout")
public class UserInOutController {

    @Autowired
    private UserInOutService userInOutService;

    @Autowired
    private JwtTokenUtil jwtService;

    @GetMapping("/inoutreport")
    public ApiResponse<?> getReport(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestParam(value = "userIds", required = false) List<Integer> userIds,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "timeZone", required = false) String timeZone
    ) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            resBody = this.userInOutService.getTimeInOutReport(userIds, startDate, endDate, timeZone);
            return new ApiResponse<>(HttpStatus.OK.value(), "InOut's Report fetched successfully", resBody);
        } catch (Exception e) {
            e.printStackTrace();
            resBody.put("error", e.getMessage());
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to Fetch InOut's Report", resBody);
        }
    }

    @GetMapping("/generateExcelReport")
    public ResponseEntity<Resource> generateExcelReport(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestParam(value = "userIds", required = false) List<Integer> userIds,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "timeZone", required = false) String timeZone
    ) {
        try {
            // Generate the Excel file
            Workbook workbook = this.userInOutService.generateExcelReport(
                    this.userInOutService.getTimeInOutReport(userIds, startDate, endDate, timeZone), startDate, endDate, timeZone);

            // Write to ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();

            // Convert to InputStream
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            InputStreamResource resource = new InputStreamResource(in);

            // Set headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=InOutReport.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/getDashboardData/{companyId}")
    public ApiResponse<?> getDashboardData(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable int companyId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Current In-Users fetched successfully", this.userInOutService.dashboardCounts(companyId));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to get userInOut", resBody);
        }
    }

    @GetMapping("/getUserLastInOut/{userId}")
    public ApiResponse<?> getUserLastInOut(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable int userId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully", this.userInOutService.getUserLastInOut(userId));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to get userInOut", resBody);
        }
    }

    @GetMapping("/getAllRecords")
    public ApiResponse<?> getAllEntriesByUserId(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestParam(value = "userIds", required = false) List<Integer> userIds, @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "timeZone", required = false) String timeZone, @RequestParam(value = "locationIds", required = false) List<Integer> locationIds, @RequestParam(value = "departmentIds", required = false) List<Integer> departmentIds) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            String token = authorizationHeader.substring(7);
            Long userId = jwtService.extractUserId(token);
            return new ApiResponse<>(HttpStatus.OK.value(), "UserInOut fetched successfully", this.userInOutService.getAllEntriesByUserId(userIds, startDate, endDate, timeZone, locationIds, departmentIds));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to get userInOut", resBody);
        }
    }

    @GetMapping("/todayrecords")
    public ApiResponse<?> getTodayEntries(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            String token = authorizationHeader.substring(7);
            Long userId = jwtService.extractUserId(token);
            return new ApiResponse<>(HttpStatus.OK.value(), "UserInOut fetched successfully", this.userInOutService.getTodayEntriesByUserId(Integer.parseInt(userId.toString())));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to get userInOut", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getUserInOut(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "UserInOut fetched successfully", this.userInOutService.getUserInOut(Long.parseLong(id)));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to get userInOut", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createUserInOut(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(value = "locationId", required = false) String locationId
    ) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            String token = authorizationHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            Integer parsedLocationId = (locationId != null && !locationId.isBlank() && !"undefined".equals(locationId))
                    ? Integer.parseInt(locationId)
                    : null;

            return new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "UserInOut added successfully",
                    this.userInOutService.createUserInOut(userId.intValue(), parsedLocationId)
            );
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to create userInOut", resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateUserInOut(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            String token = authorizationHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            this.userInOutService.updateUserInOut(Long.parseLong(id), Integer.parseInt(userId.toString()));
            return new ApiResponse<>(HttpStatus.OK.value(), "UserInOut updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update userInOut details", resBody);
        }
    }

    @PutMapping("/update")
    public ApiResponse<?> updateInOut(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody UserInOutDto userInOutDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            String token = authorizationHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            this.userInOutService.updateUserInOut(userInOutDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "UserInOut updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update userInOut details", resBody);
        }
    }

    @PostMapping("/clockInOut")
    public ApiResponse<?> createUserInOutFromApplication(
            @RequestParam(value = "employeeId", required = false) String employeeId,
            @RequestParam(value = "locationId", required = false) String locationId
    ) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            System.out.println("=========== employeeId: " + employeeId);
            System.out.println("=========== locationId: " + locationId);
            Integer parsedLocationId = (locationId != null && !locationId.isBlank() && !"undefined".equals(locationId))
                    ? Integer.parseInt(locationId)
                    : null;

            String res = this.userInOutService.clickInOut(Integer.parseInt(employeeId), parsedLocationId);
            if (res.equals("created")) {
                return new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Clock In successfully",
                        ""
                );
            } else {
                return new ApiResponse<>(
                        HttpStatus.OK.value(),
                        "Clock Out successfully",
                        ""
                );
            }
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to create userInOut", resBody);
        }
    }
}

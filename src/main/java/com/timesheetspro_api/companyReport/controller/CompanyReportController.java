package com.timesheetspro_api.companyReport.controller;

import com.timesheetspro_api.common.dto.companyReportDto.CompanyReportDto;
import com.timesheetspro_api.common.dto.companyReportDto.CompanyReportResponse;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyReport.service.CompanyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/companyReport")
public class CompanyReportController {

    @Autowired
    private CompanyReportService companyReportService;

    @GetMapping("")
    public ApiResponse<?> getFilteredCompanies(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer min,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) String timeZone,
            @PageableDefault(size = 10, sort = "registerDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        try {
            Page<CompanyReportDto> companies = companyReportService.getCompanies(startDate, endDate, min, max, pageable, timeZone);
            CompanyReportResponse customPage = new CompanyReportResponse(companies);
            return new ApiResponse<>(HttpStatus.OK.value(), "Companies fetched successfully", customPage);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch companies", new HashMap<>());
        }
    }
}

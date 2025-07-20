package com.timesheetspro_api.companyReport.service;

import com.timesheetspro_api.common.dto.companyReportDto.CompanyReportDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyReportService {
    Page<CompanyReportDto> getCompanies(String startDate, String endDate, Integer min, Integer max, Pageable pageable, String timeZone);
}
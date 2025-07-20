package com.timesheetspro_api.companyReport.serviceImpl;

import com.timesheetspro_api.common.dto.companyReportDto.CompanyReportDto;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.common.specification.CompanySpecification;
import com.timesheetspro_api.companyReport.service.CompanyReportService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service(value = "companyReportService")
public class CompanyReportServiceImpl implements CompanyReportService {

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private CommonService commonService;

    @Override
    public Page<CompanyReportDto> getCompanies(String startDateStr, String endDateStr, Integer min, Integer max, Pageable pageable, String timeZone) {
        try {
            Specification<CompanyDetails> spec = Specification.where(null);
            Date startDate = null;
            Date endDate = null;
            if (startDateStr != null && timeZone != null) {
                startDate = this.commonService.convertLocalToUtc(startDateStr, timeZone, false);
                spec = spec.and(CompanySpecification.registerDateGreaterThanEqual(startDate));
            }
            if (endDateStr != null && timeZone != null) {
                endDate = this.commonService.convertLocalToUtc(endDateStr, timeZone, false);
                spec = spec.and(CompanySpecification.registerDateLessThanEqual(endDate));
            }
            if (min != null && max != null) {
                spec = spec.and(CompanySpecification.employeeCountBetween(min, max));
            } else if (min != null) {
                spec = spec.and(CompanySpecification.employeeCountGreaterThan(min));
            } else if (max != null) {
                spec = spec.and(CompanySpecification.employeeCountLessThan(max));
            }
            Page<CompanyDetails> result = this.companyDetailsRepository.findAll(spec, pageable);
            return result.map(company -> mapToDto(company, timeZone));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private CompanyReportDto mapToDto(CompanyDetails company, String timeZone) {
        CompanyReportDto dto = new CompanyReportDto();
        BeanUtils.copyProperties(company, dto);

        int employeeCount = (company.getCompanyEmployees() != null)
                ? company.getCompanyEmployees().size()
                : 0;

        dto.setEmployeeCount(employeeCount);

        if (timeZone != null && company.getRegisterDate() != null) {
            String dateString = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a", Locale.ENGLISH)
                    .format(company.getRegisterDate())
                    .toUpperCase();
            String formattedDate = this.commonService.convertUtcToLocal(dateString, timeZone);
            dto.setRegisterDate(formattedDate);
        }
        return dto;
    }
}
package com.timesheetspro_api.companyDetails.service;


import com.timesheetspro_api.common.dto.companyDetails.CompanyDetailsDto;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;

import java.util.List;
import java.util.Map;

public interface CompanyDetailsService {

    List<Map<String, Object>> searchCompanies(String name, int active);

    List<Map<String, Object>> getAllCompanyDetails(int active);

    CompanyDetailsDto getCompanyDetails(Integer id);

    CompanyDetailsDto createCompanyDetails(CompanyDetailsDto companyDetailsDto, String step);

    CompanyDetailsDto updateCompanyDetails(Integer id, CompanyDetailsDto companyDetailsDto, String step);

    void deleteCompanyDetails(Integer id);

    String uploadCompanyLogo(Integer companyId, String imagePath);

    boolean deleteCompanyLogo(Integer companyId);

    String getLastCompany();

}

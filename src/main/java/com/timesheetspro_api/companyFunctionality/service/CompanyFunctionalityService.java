package com.timesheetspro_api.companyFunctionality.service;

import com.timesheetspro_api.common.dto.CompanyFunctionality.CompanyFunctionalityDto;
import com.timesheetspro_api.common.model.companyFunctionality.CompanyFunctionality;

import java.util.List;

public interface CompanyFunctionalityService {

    List<CompanyFunctionality> getAllFunctionality();

    CompanyFunctionality getFunctionality(int functionalityId);

    CompanyFunctionalityDto createFunctionality(CompanyFunctionalityDto functionalityDto);

    CompanyFunctionalityDto updateFunctionality(int functionalityId, CompanyFunctionalityDto functionalityDto);

    void deleteFunctionality(int functionalityId);
}

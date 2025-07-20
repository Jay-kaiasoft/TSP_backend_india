package com.timesheetspro_api.companyModule.service;

import com.timesheetspro_api.common.dto.CompanyModuleDto.AssignCompanyActionsToCompanyModuleDto;
import com.timesheetspro_api.common.dto.CompanyModuleDto.CompanyModuleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CompanyModuleService {
    CompanyModuleDto createModule(CompanyModuleDto moduleDto);
    Map<String, Object> allModuleListPage(String searchKey, Pageable pageable);
    Map<String, Object> moduleByFunctionalityListPage(int functionalityId, String searchKey, Pageable pageable);
    Map<String, Object> getAllModules();
    CompanyModuleDto getModuleById(int moduleId);
    CompanyModuleDto updateModuleById(int moduleId, CompanyModuleDto moduleDto) throws Exception;
    void assignPolicies(AssignCompanyActionsToCompanyModuleDto assignActionsToModuleDto) throws Exception;
    void deleteModuleById(int moduleId) throws Exception;
    List<Integer> getModulePolicy(int moduleId);
}

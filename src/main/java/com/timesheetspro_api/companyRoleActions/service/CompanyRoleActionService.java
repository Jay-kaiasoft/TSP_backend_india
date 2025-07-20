package com.timesheetspro_api.companyRoleActions.service;

import com.timesheetspro_api.common.dto.companyActionsDto.CompanyActionsDto;

import java.util.List;

public interface CompanyRoleActionService {
    List<CompanyActionsDto> getCompanyActions();

    CompanyActionsDto getActions(int id);

    CompanyActionsDto createActions(CompanyActionsDto companyActionsDto);

    CompanyActionsDto updateActions(int id, CompanyActionsDto companyActionsDto);

    void deleteActions(int id);
}

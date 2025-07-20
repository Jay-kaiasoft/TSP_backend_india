package com.timesheetspro_api.companyAction.service;

import com.timesheetspro_api.common.dto.companyActionsDto.CompanyActionsDto;

import java.util.List;

public interface CompanyActionService {
    List<CompanyActionsDto> getAllActions();
}

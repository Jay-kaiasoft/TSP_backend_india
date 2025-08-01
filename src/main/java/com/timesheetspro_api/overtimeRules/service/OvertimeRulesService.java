package com.timesheetspro_api.overtimeRules.service;


import com.timesheetspro_api.common.dto.overtimeRules.OvertimeRulesDto;

import java.util.List;

public interface OvertimeRulesService {
    List<OvertimeRulesDto> getAllOvertimeRules(int companyId);

    OvertimeRulesDto getOvertimeRule(int id);

    OvertimeRulesDto createOvertimeRule(OvertimeRulesDto overtimeRulesDto,int companyId);

    OvertimeRulesDto updateOvertimeRule(int id, OvertimeRulesDto overtimeRulesDto);

    void deleteOvertimeRule(int id);
}

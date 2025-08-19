package com.timesheetspro_api.attendancePenaltyRules.service;

import com.timesheetspro_api.common.dto.attendancePenaltyRules.AttendancePenaltyRulesDto;

import java.util.List;

public interface AttendancePenaltyRulesService {
    List<AttendancePenaltyRulesDto> findAllByCompanyId(Integer companyId);

    AttendancePenaltyRulesDto findById(Integer id);

    AttendancePenaltyRulesDto create(AttendancePenaltyRulesDto attendancePenaltyRules);

    AttendancePenaltyRulesDto update(Integer id, AttendancePenaltyRulesDto attendancePenaltyRules);

    void deleteById(Integer id);
}

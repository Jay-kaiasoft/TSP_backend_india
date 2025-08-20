package com.timesheetspro_api.common.dto.attendancePenaltyRules;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class AttendancePenaltyRulesDto {
    private Integer id;
    private String ruleName;
    private Integer companyId;
    private Integer createdBy;
    private String createdByUserName;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer minutes;
    private String deductionType;
    private Integer amount;
    private Integer count;
    private Boolean isEarlyExit;
}

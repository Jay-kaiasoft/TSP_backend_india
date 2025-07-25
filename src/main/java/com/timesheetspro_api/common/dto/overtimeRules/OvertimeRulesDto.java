package com.timesheetspro_api.common.dto.overtimeRules;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class OvertimeRulesDto {
    private Integer id;
    private String ruleName;
    private Integer otMinutes;
    private Float otAmount;
    private String otType;
    private String userIds;
    private Integer companyId;
    private Timestamp startTime;
    private Timestamp endTime;
}

package com.timesheetspro_api.common.dto.weeklyOff;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class WeeklyOffDto {
    private Integer id;

    @NotBlank(message = "Template name is required")
    private String name;

    private String description;
    private Integer isDefault;

    // For brevity, include only sundayAll / mondayAll etc. but include others similarly
    private boolean sundayAll;
    private boolean sunday1st;
    private boolean sunday2nd;
    private boolean sunday3rd;
    private boolean sunday4th;
    private boolean sunday5th;

    private boolean mondayAll;
    private boolean monday1st;
    private boolean monday2nd;
    private boolean monday3rd;
    private boolean monday4th;
    private boolean monday5th;

    private boolean tuesdayAll;
    private boolean tuesday1st;
    private boolean tuesday2nd;
    private boolean tuesday3rd;
    private boolean tuesday4th;
    private boolean tuesday5th;

    private boolean wednesdayAll;
    private boolean wednesday1st;
    private boolean wednesday2nd;
    private boolean wednesday3rd;
    private boolean wednesday4th;
    private boolean wednesday5th;

    private boolean thursdayAll;
    private boolean thursday1st;
    private boolean thursday2nd;
    private boolean thursday3rd;
    private boolean thursday4th;
    private boolean thursday5th;

    private boolean fridayAll;
    private boolean friday1st;
    private boolean friday2nd;
    private boolean friday3rd;
    private boolean friday4th;
    private boolean friday5th;

    private boolean saturdayAll;
    private boolean saturday1st;
    private boolean saturday2nd;
    private boolean saturday3rd;
    private boolean saturday4th;
    private boolean saturday5th;

    private Integer companyId;
    private Integer createdBy;
    private String createdByUsername;
    private List<Integer> assignedEmployeeIds;
}

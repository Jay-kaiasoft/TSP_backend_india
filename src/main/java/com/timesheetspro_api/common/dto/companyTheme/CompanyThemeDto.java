package com.timesheetspro_api.common.dto.companyTheme;


import lombok.Data;

@Data
public class CompanyThemeDto {
    private int id;
    private int companyId;
    private String primaryColor;
    private String sideNavigationBgColor;
    private String contentBgColor;
    private String contentBgColor2;
    private String headerBgColor;
    private String textColor;
    private String iconColor;
    private String type;
}

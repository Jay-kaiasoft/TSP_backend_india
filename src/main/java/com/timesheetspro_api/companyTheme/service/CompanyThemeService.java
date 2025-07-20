package com.timesheetspro_api.companyTheme.service;

import com.timesheetspro_api.common.dto.companyTheme.CompanyThemeDto;

import java.util.List;

public interface CompanyThemeService {

    CompanyThemeDto getAllTheme(int id);

    CompanyThemeDto getTheme(int id);

    CompanyThemeDto createTheme(CompanyThemeDto companyThemeDto);

    CompanyThemeDto updateTheme(int id, CompanyThemeDto companyThemeDto);

    void deleteTheme(int id);

}

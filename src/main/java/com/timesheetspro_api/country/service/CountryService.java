package com.timesheetspro_api.country.service;

import com.timesheetspro_api.common.dto.country.CountryDto;

import java.util.List;

public interface CountryService {
    List<CountryDto> getAllCountry();
    CountryDto getCountry(int id);
}

package com.timesheetspro_api.countryToState.service;

import com.timesheetspro_api.common.dto.countryToState.CountryToStateDto;

import java.util.List;

public interface CountryToStateService {
    List<CountryToStateDto> getAllState();
    List<CountryToStateDto> getAllStateByCountry(int id);
}

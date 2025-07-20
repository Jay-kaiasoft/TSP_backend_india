package com.timesheetspro_api.locations.service;

import com.timesheetspro_api.common.dto.location.LocationDto;

import java.util.List;

public interface LocationService {
    List<LocationDto> getCompanyActiveLocations(Integer companyId);

    List<LocationDto> getAllLocationByCompany(Integer id);

    List<LocationDto> getLocations(List<Integer> ids);

    List<LocationDto> getAllLocation();

    LocationDto getLocation(Integer id);

    LocationDto createLocation(LocationDto locationDto);

    LocationDto updateLocation(Integer id, LocationDto locationDto);

    void deleteLocation(Integer id);
}

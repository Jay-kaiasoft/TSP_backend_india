package com.timesheetspro_api.locations.serviceImpl;

import com.timesheetspro_api.common.dto.location.LocationDto;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.locations.Locations;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.LocationsRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.locations.service.LocationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "locationService")
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private CommonService commonService;

    @Override
    public List<LocationDto> getCompanyActiveLocations(Integer companyId) {
        try {
            List<Locations> locationsList = this.locationsRepository.findByCompanyActiveLocations(companyId);
            List<LocationDto> locationDtos = new ArrayList<>();

            if (!locationsList.isEmpty()) {
                for (Locations locations : locationsList) {
                    locationDtos.add(this.getLocation(locations.getId()));
                }
            }
            return locationDtos;
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    @Override
    public List<LocationDto> getAllLocationByCompany(Integer id) {
        try {
            List<Locations> locationsList = this.locationsRepository.findByCompanyId(id);
            List<LocationDto> locationDtos = new ArrayList<>();

            if (!locationsList.isEmpty()) {
                for (Locations locations : locationsList) {
                    locationDtos.add(this.getLocation(locations.getId()));
                }
            }
            return locationDtos;
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    @Override
    public List<LocationDto> getAllLocation() {
        try {
            List<Locations> locationsList = this.locationsRepository.findAll();
            List<LocationDto> locationDtos = new ArrayList<>();

            if (!locationsList.isEmpty()) {
                for (Locations locations : locationsList) {
                    locationDtos.add(this.getLocation(locations.getId()));
                }
            }
            return locationDtos;
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    @Override
    public List<LocationDto> getLocations(List<Integer> ids) {
        try {
            List<Locations> locationsList = locationsRepository.findAllById(ids);

            if (locationsList.isEmpty()) {
                throw new RuntimeException("No locations found for given IDs");
            }

            List<LocationDto> locationDtos = new ArrayList<>();

            for (Locations location : locationsList) {
                LocationDto dto = new LocationDto();
                if (location.getCompanyDetails() != null) {
                    dto.setCompanyId(location.getCompanyDetails().getId());
                }
                BeanUtils.copyProperties(location, dto);
                locationDtos.add(dto);
            }

            return locationDtos;
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public LocationDto getLocation(Integer id) {
        try {
            LocationDto locationDto = new LocationDto();
            Locations locations = this.locationsRepository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
            if (locations.getCompanyDetails() != null) {
                locationDto.setCompanyId(locations.getCompanyDetails().getId());
            }
            if (locations.getPayPeriodStart() != null){
                locationDto.setPayPeriodStart(this.commonService.convertDateToString(locations.getPayPeriodStart()));
            }
            if (locations.getPayPeriodEnd() != null){
                locationDto.setPayPeriodEnd(this.commonService.convertDateToString(locations.getPayPeriodEnd()));
            }
            BeanUtils.copyProperties(locations, locationDto);
            return locationDto;
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    @Override
    public LocationDto createLocation(LocationDto locationDto) {
        try {
            Locations locations = new Locations();
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(locationDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            if (companyDetails != null) {
                locations.setCompanyDetails(companyDetails);
            }
            locations.setPayPeriod(locationDto.getPayPeriod());
            if (locationDto.getPayPeriodStart() != null){
                locations.setPayPeriodStart(this.commonService.convertStringToDate(locationDto.getPayPeriodStart()));
            }
            if (locationDto.getPayPeriodEnd() != null){
                locations.setPayPeriodEnd(this.commonService.convertStringToDate(locationDto.getPayPeriodEnd()));
            }
            BeanUtils.copyProperties(locationDto, locations);
            //isNotEmpty(locationDto.getTimeZone()) &&
            if (
                    isNotEmpty(locationDto.getLocationName()) &&
                    isNotEmpty(locationDto.getCity()) &&
                    isNotEmpty(locationDto.getCountry()) &&
                    isNotEmpty(locationDto.getState()) &&
                    isNotEmpty(locationDto.getAddress1()) &&
                    isNotEmpty(locationDto.getZipCode()) &&
                    isNotEmpty(locationDto.getGeofenceId()) &&
                    isNotEmpty(locationDto.getExternalId())) {
                locations.setIsActive(1);
            } else {
                locations.setIsActive(0);
            }

            this.locationsRepository.save(locations);
            return locationDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LocationDto updateLocation(Integer id, LocationDto locationDto) {
        try {
            Locations locations = this.locationsRepository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(locationDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            if (companyDetails != null) {
                locations.setCompanyDetails(companyDetails);
            }
//            locations.setTimeZone(locationDto.getTimeZone());
            locations.setCity(locationDto.getCity());
            locations.setState(locationDto.getState());
            locations.setCountry(locationDto.getCountry());
            locations.setAddress1(locationDto.getAddress1());
            locations.setAddress2(locationDto.getAddress2());
            locations.setEmployeeCount(locationDto.getEmployeeCount());
            locations.setZipCode(locationDto.getZipCode());
            locations.setLocationName(locationDto.getLocationName());
            locations.setGeofenceId(locationDto.getGeofenceId());
            locations.setExternalId(locationDto.getExternalId());
            locations.setPayPeriod(locationDto.getPayPeriod());
            if (locationDto.getPayPeriodStart() != null){
                locations.setPayPeriodStart(this.commonService.convertStringToDate(locationDto.getPayPeriodStart()));
            }
            if (locationDto.getPayPeriodEnd() != null){
                locations.setPayPeriodEnd(this.commonService.convertStringToDate(locationDto.getPayPeriodEnd()));
            }
            //isNotEmpty(locationDto.getTimeZone()) &&
            if (
                    isNotEmpty(locationDto.getLocationName()) &&
                    isNotEmpty(locationDto.getCity()) &&
                    isNotEmpty(locationDto.getCountry()) &&
                    isNotEmpty(locationDto.getState()) &&
                    isNotEmpty(locationDto.getAddress1()) &&
                    isNotEmpty(locationDto.getZipCode()) &&
                    isNotEmpty(locationDto.getGeofenceId()) &&
                    isNotEmpty(locationDto.getExternalId())) {
                locations.setIsActive(1);
            } else {
                locations.setIsActive(0);
            }
            this.locationsRepository.save(locations);
            return locationDto;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteLocation(Integer id) {
        try {
            Locations locations = this.locationsRepository.findById(id).orElseThrow(() -> new RuntimeException("Location not found"));
            this.locationsRepository.delete(locations);
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

}

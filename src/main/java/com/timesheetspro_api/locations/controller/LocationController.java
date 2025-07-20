package com.timesheetspro_api.locations.controller;

import com.timesheetspro_api.common.dto.location.LocationDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.locations.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/location")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @GetMapping("/getActiveLocations/{id}")
    public ApiResponse<?> getCompanyActiveLocations(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<LocationDto> locationsList = this.locationService.getCompanyActiveLocations(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch locations details successfully", locationsList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch locations details", resBody);
        }
    }

    @GetMapping("/getAllLocationByCompany/{id}")
    public ApiResponse<?> getAllLocationByCompany(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<LocationDto> locationsList = this.locationService.getAllLocationByCompany(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch locations details successfully", locationsList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch locations details", resBody);
        }
    }

    @GetMapping("/get/all")
    public ApiResponse<?> getAllLocation(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<LocationDto> locationsList = this.locationService.getAllLocation();
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch locations details successfully", locationsList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch locations details", resBody);
        }
    }

    @GetMapping("/getLocations")
    public ApiResponse<?> getLocations(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam List<Integer> locationIds) {

        Map<String, Object> resBody = new HashMap<>();
        try {
            List<LocationDto> locationsList = this.locationService.getLocations(locationIds);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetched location details successfully", locationsList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch location details", resBody);
        }
    }


    @GetMapping("/get/{id}")
    public ApiResponse<?> getLocation(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            LocationDto locationsList = this.locationService.getLocation(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch locations details successfully", locationsList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch locations details", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createLocation(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @Valid @RequestBody LocationDto locationDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            LocationDto locationsList = this.locationService.createLocation(locationDto);
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Locations details added successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to add locations details", resBody);
        }
    }

    @PatchMapping("/update/{id}")
    public ApiResponse<?> updateLocation(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id, @Valid @RequestBody LocationDto locationDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            LocationDto locationsList = this.locationService.updateLocation(id, locationDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "Locations details updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to updated locations details", resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteLocation(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.locationService.deleteLocation(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Locations details deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete locations details", resBody);
        }
    }
}

package com.timesheetspro_api.country.controller;

import com.timesheetspro_api.common.dto.country.CountryDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/country")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping("/get/all")
    public ApiResponse<?> getAllCountry() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<CountryDto> countryDtoList = this.countryService.getAllCountry();
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch country details successfully", countryDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch country details", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getCountry(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CountryDto countryDtoList = this.countryService.getCountry(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch country details successfully", countryDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch country details", resBody);
        }
    }
}

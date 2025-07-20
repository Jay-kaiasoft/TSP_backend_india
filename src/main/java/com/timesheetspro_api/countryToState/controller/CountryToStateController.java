package com.timesheetspro_api.countryToState.controller;

import com.timesheetspro_api.common.dto.countryToState.CountryToStateDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.countryToState.service.CountryToStateService;
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
@RequestMapping("/state")
public class CountryToStateController {

    @Autowired
    private CountryToStateService countryToStateService;

    @GetMapping("/get/all")
    public ApiResponse<?> getAllState() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<CountryToStateDto> countryDtoList = this.countryToStateService.getAllState();
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch state details successfully", countryDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch state details", resBody);
        }
    }

    @GetMapping("/getAllStateByCountry/{id}")
    public ApiResponse<?> getAllStateByCountry(@PathVariable int id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<CountryToStateDto> countryDtoList = this.countryToStateService.getAllStateByCountry(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch state details successfully", countryDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch state details", resBody);
        }
    }
}

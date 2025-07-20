package com.timesheetspro_api.countryToState.serviceImpl;

import com.timesheetspro_api.common.dto.countryToState.CountryToStateDto;
import com.timesheetspro_api.common.model.countryToState.CountryToState;
import com.timesheetspro_api.common.repository.CountryToStateRepository;
import com.timesheetspro_api.countryToState.service.CountryToStateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "countryToStateService")
public class CountryToStateServiceImpl implements CountryToStateService {
    @Autowired
    private CountryToStateRepository countryToStateRepository;

    @Override
    public List<CountryToStateDto> getAllState() {
        try {
            try {
                List<CountryToStateDto> countryToStateDtoList = new ArrayList<>();
                List<CountryToState> countryToStateList = this.countryToStateRepository.findAll();
                for (CountryToState countryToState : countryToStateList) {
                    CountryToStateDto countryToStateDto = new CountryToStateDto();
                    CountryToState countryToState1 = this.countryToStateRepository.findById(countryToState.getId()).orElseThrow(() -> new RuntimeException("State not found"));
                    countryToStateDto.setCountryId(countryToState1.getCountry().getId());
                    BeanUtils.copyProperties(countryToState1, countryToStateDto);
                    countryToStateDtoList.add(countryToStateDto);
                }
                return countryToStateDtoList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CountryToStateDto> getAllStateByCountry(int id) {
        try {
            List<CountryToStateDto> countryToStateDtoList = new ArrayList<>();
            List<CountryToState> countryToStateList = this.countryToStateRepository.findByCountryId(id);
            if (countryToStateList != null) {
                for (CountryToState countryToState : countryToStateList) {
                    CountryToStateDto countryToStateDto = new CountryToStateDto();
                    CountryToState countryToState1 = this.countryToStateRepository.findById(countryToState.getId()).orElseThrow(() -> new RuntimeException("State not found"));
                    countryToStateDto.setCountryId(countryToState1.getCountry().getId());
                    BeanUtils.copyProperties(countryToState1, countryToStateDto);
                    countryToStateDtoList.add(countryToStateDto);
                }
            }
            return countryToStateDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

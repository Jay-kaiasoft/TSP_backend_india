package com.timesheetspro_api.holidayTemplateDetails.serviceImpl;

import com.timesheetspro_api.common.dto.holidayTemplateDetails.HolidayTemplateDetailsDto;
import com.timesheetspro_api.common.model.holidayTemplateDetails.HolidayTemplateDetails;
import com.timesheetspro_api.common.model.holidayTemplates.HolidayTemplates;
import com.timesheetspro_api.common.repository.company.HolidayTemplateDetailsRepository;
import com.timesheetspro_api.common.repository.company.HolidayTemplatesRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.holidayTemplateDetails.service.HolidayTemplateDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "HolidayTemplateDetailsService")
public class HolidayTemplateDetailsServiceImpl implements HolidayTemplateDetailsService {

    @Autowired
    private HolidayTemplatesRepository holidayTemplatesRepository;

    @Autowired
    private HolidayTemplateDetailsRepository holidayTemplateDetailsRepository;

    @Autowired
    private CommonService commonService;

    @Override
    public List<HolidayTemplateDetailsDto> getAllHolidayTemplateDetailsByTemplateId(Integer id) {
        try {
            List<HolidayTemplateDetails> holidayTemplateDetailsList = this.holidayTemplateDetailsRepository.findByTemplatesId(id);
            List<HolidayTemplateDetailsDto> holidayTemplateDetailsDtoList = new ArrayList<>();
            if (!holidayTemplateDetailsList.isEmpty()) {
                for (HolidayTemplateDetails holidayTemplateDetails : holidayTemplateDetailsList) {
                    holidayTemplateDetailsDtoList.add(this.getHolidayTemplateDetailsById(holidayTemplateDetails.getId()));
                }
            }
            return holidayTemplateDetailsDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HolidayTemplateDetailsDto getHolidayTemplateDetailsById(Integer id) {
        try {
            HolidayTemplateDetails holidayTemplateDetails = this.holidayTemplateDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday Template Details not found"));
            HolidayTemplateDetailsDto holidayTemplateDetailsDto = new HolidayTemplateDetailsDto();
            holidayTemplateDetailsDto.setId(holidayTemplateDetails.getId());
            holidayTemplateDetailsDto.setName(holidayTemplateDetails.getName());
            holidayTemplateDetailsDto.setHolidayTemplateId(holidayTemplateDetails.getHolidayTemplates().getId());
            if (holidayTemplateDetails.getDate() != null) {
                holidayTemplateDetailsDto.setDate(this.commonService.convertDateToString(holidayTemplateDetails.getDate()));
            }
            return holidayTemplateDetailsDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public HolidayTemplateDetailsDto createHolidayTemplateDetails(HolidayTemplateDetailsDto holidayTemplateDetailsDto) {
        try {
            HolidayTemplateDetails holidayTemplateDetails = new HolidayTemplateDetails();
            HolidayTemplates holidayTemplates = this.holidayTemplatesRepository.findById(holidayTemplateDetailsDto.getHolidayTemplateId())
                    .orElseThrow(() -> new RuntimeException("Holiday Template not found"));
            holidayTemplateDetails.setHolidayTemplates(holidayTemplates);
            holidayTemplateDetails.setName(holidayTemplateDetailsDto.getName());
            if (holidayTemplateDetailsDto.getDate() != null) {
                holidayTemplateDetails.setDate(this.commonService.convertStringToDate(holidayTemplateDetailsDto.getDate()));
            }
            this.holidayTemplateDetailsRepository.save(holidayTemplateDetails);
            return holidayTemplateDetailsDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public HolidayTemplateDetailsDto updateHolidayTemplateDetails(Integer id, HolidayTemplateDetailsDto holidayTemplateDetailsDto) {
        try {
            HolidayTemplateDetails holidayTemplateDetails = this.holidayTemplateDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday Template Details not found"));
            HolidayTemplates holidayTemplates = this.holidayTemplatesRepository.findById(holidayTemplateDetailsDto.getHolidayTemplateId())
                    .orElseThrow(() -> new RuntimeException("Holiday Template not found"));
            holidayTemplateDetails.setHolidayTemplates(holidayTemplates);
            holidayTemplateDetails.setName(holidayTemplateDetailsDto.getName());
            if (holidayTemplateDetailsDto.getDate() != null) {
                holidayTemplateDetails.setDate(this.commonService.convertStringToDate(holidayTemplateDetailsDto.getDate()));
            }
            this.holidayTemplateDetailsRepository.save(holidayTemplateDetails);
            return holidayTemplateDetailsDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteHolidayTemplateDetails(Integer id) {
        try {
            HolidayTemplateDetails holidayTemplateDetails = this.holidayTemplateDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday Template Details not found"));
            this.holidayTemplateDetailsRepository.delete(holidayTemplateDetails);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

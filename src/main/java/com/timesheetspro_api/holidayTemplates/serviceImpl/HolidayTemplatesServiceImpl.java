package com.timesheetspro_api.holidayTemplates.serviceImpl;

import com.timesheetspro_api.common.dto.holidayTemplateDetails.HolidayTemplateDetailsDto;
import com.timesheetspro_api.common.dto.holidayTemplates.HolidayTemplatesDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.holidayTemplates.HolidayTemplates;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.HolidayTemplatesRepository;
import com.timesheetspro_api.holidayTemplateDetails.service.HolidayTemplateDetailsService;
import com.timesheetspro_api.holidayTemplates.service.HolidayTemplatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "HolidayTemplatesService")
public class HolidayTemplatesServiceImpl implements HolidayTemplatesService {

    @Autowired
    private HolidayTemplatesRepository holidayTemplatesRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private HolidayTemplateDetailsService holidayTemplateDetailsService;

    @Override
    public List<HolidayTemplatesDto> getAllHolidayTemplatesByCompanyId(Integer id) {
        try {
            List<HolidayTemplates> holidayTemplatesList = this.holidayTemplatesRepository.findByCompanyId(id);
            List<HolidayTemplatesDto> holidayTemplatesDtoList = new ArrayList<>();
            if (!holidayTemplatesList.isEmpty()) {
                for (HolidayTemplates holidayTemplates : holidayTemplatesList) {
                    holidayTemplatesDtoList.add(this.getHolidayTemplateById(holidayTemplates.getId()));
                }
            }
            return holidayTemplatesDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HolidayTemplatesDto getHolidayTemplateById(Integer id) {
        try {
            HolidayTemplates holidayTemplates = this.holidayTemplatesRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday Template not found"));
            HolidayTemplatesDto holidayTemplatesDto = new HolidayTemplatesDto();
            holidayTemplatesDto.setId(holidayTemplates.getId());
            holidayTemplatesDto.setName(holidayTemplates.getName());
            holidayTemplatesDto.setCompanyId(holidayTemplates.getCompanyDetails().getId());
            holidayTemplatesDto.setCreatedBy(holidayTemplates.getCompanyEmployee().getEmployeeId());
            holidayTemplatesDto.setCreatedByUserName(holidayTemplates.getCompanyEmployee().getUsername());
            List<HolidayTemplateDetailsDto> holidayTemplateDetailsDtoList = this.holidayTemplateDetailsService.getAllHolidayTemplateDetailsByTemplateId(id);
            if (!holidayTemplateDetailsDtoList.isEmpty()) {
                holidayTemplatesDto.setHolidayTemplateDetailsList(holidayTemplateDetailsDtoList);
            }
            return holidayTemplatesDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public HolidayTemplatesDto createHolidayTemplate(HolidayTemplatesDto holidayTemplatesDto) {
        try {
            HolidayTemplates holidayTemplates = new HolidayTemplates();
            CompanyDetails companyDetails = companyDetailsRepository.findById(holidayTemplatesDto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            holidayTemplates.setCompanyDetails(companyDetails);
            CompanyEmployee companyEmployee = companyEmployeeRepository.findById(holidayTemplatesDto.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("Company Employee not found"));
            holidayTemplates.setCompanyEmployee(companyEmployee);
            holidayTemplates.setName(holidayTemplatesDto.getName());
            this.holidayTemplatesRepository.save(holidayTemplates);
            return holidayTemplatesDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public HolidayTemplatesDto updateHolidayTemplate(Integer id, HolidayTemplatesDto holidayTemplatesDto) {
        try {
            HolidayTemplates holidayTemplates = this.holidayTemplatesRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday Template not found"));
            CompanyDetails companyDetails = companyDetailsRepository.findById(holidayTemplatesDto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            holidayTemplates.setCompanyDetails(companyDetails);
            CompanyEmployee companyEmployee = companyEmployeeRepository.findById(holidayTemplatesDto.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("Company Employee not found"));
            holidayTemplates.setCompanyEmployee(companyEmployee);
            holidayTemplates.setName(holidayTemplatesDto.getName());
            this.holidayTemplatesRepository.save(holidayTemplates);
            return holidayTemplatesDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteHolidayTemplate(Integer id) {
        try {
            HolidayTemplates holidayTemplates = this.holidayTemplatesRepository.findById(id).orElseThrow(() -> new RuntimeException("Holiday Template not found"));
            this.holidayTemplatesRepository.delete(holidayTemplates);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

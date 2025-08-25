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
            holidayTemplatesDto.setAssignedEmployeeIds(this.getAssignEmployees(id));
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
            HolidayTemplates newHolidayTemplates = this.holidayTemplatesRepository.save(holidayTemplates);
            if (newHolidayTemplates.getId() != null) {
                if (!holidayTemplatesDto.getHolidayTemplateDetailsList().isEmpty()) {
                    for (HolidayTemplateDetailsDto holidayTemplateDetailsDto : holidayTemplatesDto.getHolidayTemplateDetailsList()) {
                        holidayTemplateDetailsDto.setHolidayTemplateId(newHolidayTemplates.getId());
                        this.holidayTemplateDetailsService.createHolidayTemplateDetails(holidayTemplateDetailsDto);
                    }
                } else {
                    throw new RuntimeException("Holiday list is required");
                }
            }
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
            if (!holidayTemplatesDto.getHolidayTemplateDetailsList().isEmpty()) {
                for (HolidayTemplateDetailsDto holidayTemplateDetailsDto : holidayTemplatesDto.getHolidayTemplateDetailsList()) {
                    if (holidayTemplateDetailsDto.getId() != null) {
                        holidayTemplateDetailsDto.setHolidayTemplateId(holidayTemplatesDto.getId());
                        this.holidayTemplateDetailsService.updateHolidayTemplateDetails(holidayTemplateDetailsDto.getId(), holidayTemplateDetailsDto);
                    } else {
                        holidayTemplateDetailsDto.setHolidayTemplateId(id);
                        this.holidayTemplateDetailsService.createHolidayTemplateDetails(holidayTemplateDetailsDto);
                    }
                }
            } else {
                throw new RuntimeException("Holiday list is required");
            }
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

    @Override
    public boolean assignEmployees(Integer templateId, List<Integer> employeeIds, List<Integer> removeEmployeeIds) {
        try {
            HolidayTemplates holidayTemplates = this.holidayTemplatesRepository.findById(templateId).orElseThrow(() -> new RuntimeException("Holiday Template not found"));
            for (Integer id : employeeIds) {
                CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Company Employee not found"));
                companyEmployee.setHolidayTemplates(holidayTemplates);
                this.companyEmployeeRepository.save(companyEmployee);
            }
            if (!removeEmployeeIds.isEmpty()) {
                for (Integer id : removeEmployeeIds) {
                    CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Company Employee not found"));
                    companyEmployee.setHolidayTemplates(null);
                    this.companyEmployeeRepository.save(companyEmployee);
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Integer> getAssignEmployees(Integer templateId) {
        try {
            List<CompanyEmployee> companyEmployeeList = this.companyEmployeeRepository.findByHolidayTemplateId(templateId);
            List<Integer> employeeIds = new ArrayList<>();
            if (!companyEmployeeList.isEmpty()) {
                for (CompanyEmployee companyEmployee : companyEmployeeList) {
                    employeeIds.add(companyEmployee.getEmployeeId());
                }
            }
            return employeeIds;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

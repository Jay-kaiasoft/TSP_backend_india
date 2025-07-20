package com.timesheetspro_api.companyShift.serviceImpl;

import com.timesheetspro_api.common.dto.companyShiftDto.CompanyShiftDto;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyShift.CompanyShift;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyShiftRepository;
import com.timesheetspro_api.companyShift.service.CompanyShiftService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyShiftServiceImpl implements CompanyShiftService {

    @Autowired
    private CompanyShiftRepository shiftRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Override
    public CompanyShiftDto createShift(CompanyShiftDto dto) {
        try {
            CompanyShift companyShift = new CompanyShift();
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(dto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            companyShift.setCompanyDetails(companyDetails);
            BeanUtils.copyProperties(dto, companyShift);
            this.shiftRepository.save(companyShift);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyShiftDto updateShift(int id, CompanyShiftDto dto) {
        try {
            CompanyShift companyShift = this.shiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(dto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            companyShift.setCompanyDetails(companyDetails);
            BeanUtils.copyProperties(dto, companyShift, "id");
            this.shiftRepository.save(companyShift);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteShift(int id) {
        try {
            CompanyShift companyShift = this.shiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
            this.shiftRepository.delete(companyShift);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyShiftDto getShiftById(int id) {
        try {
            CompanyShift companyShift = this.shiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
            CompanyShiftDto companyShiftDto = new CompanyShiftDto();
            companyShiftDto.setCompanyId(companyShift.getCompanyDetails().getId());
            BeanUtils.copyProperties(companyShift, companyShiftDto);
            return companyShiftDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CompanyShiftDto> getAllShifts(int companyId) {
        try {
            List<CompanyShift> companyShiftList = this.shiftRepository.findByCompanyId(companyId);
            List<CompanyShiftDto> companyShiftDtoList = new ArrayList<>();
            if (!companyShiftList.isEmpty()) {
                for (CompanyShift companyShift:companyShiftList){
                    companyShiftDtoList.add(this.getShiftById(companyShift.getId()));
                }
            }
            return companyShiftDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
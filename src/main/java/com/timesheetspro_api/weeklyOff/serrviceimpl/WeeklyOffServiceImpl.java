package com.timesheetspro_api.weeklyOff.serrviceimpl;

import com.timesheetspro_api.common.dto.weeklyOff.WeeklyOffDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.weeklyOff.WeeklyOff;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.WeeklyOffRepository;
import com.timesheetspro_api.weeklyOff.serrvice.WeeklyOffService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service(value = "weeklyOffService")
public class WeeklyOffServiceImpl implements WeeklyOffService {

    @Autowired
    private WeeklyOffRepository repository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Override
    public boolean assignEmployees(List<Integer> employeeIds, Integer weeklyOffId) {
        try {
            if (!employeeIds.isEmpty()){
                WeeklyOff weeklyOff = this.repository.findById(weeklyOffId)
                        .orElseThrow(() -> new IllegalArgumentException("Weekly off not found"));
                for (Integer employeeId : employeeIds) {
                    CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(employeeId)
                            .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + employeeId));
                    companyEmployee.setWeeklyOff(weeklyOff);
                    this.companyEmployeeRepository.save(companyEmployee);
                }
                return true;
            } else {
                throw new IllegalArgumentException("Employee ID cannot be null or empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WeeklyOffDto> getAllByCompany(Integer companyId) {
        try {
            List<WeeklyOff> weeklyOffs = this.repository.findByCompany(companyId);
            List<WeeklyOffDto> weeklyOffDtos = new java.util.ArrayList<>();
            for (WeeklyOff weeklyOff : weeklyOffs) {
                weeklyOffDtos.add(this.getById(weeklyOff.getId()));
            }
            return weeklyOffDtos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WeeklyOffDto getById(Integer id) {
        try {
            WeeklyOffDto dto = new WeeklyOffDto();
            WeeklyOff weeklyOff = this.repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Weekly off not found"));
            BeanUtils.copyProperties(weeklyOff, dto);
            dto.setCompanyId(weeklyOff.getCompanyDetails().getId());
            dto.setCreatedBy(weeklyOff.getCompanyEmployee().getEmployeeId());
            dto.setCreatedByUsername(weeklyOff.getCompanyEmployee().getUsername());
            BeanUtils.copyProperties(weeklyOff, dto);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WeeklyOffDto create(WeeklyOffDto dto) {
        try {
            if (!hasAnyFlag(dto)) {
                throw new IllegalArgumentException("At least one weekly off must be selected");
            }
            WeeklyOff isExites= this.repository.existsByName(dto.getName());
            if (isExites != null) {
                throw new IllegalArgumentException("Template name already exists");
            }
            WeeklyOff weeklyOff = new WeeklyOff();
            weeklyOff.setCompanyDetails(companyDetailsRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found")));
            weeklyOff.setCompanyEmployee(companyEmployeeRepository.findById(dto.getCreatedBy())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found")));

            BeanUtils.copyProperties(dto, weeklyOff,"id", "companyDetails", "companyEmployee");
            this.repository.save(weeklyOff);
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public WeeklyOffDto update(Integer id, WeeklyOffDto dto) {
        try {
            if (!hasAnyFlag(dto)) {
                throw new IllegalArgumentException("At least one weekly off must be selected");
            }
            WeeklyOff weeklyOff = this.repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Weekly off not found"));
            weeklyOff.setCompanyDetails(companyDetailsRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new IllegalArgumentException("Company not found")));
            weeklyOff.setCompanyEmployee(companyEmployeeRepository.findById(dto.getCreatedBy())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found")));
            BeanUtils.copyProperties(dto, weeklyOff,"id");
            this.repository.save(weeklyOff);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            WeeklyOff weeklyOff = this.repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Weekly off not found"));
            this.repository.delete(weeklyOff);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Helper mapping methods
    private boolean hasAnyFlag(WeeklyOffDto dto) {
        // check all boolean flags
        return dto.isSundayAll() || dto.isSunday1st() || dto.isSunday2nd() || dto.isSunday3rd() || dto.isSunday4th() || dto.isSunday5th()
                || dto.isMondayAll() || dto.isMonday1st() || dto.isMonday2nd() || dto.isMonday3rd() || dto.isMonday4th() || dto.isMonday5th()
                || dto.isTuesdayAll() || dto.isTuesday1st() || dto.isTuesday2nd() || dto.isTuesday3rd() || dto.isTuesday4th() || dto.isTuesday5th()
                || dto.isWednesdayAll() || dto.isWednesday1st() || dto.isWednesday2nd() || dto.isWednesday3rd() || dto.isWednesday4th() || dto.isWednesday5th()
                || dto.isThursdayAll() || dto.isThursday1st() || dto.isThursday2nd() || dto.isThursday3rd() || dto.isThursday4th() || dto.isThursday5th()
                || dto.isFridayAll() || dto.isFriday1st() || dto.isFriday2nd() || dto.isFriday3rd() || dto.isFriday4th() || dto.isFriday5th()
                || dto.isSaturdayAll() || dto.isSaturday1st() || dto.isSaturday2nd() || dto.isSaturday3rd() || dto.isSaturday4th() || dto.isSaturday5th();
    }
}

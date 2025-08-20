package com.timesheetspro_api.attendancePenaltyRules.serviceImpl;

import com.timesheetspro_api.attendancePenaltyRules.service.AttendancePenaltyRulesService;
import com.timesheetspro_api.common.dto.attendancePenaltyRules.AttendancePenaltyRulesDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.attendancePenaltyRules.AttendancePenaltyRules;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.repository.company.AttendancePenaltyRulesRepository;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "attendancePenaltyRulesService")
public class AttendancePenaltyRulesServiceImpl implements AttendancePenaltyRulesService {

    @Autowired
    private AttendancePenaltyRulesRepository attendancePenaltyRulesRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Override
    public List<AttendancePenaltyRulesDto> findAllByCompanyId(Integer flag, Integer companyId) {
        try {
            List<AttendancePenaltyRules> attendancePenaltyRulesList = this.attendancePenaltyRulesRepository.findByCompanyId(companyId, flag == 1);
            List<AttendancePenaltyRulesDto> attendancePenaltyRulesDtoList = new ArrayList<>();

            if (!attendancePenaltyRulesList.isEmpty()) {
                for (AttendancePenaltyRules attendancePenaltyRules : attendancePenaltyRulesList) {
                    attendancePenaltyRulesDtoList.add(this.findById(attendancePenaltyRules.getId()));
                }
            }
            return attendancePenaltyRulesDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public AttendancePenaltyRulesDto findById(Integer id) {
        try {
            AttendancePenaltyRules attendancePenaltyRules = this.attendancePenaltyRulesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Attendance penalty rule not found"));
            AttendancePenaltyRulesDto attendancePenaltyRulesDto = new AttendancePenaltyRulesDto();
            attendancePenaltyRulesDto.setCompanyId(attendancePenaltyRules.getCompanyDetails().getId());
            attendancePenaltyRulesDto.setCreatedBy(attendancePenaltyRules.getCompanyEmployee().getEmployeeId());
            attendancePenaltyRulesDto.setCreatedByUserName(attendancePenaltyRules.getCompanyEmployee().getUsername());
            BeanUtils.copyProperties(attendancePenaltyRules, attendancePenaltyRulesDto);
            return attendancePenaltyRulesDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public AttendancePenaltyRulesDto create(AttendancePenaltyRulesDto attendancePenaltyRulesDto) {
        try {
            AttendancePenaltyRules existingRule = attendancePenaltyRulesRepository
                    .findByCompanyIdAndName(attendancePenaltyRulesDto.getRuleName(), attendancePenaltyRulesDto.getCompanyId(),attendancePenaltyRulesDto.getIsEarlyExit());
            AttendancePenaltyRules existingRuleWithMinutes = attendancePenaltyRulesRepository
                    .findByCompanyIdAndMinutes(attendancePenaltyRulesDto.getMinutes(), attendancePenaltyRulesDto.getCompanyId(),attendancePenaltyRulesDto.getIsEarlyExit());
            if (existingRule != null) {
                throw new RuntimeException("Penalty rule already exists with name " + attendancePenaltyRulesDto.getRuleName());
            }
            if (existingRuleWithMinutes != null) {
                throw new RuntimeException("Penalty rule already exists for " + attendancePenaltyRulesDto.getMinutes() + " minutes");
            }
            AttendancePenaltyRules attendancePenaltyRules = new AttendancePenaltyRules();
            CompanyDetails companyDetails = companyDetailsRepository.findById(attendancePenaltyRulesDto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            attendancePenaltyRules.setCompanyDetails(companyDetails);
            CompanyEmployee companyEmployee = companyEmployeeRepository.findById(attendancePenaltyRulesDto.getCreatedBy())
                    .orElseThrow(() -> new RuntimeException("Company employee not found"));
            attendancePenaltyRules.setCompanyEmployee(companyEmployee);
            BeanUtils.copyProperties(attendancePenaltyRulesDto, attendancePenaltyRules, "id");
            this.attendancePenaltyRulesRepository.save(attendancePenaltyRules);
            return attendancePenaltyRulesDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public AttendancePenaltyRulesDto update(Integer id, AttendancePenaltyRulesDto attendancePenaltyRulesDto) {
        try {
            AttendancePenaltyRules existingRule = attendancePenaltyRulesRepository
                    .findAllExceptByCompanyId(attendancePenaltyRulesDto.getRuleName(), id, attendancePenaltyRulesDto.getCompanyId(),attendancePenaltyRulesDto.getIsEarlyExit());

            AttendancePenaltyRules existingRuleWithMinutes = attendancePenaltyRulesRepository
                    .findAllExceptByCompanyIdWithMinutes(attendancePenaltyRulesDto.getMinutes(), id, attendancePenaltyRulesDto.getCompanyId(),attendancePenaltyRulesDto.getIsEarlyExit());

            if (existingRule != null) {
                throw new RuntimeException("Penalty rule already exists with name " + attendancePenaltyRulesDto.getRuleName());
            }
            if (existingRuleWithMinutes != null) {
                throw new RuntimeException("Penalty rule already exists for " + attendancePenaltyRulesDto.getMinutes() + " minutes");
            }
            AttendancePenaltyRules attendancePenaltyRules = this.attendancePenaltyRulesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Penalty rule not found"));
            ;
            CompanyDetails companyDetails = companyDetailsRepository.findById(attendancePenaltyRulesDto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            attendancePenaltyRules.setCompanyDetails(companyDetails);

            BeanUtils.copyProperties(attendancePenaltyRulesDto, attendancePenaltyRules, "id");
            this.attendancePenaltyRulesRepository.save(attendancePenaltyRules);
            return attendancePenaltyRulesDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try {
            AttendancePenaltyRules attendancePenaltyRules = this.attendancePenaltyRulesRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Penalty rule not found"));
            this.attendancePenaltyRulesRepository.delete(attendancePenaltyRules);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

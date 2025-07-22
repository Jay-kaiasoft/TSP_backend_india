package com.timesheetspro_api.overtimeRules.serviceImpl;

import com.timesheetspro_api.common.dto.overtimeRules.OvertimeRulesDto;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.overtimeRules.OvertimeRules;
import com.timesheetspro_api.common.repository.OvertimeRulesRepository;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.overtimeRules.service.OvertimeRulesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "OvertimeRulesService")
public class OvertimeRulesServiceImpl implements OvertimeRulesService {
    @Autowired
    private OvertimeRulesRepository overtimeRulesRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Override
    public List<OvertimeRulesDto> getAllOvertimeRules(int companyId) {
        try {
            List<OvertimeRules> overtimeRulesList = this.overtimeRulesRepository.findByCompanyId(companyId);
            List<OvertimeRulesDto> overtimeRulesDtoList = new java.util.ArrayList<>();
            for (OvertimeRules overtimeRules : overtimeRulesList) {
                overtimeRulesDtoList.add(this.getOvertimeRule(overtimeRules.getId()));
            }
            return overtimeRulesDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OvertimeRulesDto getOvertimeRule(int id) {
        try {
            OvertimeRules overtimeRules = this.overtimeRulesRepository.findById(id).orElseThrow(() -> new RuntimeException("Overtime rule not found with id: " + id));
            OvertimeRulesDto overtimeRulesDto = new OvertimeRulesDto();
            overtimeRulesDto.setCompanyId(overtimeRules.getCompanyDetails().getId());
            BeanUtils.copyProperties(overtimeRules, overtimeRulesDto);
            return overtimeRulesDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OvertimeRulesDto createOvertimeRule(OvertimeRulesDto overtimeRulesDto, int companyId) {
        try {
            OvertimeRules isExites = this.overtimeRulesRepository.findByRuleName(overtimeRulesDto.getRuleName());
            if (isExites != null) {
                throw new RuntimeException("Overtime rule with name '" + overtimeRulesDto.getRuleName() + "' already exists.");
            }
            OvertimeRules overtimeRules = new OvertimeRules();
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
            overtimeRules.setCompanyDetails(companyDetails);
            overtimeRules.setRuleName(overtimeRulesDto.getRuleName());
            overtimeRules.setOtMinutes(overtimeRulesDto.getOtMinutes());
            overtimeRules.setOtAmount(overtimeRulesDto.getOtAmount());
            overtimeRules.setOtType(overtimeRulesDto.getOtType());
            overtimeRules.setStartTime(overtimeRulesDto.getStartTime());
            overtimeRules.setEndTime(overtimeRulesDto.getEndTime());
            OvertimeRules savedOvertimeRules = this.overtimeRulesRepository.save(overtimeRules);
            OvertimeRulesDto savedOvertimeRulesDto = new OvertimeRulesDto();
            BeanUtils.copyProperties(savedOvertimeRules, savedOvertimeRulesDto);
            savedOvertimeRulesDto.setCompanyId(companyDetails.getId());
            return savedOvertimeRulesDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public OvertimeRulesDto updateOvertimeRule(int id, OvertimeRulesDto overtimeRulesDto) {
        try {
            OvertimeRules isExites = this.overtimeRulesRepository.findByRuleName(id, overtimeRulesDto.getRuleName());
            if (isExites != null) {
                throw new RuntimeException("Overtime rule with name '" + overtimeRulesDto.getRuleName() + "' already exists.");
            }
            OvertimeRules overtimeRules = this.overtimeRulesRepository.findById(id).orElseThrow(() -> new RuntimeException("Overtime rule not found"));
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(overtimeRulesDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            overtimeRules.setCompanyDetails(companyDetails);
            overtimeRules.setRuleName(overtimeRulesDto.getRuleName());
            overtimeRules.setOtMinutes(overtimeRulesDto.getOtMinutes());
            overtimeRules.setOtAmount(overtimeRulesDto.getOtAmount());
            overtimeRules.setOtType(overtimeRulesDto.getOtType());
            overtimeRules.setStartTime(overtimeRulesDto.getStartTime());
            overtimeRules.setEndTime(overtimeRulesDto.getEndTime());
            overtimeRules.setUserIds(overtimeRulesDto.getUserIds());
            OvertimeRules savedOvertimeRules = this.overtimeRulesRepository.save(overtimeRules);
            OvertimeRulesDto savedOvertimeRulesDto = new OvertimeRulesDto();
            BeanUtils.copyProperties(savedOvertimeRules, savedOvertimeRulesDto);
            savedOvertimeRulesDto.setCompanyId(companyDetails.getId());
            return savedOvertimeRulesDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteOvertimeRule(int id) {
        try {
            OvertimeRules overtimeRules = this.overtimeRulesRepository.findById(id).orElseThrow(() -> new RuntimeException("Overtime rule not found"));
            this.overtimeRulesRepository.delete(overtimeRules);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void assignOvertimeRuleToEmployee(int overtimeRuleId, OvertimeRulesDto overtimeRulesDto) {
        try {
            OvertimeRules overtimeRules = this.overtimeRulesRepository.findById(overtimeRuleId).orElseThrow(() -> new RuntimeException("Overtime rule not found"));
            overtimeRules.setUserIds(overtimeRulesDto.getUserIds());
            this.overtimeRulesRepository.save(overtimeRules);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

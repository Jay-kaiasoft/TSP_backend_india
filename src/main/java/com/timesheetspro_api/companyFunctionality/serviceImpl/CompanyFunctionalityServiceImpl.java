package com.timesheetspro_api.companyFunctionality.serviceImpl;

import com.timesheetspro_api.common.dto.CompanyFunctionality.CompanyFunctionalityDto;
import com.timesheetspro_api.common.model.companyFunctionality.CompanyFunctionality;
import com.timesheetspro_api.common.repository.company.CompanyFunctionalityRepository;
import com.timesheetspro_api.companyFunctionality.service.CompanyFunctionalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "companyFunctionalityService")
public class CompanyFunctionalityServiceImpl implements CompanyFunctionalityService {

    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Autowired
    private CompanyFunctionalityRepository companyFunctionalityRepository;

    @Override
    public List<CompanyFunctionality> getAllFunctionality() {
        try {
            return this.companyFunctionalityRepository.findAll();
        } catch (Exception e) {
            errorLogger.error("getAllFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyFunctionality getFunctionality(int functionalityId) {
        try {
            return this.companyFunctionalityRepository.findById(functionalityId).orElseThrow(() -> new RuntimeException("Functionality not found"));
        } catch (Exception e) {
            errorLogger.error("getFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyFunctionalityDto createFunctionality(CompanyFunctionalityDto functionalityDto) {
        try {
            CompanyFunctionality functionality = new CompanyFunctionality();
            BeanUtils.copyProperties(functionalityDto,functionality);
            this.companyFunctionalityRepository.save(functionality);
            return functionalityDto;
        } catch (Exception e) {
            errorLogger.error("createFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyFunctionalityDto updateFunctionality(int functionalityId, CompanyFunctionalityDto functionalityDto) {
        try {
            CompanyFunctionality functionality = this.companyFunctionalityRepository.findById(functionalityId).orElseThrow(() -> new RuntimeException("Functionality not found"));;
            BeanUtils.copyProperties(functionalityDto,functionality);
            this.companyFunctionalityRepository.save(functionality);
            return functionalityDto;
        } catch (Exception e) {
            errorLogger.error("updateFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteFunctionality(int functionalityId) {
        try {
            CompanyFunctionality functionality = this.companyFunctionalityRepository.findById(functionalityId).orElseThrow(() -> new RuntimeException("Functionality not found"));
            this.companyFunctionalityRepository.delete(functionality);
        } catch (Exception e) {
            errorLogger.error("deleteFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

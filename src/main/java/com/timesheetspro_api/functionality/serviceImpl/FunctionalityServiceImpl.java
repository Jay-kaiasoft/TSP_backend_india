package com.timesheetspro_api.functionality.serviceImpl;

import com.timesheetspro_api.common.dto.functionality.FunctionalityDto;
import com.timesheetspro_api.common.model.functionality.Functionality;
import com.timesheetspro_api.common.repository.FunctionalityRepository;
import com.timesheetspro_api.functionality.service.FunctionalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "FunctionalityService")
public class FunctionalityServiceImpl implements FunctionalityService {

    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Override
    public List<Functionality> getAllFunctionality() {
        try {
            return this.functionalityRepository.findAll();
        } catch (Exception e) {
            errorLogger.error("getAllFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Functionality getFunctionality(Long functionalityId) {
        try {
            return this.functionalityRepository.findById(functionalityId).orElseThrow(() -> new RuntimeException("Functionality not found"));
        } catch (Exception e) {
            errorLogger.error("getFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public FunctionalityDto createFunctionality(FunctionalityDto functionalityDto) {
        try {
            Functionality functionality = new Functionality();
            BeanUtils.copyProperties(functionalityDto,functionality);
            this.functionalityRepository.save(functionality);
            return functionalityDto;
        } catch (Exception e) {
            errorLogger.error("createFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public FunctionalityDto updateFunctionality(Long functionalityId, FunctionalityDto functionalityDto) {
        try {
            Functionality functionality = this.functionalityRepository.findById(functionalityId).orElseThrow(() -> new RuntimeException("Functionality not found"));;
            BeanUtils.copyProperties(functionalityDto,functionality);
            this.functionalityRepository.save(functionality);
            return functionalityDto;
        } catch (Exception e) {
            errorLogger.error("updateFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteFunctionality(Long functionalityId) {
        try {
            Functionality functionality = this.functionalityRepository.findById(functionalityId).orElseThrow(() -> new RuntimeException("Functionality not found"));
            this.functionalityRepository.delete(functionality);
        } catch (Exception e) {
            errorLogger.error("deleteFunctionality service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }
}

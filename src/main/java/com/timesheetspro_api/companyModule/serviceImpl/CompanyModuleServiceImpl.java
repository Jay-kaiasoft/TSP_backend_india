package com.timesheetspro_api.companyModule.serviceImpl;

import com.timesheetspro_api.common.dto.CompanyModuleDto.AssignCompanyActionsToCompanyModuleDto;
import com.timesheetspro_api.common.dto.CompanyModuleDto.CompanyModuleDto;
import com.timesheetspro_api.common.model.companyActions.CompanyActions;
import com.timesheetspro_api.common.model.companyModuleActions.CompanyModuleActions;
import com.timesheetspro_api.common.model.companyModules.CompanyModules;
import com.timesheetspro_api.common.repository.company.CompanyActionsRepository;
import com.timesheetspro_api.common.repository.company.CompanyFunctionalityRepository;
import com.timesheetspro_api.common.repository.company.CompanyModuleActionsRepository;
import com.timesheetspro_api.common.repository.company.CompanyModuleRepository;
import com.timesheetspro_api.companyModule.service.CompanyModuleService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service(value = "companyModuleService")
public class CompanyModuleServiceImpl implements CompanyModuleService {
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Autowired
    private CompanyModuleRepository moduleRepository;

    @Autowired
    private CompanyFunctionalityRepository functionalityRepository;

    @Autowired
    private CompanyActionsRepository actionsRepository;

    @Autowired
    private CompanyModuleActionsRepository moduleActionsRepository;

    @Override
    public CompanyModuleDto createModule(CompanyModuleDto moduleDto) {
        CompanyModuleDto responseDto = new CompanyModuleDto();
        try {
            CompanyModules module = new CompanyModules();
            module.setModuleName(moduleDto.getModuleName());
            module.setFunctionality(this.functionalityRepository.findById(moduleDto.getFunctionalityId()).orElseThrow(() -> new RuntimeException("Functionality not found.")));
            module = this.moduleRepository.save(module);

            AssignCompanyActionsToCompanyModuleDto assignPolicyToModuleDto = new AssignCompanyActionsToCompanyModuleDto();
            assignPolicyToModuleDto.setModuleId(module.getModuleId());
            assignPolicyToModuleDto.setActionIds(moduleDto.getActions());
            this.assignPolicies(assignPolicyToModuleDto);

            responseDto.setModuleId(module.getModuleId());
            responseDto.setModuleName(module.getModuleName());
            responseDto.setFunctionalityId(module.getFunctionality().getId());
            responseDto.setFunctionalityName(module.getFunctionality().getFunctionalityName());
            responseDto.setActions(moduleDto.getActions());
        } catch (Exception e) {
            errorLogger.error("createModule service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        return responseDto;
    }

    @Override
    public Map<String, Object> allModuleListPage(String searchKey, Pageable pageable) {
        Map<String, Object> resbody = new HashMap<>();
        List<CompanyModuleDto> moduleDtos = new ArrayList<>();
        try {
            Page<CompanyModules> moduleList;
            if (searchKey == null || searchKey.equals("")) {
                moduleList = this.moduleRepository.findAll(pageable);
            } else {
                moduleList = this.moduleRepository.getModuleByName(searchKey, pageable);
            }
            resbody.put("getTotalPages", moduleList.getTotalPages());
            resbody.put("getNumber", moduleList.getNumber());
            resbody.put("getSize", moduleList.getSize());
            resbody.put("getTotalRecords", moduleList.getTotalElements());
            for (CompanyModules module : moduleList) {
                CompanyModuleDto moduleDto = new CompanyModuleDto();
                BeanUtils.copyProperties(module, moduleDto);
                moduleDto.setFunctionalityId(module.getFunctionality().getId());
                moduleDto.setFunctionalityName(module.getFunctionality().getFunctionalityName());
                moduleDto.setActions(this.getModulePolicy(module.getModuleId()));
                moduleDtos.add(moduleDto);
            }
        } catch (Exception e) {
            errorLogger.error("allModuleListPage service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        resbody.put("modulesList", moduleDtos);
        return resbody;
    }

    @Override
    public Map<String, Object> moduleByFunctionalityListPage(int functionalityId, String searchKey, Pageable pageable) {
        Map<String, Object> resbody = new HashMap<>();
        List<CompanyModuleDto> moduleDtos = new ArrayList<>();
        try {
            Page<CompanyModules> moduleList;
            if (searchKey == null || searchKey.equals("")) {
                moduleList = this.moduleRepository.findModulesByFunctionalityId(functionalityId, pageable);
            } else {
                moduleList = this.moduleRepository.findModulesByFunctionalityIdAndName(functionalityId, searchKey, pageable);
            }
            resbody.put("getTotalPages", moduleList.getTotalPages());
            resbody.put("getNumber", moduleList.getNumber());
            resbody.put("getSize", moduleList.getSize());
            resbody.put("getTotalRecords", moduleList.getTotalElements());
            for (CompanyModules module : moduleList) {
                CompanyModuleDto moduleDto = new CompanyModuleDto();
                BeanUtils.copyProperties(module, moduleDto);
                moduleDto.setFunctionalityId(module.getFunctionality().getId());
                moduleDto.setFunctionalityName(module.getFunctionality().getFunctionalityName());
                moduleDto.setActions(this.getModulePolicy(module.getModuleId()));
                moduleDtos.add(moduleDto);
            }
        } catch (Exception e) {
            errorLogger.error("moduleByFunctionalityListPage service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        resbody.put("modulesList", moduleDtos);
        return resbody;
    }

    @Override
    public Map<String, Object> getAllModules() {
        Map<String, Object> resbody = new HashMap<>();
        List<CompanyModuleDto> moduleDtos = new ArrayList<>();
        try {
            List<CompanyModules> moduleList = this.moduleRepository.findAll();
            for (CompanyModules module : moduleList) {
                CompanyModuleDto moduleDto = new CompanyModuleDto();
                BeanUtils.copyProperties(module, moduleDto);
                moduleDto.setFunctionalityId(module.getFunctionality().getId());
                moduleDto.setFunctionalityName(module.getFunctionality().getFunctionalityName());
                moduleDto.setActions(this.getModulePolicy(module.getModuleId()));
                moduleDtos.add(moduleDto);
            }
        } catch (Exception e) {
            errorLogger.error("getAllModules service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        resbody.put("modulesList", moduleDtos);
        return resbody;
    }

    @Override
    public CompanyModuleDto getModuleById(int moduleId) {
        CompanyModuleDto responseDto = new CompanyModuleDto();
        try {
            CompanyModules module = this.moduleRepository.findModuleById(moduleId);
            BeanUtils.copyProperties(module, responseDto);
            responseDto.setFunctionalityId(module.getFunctionality().getId());
            responseDto.setFunctionalityName(module.getFunctionality().getFunctionalityName());
            responseDto.setActions(this.getModulePolicy(module.getModuleId()));
        } catch (Exception e) {
            errorLogger.error("getModuleById service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        return responseDto;
    }

    @Override
    public CompanyModuleDto updateModuleById(int moduleId, CompanyModuleDto moduleDto) throws Exception {
        CompanyModuleDto responseDto = new CompanyModuleDto();
        CompanyModules module = this.moduleRepository.findModuleById(moduleId);
        if (module != null) {
            module.setModuleName(moduleDto.getModuleName());
            module.setFunctionality(this.functionalityRepository.getById(moduleDto.getFunctionalityId()));
            module = this.moduleRepository.save(module);

            AssignCompanyActionsToCompanyModuleDto assignPolicyToModuleDto = new AssignCompanyActionsToCompanyModuleDto();
            assignPolicyToModuleDto.setModuleId(module.getModuleId());
            assignPolicyToModuleDto.setActionIds(moduleDto.getActions());
            this.assignPolicies(assignPolicyToModuleDto);

            BeanUtils.copyProperties(module, responseDto);
            responseDto.setFunctionalityId(module.getFunctionality().getId());
            responseDto.setFunctionalityName(module.getFunctionality().getFunctionalityName());
            responseDto.setActions(moduleDto.getActions());
        } else {
            throw new Exception("Such Module Doesn't Exist");
        }
        return responseDto;
    }

    @Transactional
    @Override
    public void assignPolicies(AssignCompanyActionsToCompanyModuleDto assignActionsToModuleDto) throws Exception {
        try {
            CompanyModules module = this.moduleRepository.findModuleById(assignActionsToModuleDto.getModuleId());
            try {
                this.moduleActionsRepository.deleteModuleActionByIds(
                        module.getModuleActions().stream().map(CompanyModuleActions::getModuleActionId).collect(Collectors.toList())
                );
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

            for (Integer actionId : assignActionsToModuleDto.getActionIds()) {
                CompanyActions actions = this.actionsRepository.findActionById(actionId);
                CompanyModuleActions storeModuleActions = new CompanyModuleActions();
                storeModuleActions.setModule(module);
                storeModuleActions.setAction(actions);
                this.moduleActionsRepository.save(storeModuleActions);
            }
        } catch (Exception e) {
            errorLogger.error("assignPolicies service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public void deleteModuleById(int moduleId) throws Exception {
        try {
            this.moduleRepository.deleteById(moduleId);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public List<Integer> getModulePolicy(int moduleId) {
        List<Integer> lst = new ArrayList<>();
        CompanyModules module = this.moduleRepository.findModuleById(moduleId);
        try {
            Set<CompanyModuleActions> modulePolicySet = module.getModuleActions();
            lst = modulePolicySet.stream().map(e -> e.getAction().getActionId()).sorted().collect(toList());
        } catch (Exception ignored) {
            throw new RuntimeException(ignored.getMessage());
        }
        return lst;
    }
}

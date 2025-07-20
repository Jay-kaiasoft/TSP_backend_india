package com.timesheetspro_api.module.serviceImpl;

import com.timesheetspro_api.common.dto.module.AssignActionsToModuleDto;
import com.timesheetspro_api.common.dto.module.ModuleDto;
import com.timesheetspro_api.common.model.actions.Actions;
import com.timesheetspro_api.common.model.moduleActions.ModuleActions;
import com.timesheetspro_api.common.repository.ActionsRepository;
import com.timesheetspro_api.common.repository.FunctionalityRepository;
import com.timesheetspro_api.common.repository.ModuleActionsRepository;
import com.timesheetspro_api.common.repository.ModuleRepository;
import com.timesheetspro_api.module.service.ModuleService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.timesheetspro_api.common.model.module.Module;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service(value = "moduleService")
public class ModuleServiceImpl implements ModuleService {
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Autowired
    private ActionsRepository actionsRepository;

    @Autowired
    private ModuleActionsRepository moduleActionsRepository;

    @Override
    public ModuleDto createModule(ModuleDto moduleDto) {
        ModuleDto responseDto = new ModuleDto();
        try {
            Module module = new Module();
            module.setModuleName(moduleDto.getModuleName());
            module.setFunctionality(this.functionalityRepository.findById(moduleDto.getFunctionalityId()).orElseThrow(() -> new RuntimeException("Functionality not found.")));
            module = this.moduleRepository.save(module);

            AssignActionsToModuleDto assignPolicyToModuleDto = new AssignActionsToModuleDto();
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
        List<ModuleDto> moduleDtos = new ArrayList<>();
        try {
            Page<Module> moduleList;
            if (searchKey == null || searchKey.equals("")) {
                moduleList = this.moduleRepository.findAll(pageable);
            } else {
                moduleList = this.moduleRepository.getModuleByName(searchKey, pageable);
            }
            resbody.put("getTotalPages", moduleList.getTotalPages());
            resbody.put("getNumber", moduleList.getNumber());
            resbody.put("getSize", moduleList.getSize());
            resbody.put("getTotalRecords", moduleList.getTotalElements());
            for (Module module : moduleList) {
                ModuleDto moduleDto = new ModuleDto();
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
    public Map<String, Object> moduleByFunctionalityListPage(Long functionalityId, String searchKey, Pageable pageable) {
        Map<String, Object> resbody = new HashMap<>();
        List<ModuleDto> moduleDtos = new ArrayList<>();
        try {
            Page<Module> moduleList;
            if (searchKey == null || searchKey.equals("")) {
                moduleList = this.moduleRepository.findModulesByFunctionalityId(functionalityId, pageable);
            } else {
                moduleList = this.moduleRepository.findModulesByFunctionalityIdAndName(functionalityId, searchKey, pageable);
            }
            resbody.put("getTotalPages", moduleList.getTotalPages());
            resbody.put("getNumber", moduleList.getNumber());
            resbody.put("getSize", moduleList.getSize());
            resbody.put("getTotalRecords", moduleList.getTotalElements());
            for (Module module : moduleList) {
                ModuleDto moduleDto = new ModuleDto();
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
        List<ModuleDto> moduleDtos = new ArrayList<>();
        try {
            List<Module> moduleList = this.moduleRepository.findAll();
            for (Module module : moduleList) {
                ModuleDto moduleDto = new ModuleDto();
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
    public ModuleDto getModuleById(Long moduleId) {
        ModuleDto responseDto = new ModuleDto();
        try {
            Module module = this.moduleRepository.findModuleById(moduleId);
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
    public ModuleDto updateModuleById(Long moduleId, ModuleDto moduleDto) throws Exception {
        ModuleDto responseDto = new ModuleDto();
        Module module = this.moduleRepository.findModuleById(moduleId);
        if (module != null) {
            module.setModuleName(moduleDto.getModuleName());
            module.setFunctionality(this.functionalityRepository.getById(moduleDto.getFunctionalityId()));
            module = this.moduleRepository.save(module);

            AssignActionsToModuleDto assignPolicyToModuleDto = new AssignActionsToModuleDto();
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
    public void assignPolicies(AssignActionsToModuleDto assignActionsToModuleDto) throws Exception {
        try {
            Module module = this.moduleRepository.findModuleById(assignActionsToModuleDto.getModuleId());
            try {
                this.moduleActionsRepository.deleteModuleActionByIds(
                        module.getModuleActions().stream().map(ModuleActions::getModuleActionId).collect(Collectors.toList())
                );
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

            for (Long actionId : assignActionsToModuleDto.getActionIds()) {
                Actions actions = this.actionsRepository.findActionById(actionId);
                ModuleActions storeModuleActions = new ModuleActions();
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
    public void deleteModuleById(Long moduleId) throws Exception {
        try {
            this.moduleRepository.deleteById(moduleId);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Override
    public List<Long> getModulePolicy(Long moduleId) {
        List<Long> lst = new ArrayList<>();
        Module module = this.moduleRepository.findModuleById(moduleId);
        try {
            Set<ModuleActions> modulePolicySet = module.getModuleActions();
            lst = modulePolicySet.stream().map(e -> e.getAction().getActionId()).sorted().collect(toList());
        } catch (Exception ignored) {
            throw new RuntimeException(ignored.getMessage());
        }
        return lst;
    }
}

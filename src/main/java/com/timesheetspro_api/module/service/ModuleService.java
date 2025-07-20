package com.timesheetspro_api.module.service;

import com.timesheetspro_api.common.dto.module.AssignActionsToModuleDto;
import com.timesheetspro_api.common.dto.module.ModuleDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ModuleService {
    ModuleDto createModule(ModuleDto moduleDto);
    Map<String, Object> allModuleListPage(String searchKey, Pageable pageable);
    Map<String, Object> moduleByFunctionalityListPage(Long functionalityId, String searchKey, Pageable pageable);
    Map<String, Object> getAllModules();
    ModuleDto getModuleById(Long moduleId);
    ModuleDto updateModuleById(Long moduleId, ModuleDto moduleDto) throws Exception;
    void assignPolicies(AssignActionsToModuleDto assignActionsToModuleDto) throws Exception;
    void deleteModuleById(Long moduleId) throws Exception;
    List<Long> getModulePolicy(Long moduleId);
}

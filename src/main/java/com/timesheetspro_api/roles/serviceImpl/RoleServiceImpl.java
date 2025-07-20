package com.timesheetspro_api.roles.serviceImpl;

import com.timesheetspro_api.common.dto.roles.RoleDto;
import com.timesheetspro_api.common.dto.roles.RoleFunctionalityDto;
import com.timesheetspro_api.common.dto.roles.RoleModuleDto;
import com.timesheetspro_api.common.dto.roles.RolesActionsDto;
import com.timesheetspro_api.common.model.actions.Actions;
import com.timesheetspro_api.common.model.functionality.Functionality;
import com.timesheetspro_api.common.model.module.Module;
import com.timesheetspro_api.common.model.moduleActions.ModuleActions;
import com.timesheetspro_api.common.model.roleModuleActions.RoleModuleActions;
import com.timesheetspro_api.common.model.roles.Roles;
import com.timesheetspro_api.common.repository.*;
import com.timesheetspro_api.roles.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Autowired
    private ActionsRepository actionsRepository;

    @Autowired
    private ModuleActionsRepository moduleActionsRepository;

    @Autowired
    private RoleModuleActionsRepository roleModuleActionsRepository;

    @Override
    public List<RoleDto> getAllRolesList() {
        try {
            List<Roles> roles = this.rolesRepository.findAll();
            List<RoleDto> roleDtoList = new ArrayList<>();
            if (!roles.isEmpty()) {
                for (Roles roles1 : roles) {
                    Roles role = this.rolesRepository.findRoleById(roles1.getRoleId());
                    if (role != null) {
                        RoleDto roleDto = new RoleDto();
                        roleDto.setRoleName(role.getRoleName());
                        roleDto.setRoleId(role.getRoleId());
                        roleDtoList.add(roleDto);
                    }
                }
            }
            return roleDtoList;
        } catch (Exception e) {
            errorLogger.error("getAllRolesList service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RoleDto createRole(RoleDto rolesDto) {
        RoleDto responseDto = new RoleDto();
        try {
            Roles role = new Roles();
            role.setRoleName(rolesDto.getRoleName());
            role = this.rolesRepository.save(role);

            BeanUtils.copyProperties(role, responseDto);

            this.savePolicy(role.getRoleId(), rolesDto.getRolesActions());
            responseDto.setRolesActions(rolesDto.getRolesActions());
        } catch (Exception e) {
            errorLogger.error("createRole service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        return responseDto;
    }

    @Override
    public Map<String, Object> rolesList(String searchKey, Pageable pageable) {
        Map<String, Object> resbody = new HashMap<>();
        List<RoleDto> rolesDtos = new ArrayList<>();
        try {
            Page<Roles> rolesList;
            if (searchKey == null || searchKey.equals("")) {
                rolesList = this.rolesRepository.findAll(pageable);
            } else {
                rolesList = this.rolesRepository.getRolesByName(searchKey, pageable);
            }
            resbody.put("getTotalPages", rolesList.getTotalPages());
            resbody.put("getNumber", rolesList.getNumber());
            resbody.put("getSize", rolesList.getSize());
            resbody.put("getTotalRecords", rolesList.getTotalElements());
            for (Roles role : rolesList) {
                RoleDto rolesDto = new RoleDto();
                BeanUtils.copyProperties(role, rolesDto);
                rolesDto.setRolesActions(this.getPolicy(role.getRoleId()));
                rolesDtos.add(rolesDto);
            }
        } catch (Exception e) {
            errorLogger.error("getRolesListPage service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        resbody.put("rolesList", rolesDtos);
        return resbody;
    }

    @Override
    public Map<String, Object> getAllRoles() {
        Map<String, Object> resbody = new HashMap<>();
        List<RoleDto> rolesDtos = new ArrayList<>();
        try {
            List<Roles> roleList = this.rolesRepository.findRolesExceptOwner();
            for (Roles role : roleList) {
                RoleDto rolesDto = new RoleDto();
                BeanUtils.copyProperties(role, rolesDto);
                rolesDtos.add(rolesDto);
            }
        } catch (Exception e) {
            errorLogger.error("getAllRoles service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        resbody.put("rolesList", rolesDtos);
        return resbody;
    }

    @Override
    public RoleDto getRoleById(Long roleId) {
        RoleDto responseDto = new RoleDto();
        try {
            Roles role = this.rolesRepository.findRoleById(roleId);
            BeanUtils.copyProperties(role, responseDto);
            responseDto.setRolesActions(this.getPolicy(role.getRoleId()));
        } catch (Exception e) {
            errorLogger.error("getRoleById service Error: " + e);
            throw new RuntimeException(e.getMessage());
        }
        return responseDto;
    }

    @Override
    public RoleDto updateById(Long roleId, RoleDto rolesDto) throws Exception {
        RoleDto responseDto = new RoleDto();
        Roles role = this.rolesRepository.findRoleById(roleId);
        if (role != null) {
            BeanUtils.copyProperties(rolesDto, role);
            role = this.rolesRepository.save(role);

            this.savePolicy(role.getRoleId(), rolesDto.getRolesActions());
            BeanUtils.copyProperties(role, responseDto);
            responseDto.setRolesActions(rolesDto.getRolesActions());
        } else {
            throw new Exception("Such Role Doesn't Exist");
        }
        return responseDto;
    }

    @Override
    public void deleteRoleById(Long roleId) {
        try {
            Roles roles = this.rolesRepository.findById(roleId).orElseThrow(() -> new Exception("Role not found"));
            this.rolesRepository.delete(roles);
        } catch (Exception e) {
            errorLogger.error("deleteRoleById service Error: " + e);
            throw new RuntimeException("Failed to delete role: " + e.getMessage());
        }
    }

    @Override
    public RolesActionsDto getPolicy(Long roleId) throws Exception {
        RolesActionsDto rolePolicyDto = new RolesActionsDto();
        List<RoleFunctionalityDto> functionalities = new ArrayList<>();
        if (roleId != 0L) {
            Roles role = null;
            try {
                role = this.rolesRepository.findRoleById(roleId);
            } catch (Exception e) {
                throw new Exception("No such Role Exist");
            }
        }
        List<Functionality> functionalityList = this.functionalityRepository.findAll();
        functionalityList.forEach(functionality -> {
            List<RoleModuleDto> modules = new ArrayList<>();
            List<Module> moduleList = this.moduleRepository.findModulesByFunctionalityId(functionality.getId());
            moduleList.forEach(module -> {
                Set<ModuleActions> modulePolicySet = module.getModuleActions();
                List<Long> moduleAssignedPolicy = new ArrayList<>();
                List<Long> roleAssignedPolicy = new ArrayList<>();
                try {
                    moduleAssignedPolicy = modulePolicySet.stream().map(e -> e.getAction().getActionId()).sorted().collect(Collectors.toList());
                    if (roleId != 0L) {
                        roleAssignedPolicy = this.roleModuleActionsRepository.findModulesByRoleIdAndMpIds(roleId, modulePolicySet.stream().map(ModuleActions::getModuleActionId).collect(Collectors.toList()))
                                .stream().map(Actions::getActionId).sorted().collect(Collectors.toList());
                    } else {
                        roleAssignedPolicy = new ArrayList<>();
                    }
                } catch (Exception ignored) {
                }
                try {
                    roleAssignedPolicy = roleAssignedPolicy.isEmpty() ? new ArrayList<>() : roleAssignedPolicy;
                } catch (Exception e) {
                    roleAssignedPolicy = new ArrayList<>();
                }
                RoleModuleDto roleModuleDto = new RoleModuleDto();
                roleModuleDto.setModuleId(module.getModuleId());
                roleModuleDto.setModuleName(module.getModuleName());
                roleModuleDto.setModuleAssignedActions(moduleAssignedPolicy);
                roleModuleDto.setRoleAssignedActions(roleAssignedPolicy);
                modules.add(roleModuleDto);
            });
            RoleFunctionalityDto roleFunctionalityDto = new RoleFunctionalityDto();
            roleFunctionalityDto.setFunctionalityId(functionality.getId());
            roleFunctionalityDto.setFunctionalityName(functionality.getFunctionalityName());
            roleFunctionalityDto.setModules(modules);
            functionalities.add(roleFunctionalityDto);
        });
        rolePolicyDto.setFunctionalities(functionalities);
        return rolePolicyDto;
    }

    @Transactional
    public RolesActionsDto savePolicy(Long roleId, RolesActionsDto roleActionDto) throws Exception {
        Roles role = this.rolesRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        List<RoleModuleActions> roleModulePolicyList = this.roleModuleActionsRepository.findByRoleId(roleId);
        try {
            this.roleModuleActionsRepository.deleteByRoleId(roleId);
        } catch (Exception ignored) {
        }

        roleActionDto.getFunctionalities().forEach(functionality -> {
            functionality.getModules().forEach(module -> {
                if (!module.getRoleAssignedActions().isEmpty()) {
                    module.getRoleAssignedActions().forEach(e -> {
                        Actions policy = this.actionsRepository.findActionById(e);
                        ModuleActions modulePolicy = this.moduleActionsRepository.findModuleActionsByModuleAndActions(module.getModuleId(), policy.getActionId());
                        RoleModuleActions roleModulePolicy = new RoleModuleActions();
                        roleModulePolicy.setRole(role);
                        roleModulePolicy.setModuleActions(modulePolicy);
                        this.roleModuleActionsRepository.save(roleModulePolicy);
                    });
                }
            });
        });
        return roleActionDto;
    }
}


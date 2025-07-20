package com.timesheetspro_api.companyEmployeeRole.serviceImpl;

import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRoleFunctionalityDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRoleModuleDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesActionsDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesDto;

import com.timesheetspro_api.common.model.companyActions.CompanyActions;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyEmployeeRoles.CompanyEmployeeRoles;
import com.timesheetspro_api.common.model.companyFunctionality.CompanyFunctionality;
import com.timesheetspro_api.common.model.companyModuleActions.CompanyModuleActions;
import com.timesheetspro_api.common.model.companyModules.CompanyModules;
import com.timesheetspro_api.common.model.companyRoleModuleActions.CompanyRoleModuleActions;
import com.timesheetspro_api.common.repository.company.*;
import com.timesheetspro_api.companyEmployeeRole.service.CompanyEmployeeRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service(value = "companyEmployeeRoleService")
public class CompanyEmployeeRoleServiceImpl implements CompanyEmployeeRoleService {

    @Autowired
    private CompanyEmployeeRoleRepository companyEmployeeRoleRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private CompanyFunctionalityRepository companyFunctionalityRepository;

    @Autowired
    private CompanyEmployeeRoleModuleActionsRepository companyEmployeeRoleModuleActionsRepository;

    @Autowired
    private CompanyModuleRepository companyModuleRepository;

    @Autowired
    private CompanyActionsRepository companyActionsRepository;

    @Autowired
    private CompanyModuleActionsRepository companyModuleActionsRepository;

    @Override
    public List<CompanyEmployeeRolesDto> getAllRolesList() {
        try {
            List<CompanyEmployeeRoles> roles = this.companyEmployeeRoleRepository.findAll();
            List<CompanyEmployeeRolesDto> roleDtoList = new ArrayList<>();
            if (!roles.isEmpty()) {
                for (CompanyEmployeeRoles roles1 : roles) {
                    CompanyEmployeeRoles role = this.companyEmployeeRoleRepository.findRoleById(roles1.getRoleId());
                    if (role != null) {
                        CompanyEmployeeRolesDto roleDto = new CompanyEmployeeRolesDto();
                        roleDto.setRoleName(role.getRoleName());
                        roleDto.setRoleId(role.getRoleId());
                        roleDtoList.add(roleDto);
                    }
                }
            }
            return roleDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> rolesList(String searchKey, Pageable pageable) {
        Map<String, Object> resbody = new HashMap<>();
        List<CompanyEmployeeRolesDto> rolesDtos = new ArrayList<>();
        try {
            Page<CompanyEmployeeRoles> rolesList;
            if (searchKey == null || searchKey.equals("")) {
                rolesList = this.companyEmployeeRoleRepository.findAll(pageable);
            } else {
                rolesList = this.companyEmployeeRoleRepository.getRolesByName(searchKey, pageable);
            }
            resbody.put("getTotalPages", rolesList.getTotalPages());
            resbody.put("getNumber", rolesList.getNumber());
            resbody.put("getSize", rolesList.getSize());
            resbody.put("getTotalRecords", rolesList.getTotalElements());
            for (CompanyEmployeeRoles role : rolesList) {
                CompanyEmployeeRolesDto rolesDto = new CompanyEmployeeRolesDto();
                BeanUtils.copyProperties(role, rolesDto);
                rolesDto.setRolesActions(this.getPolicy(role.getRoleId()));
                rolesDtos.add(rolesDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        resbody.put("rolesList", rolesDtos);
        return resbody;
    }

    @Override
    public Map<String, Object> getAllRoles() {
        Map<String, Object> resbody = new HashMap<>();
        List<CompanyEmployeeRolesDto> rolesDtos = new ArrayList<>();
        try {
            List<CompanyEmployeeRoles> roleList = this.companyEmployeeRoleRepository.findRolesExceptOwner();
            for (CompanyEmployeeRoles role : roleList) {
                CompanyEmployeeRolesDto rolesDto = new CompanyEmployeeRolesDto();
                BeanUtils.copyProperties(role, rolesDto);
                rolesDtos.add(rolesDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        resbody.put("rolesList", rolesDtos);
        return resbody;
    }

    @Override
    public List<CompanyEmployeeRolesDto> getAllRolesByCompanyId(int id) {
        try {
            List<CompanyEmployeeRoles> companyEmployeeRolesList = this.companyEmployeeRoleRepository.findByCompanyId(id);
            List<CompanyEmployeeRolesDto> companyEmployeeRolesDtoList = new ArrayList<>();

            if (!companyEmployeeRolesList.isEmpty()) {
                for (CompanyEmployeeRoles companyEmployeeRoles : companyEmployeeRolesList) {
                    companyEmployeeRolesDtoList.add(this.getRole(companyEmployeeRoles.getRoleId()));
                }
            }
            return companyEmployeeRolesDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyEmployeeRolesDto getRole(int id) {
        try {
            CompanyEmployeeRoles companyEmployeeRoles = this.companyEmployeeRoleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
            CompanyEmployeeRolesDto companyEmployeeRolesDto = new CompanyEmployeeRolesDto();
            BeanUtils.copyProperties(companyEmployeeRoles, companyEmployeeRolesDto);
            companyEmployeeRolesDto.setRolesActions(this.getPolicy(id));
            return companyEmployeeRolesDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyEmployeeRolesDto createRole(CompanyEmployeeRolesDto companyEmployeeRolesDto) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyEmployeeRolesDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            CompanyEmployeeRoles companyEmployeeRoles = new CompanyEmployeeRoles();
            companyEmployeeRoles.setCompanyDetails(companyDetails);
            BeanUtils.copyProperties(companyEmployeeRolesDto, companyEmployeeRoles);
            this.companyEmployeeRoleRepository.save(companyEmployeeRoles);
            if (companyEmployeeRolesDto.getRolesActions() != null) {
                this.savePolicy(companyEmployeeRoles.getRoleId(), companyEmployeeRolesDto.getRolesActions());
            }
            companyEmployeeRolesDto.setRoleId(companyEmployeeRoles.getRoleId());
            return companyEmployeeRolesDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyEmployeeRolesDto updateRole(int id, CompanyEmployeeRolesDto companyEmployeeRolesDto) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyEmployeeRolesDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            CompanyEmployeeRoles companyEmployeeRoles = this.companyEmployeeRoleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
            companyEmployeeRoles.setCompanyDetails(companyDetails);
            this.savePolicy(id, companyEmployeeRolesDto.getRolesActions());
            BeanUtils.copyProperties(companyEmployeeRolesDto, companyEmployeeRoles);
            this.companyEmployeeRoleRepository.save(companyEmployeeRoles);
            return companyEmployeeRolesDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteRole(int id) {
        try {
            CompanyEmployeeRoles companyEmployeeRoles = this.companyEmployeeRoleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
            this.companyEmployeeRoleRepository.delete(companyEmployeeRoles);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyEmployeeRolesActionsDto getPolicy(int roleId) throws Exception {
        CompanyEmployeeRolesActionsDto rolePolicyDto = new CompanyEmployeeRolesActionsDto();
        List<CompanyEmployeeRoleFunctionalityDto> functionalities = new ArrayList<>();
        if (roleId == 0) {
        } else {
            CompanyEmployeeRoles role = null;
            try {
                role = this.companyEmployeeRoleRepository.findRoleById(roleId);
            } catch (Exception e) {
                throw new Exception("No such Role Exist");
            }
        }
        List<CompanyFunctionality> functionalityList = this.companyFunctionalityRepository.findAll();
        functionalityList.forEach(functionality -> {
            List<CompanyEmployeeRoleModuleDto> modules = new ArrayList<>();
            List<CompanyModules> moduleList = this.companyModuleRepository.findModulesByFunctionalityId(functionality.getId());
            moduleList.forEach(module -> {
                Set<CompanyModuleActions> modulePolicySet = module.getModuleActions();
                List<Integer> moduleAssignedPolicy = new ArrayList<>();
                List<Integer> roleAssignedPolicy = new ArrayList<>();
                try {
                    moduleAssignedPolicy = modulePolicySet.stream().map(e -> e.getAction().getActionId()).sorted().collect(Collectors.toList());
                    if (roleId == 0) {
                        roleAssignedPolicy = new ArrayList<>();
                    } else {
                        roleAssignedPolicy = this.companyEmployeeRoleModuleActionsRepository
                                .findModuleActionIdsByRoleIdAndMpIds(
                                        roleId,
                                        modulePolicySet.stream()
                                                .map(CompanyModuleActions::getModuleActionId)
                                                .collect(Collectors.toList())
                                ).stream()
                                .sorted()
                                .collect(Collectors.toList());
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                try {
                    roleAssignedPolicy = roleAssignedPolicy.isEmpty() ? new ArrayList<>() : roleAssignedPolicy;
                } catch (Exception e) {
                    roleAssignedPolicy = new ArrayList<>();
                }
                CompanyEmployeeRoleModuleDto roleModuleDto = new CompanyEmployeeRoleModuleDto();
                roleModuleDto.setModuleId(module.getModuleId());
                roleModuleDto.setModuleName(module.getModuleName());
                roleModuleDto.setModuleAssignedActions(moduleAssignedPolicy);
                roleModuleDto.setRoleAssignedActions(roleAssignedPolicy);
                modules.add(roleModuleDto);
            });
            CompanyEmployeeRoleFunctionalityDto roleFunctionalityDto = new CompanyEmployeeRoleFunctionalityDto();
            roleFunctionalityDto.setFunctionalityId(functionality.getId());
            roleFunctionalityDto.setFunctionalityName(functionality.getFunctionalityName());
            roleFunctionalityDto.setModules(modules);
            functionalities.add(roleFunctionalityDto);
        });
        rolePolicyDto.setFunctionalities(functionalities);
        return rolePolicyDto;
    }

    @Transactional
    public CompanyEmployeeRolesActionsDto savePolicy(int roleId, CompanyEmployeeRolesActionsDto roleActionDto) throws Exception {
        CompanyEmployeeRoles role = this.companyEmployeeRoleRepository.findRoleById(roleId);
        System.out.println("role" + role.getRoleName());
        this.companyEmployeeRoleModuleActionsRepository.findByRoleId(roleId);
        try {
            this.companyEmployeeRoleModuleActionsRepository.deleteByRoleId(roleId);
        } catch (Exception ignored) {
        }

        roleActionDto.getFunctionalities().forEach(functionality -> {
            functionality.getModules().forEach(module -> {
                if (!module.getRoleAssignedActions().isEmpty()) {
                    module.getRoleAssignedActions().forEach(e -> {
                        CompanyActions policy = this.companyActionsRepository.findActionById(e);
                        CompanyModuleActions modulePolicy = this.companyModuleActionsRepository.findModuleActionsByModuleAndActions(module.getModuleId(), policy.getActionId());
                        CompanyRoleModuleActions roleModulePolicy = new CompanyRoleModuleActions();
                        roleModulePolicy.setRole(role);
                        roleModulePolicy.setModuleActions(modulePolicy);
                        this.companyEmployeeRoleModuleActionsRepository.save(roleModulePolicy);
                    });
                }
            });
        });
        return roleActionDto;
    }
}

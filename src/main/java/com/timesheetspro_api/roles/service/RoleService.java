package com.timesheetspro_api.roles.service;

import com.timesheetspro_api.common.dto.roles.RoleDto;
import com.timesheetspro_api.common.dto.roles.RolesActionsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface RoleService {
    List<RoleDto> getAllRolesList();

    RoleDto createRole(RoleDto rolesDto);
    Map<String, Object> rolesList(String searchKey, Pageable pageable);
    Map<String, Object> getAllRoles();
    RoleDto getRoleById(Long roleId);
    RoleDto updateById(Long roleId, RoleDto rolesDto) throws Exception;
    void deleteRoleById(Long roleId);
    RolesActionsDto getPolicy(Long roleId) throws Exception;
}

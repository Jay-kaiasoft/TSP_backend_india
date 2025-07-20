package com.timesheetspro_api.companyEmployeeRole.service;

import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesActionsDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CompanyEmployeeRoleService {
    List<CompanyEmployeeRolesDto> getAllRolesList();

    Map<String, Object> rolesList(String searchKey, Pageable pageable);

    Map<String, Object> getAllRoles();

    List<CompanyEmployeeRolesDto> getAllRolesByCompanyId(int id);

    CompanyEmployeeRolesDto getRole(int id);

    CompanyEmployeeRolesDto createRole(CompanyEmployeeRolesDto companyEmployeeRolesDto);

    CompanyEmployeeRolesDto updateRole(int id, CompanyEmployeeRolesDto companyEmployeeRolesDto);

    void deleteRole(int id);

    CompanyEmployeeRolesActionsDto getPolicy(int roleId) throws Exception;
}

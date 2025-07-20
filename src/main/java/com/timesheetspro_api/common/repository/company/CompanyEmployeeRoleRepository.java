package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyEmployeeRoles.CompanyEmployeeRoles;
import com.timesheetspro_api.common.model.roles.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyEmployeeRoleRepository extends JpaRepository<CompanyEmployeeRoles, Integer>, JpaSpecificationExecutor<CompanyEmployeeRoles> {
    @Query("SELECT c FROM CompanyEmployeeRoles c WHERE c.companyDetails.id=:id")
    List<CompanyEmployeeRoles> findByCompanyId(int id);

    @Query("SELECT r FROM CompanyEmployeeRoles r WHERE r.roleId=:roleId")
    CompanyEmployeeRoles findRoleById(int roleId);

    @Query("SELECT r FROM CompanyEmployeeRoles r WHERE LOWER(r.roleName) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<CompanyEmployeeRoles> getRolesByName(@Param("searchKey")String searchKey, Pageable pageable);

    @Query("SELECT r FROM CompanyEmployeeRoles r WHERE r.roleId != 1")
    List<CompanyEmployeeRoles> findRolesExceptOwner();
}
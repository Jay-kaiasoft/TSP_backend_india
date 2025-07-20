package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyRoleModuleActions.CompanyRoleModuleActions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CompanyEmployeeRoleModuleActionsRepository extends JpaRepository<CompanyRoleModuleActions, Integer> {
//    @Query("SELECT rmp.moduleActions.action FROM CompanyRoleModuleActions rmp WHERE rmp.role.id = :roleId AND rmp.moduleActions.id IN :moduleActionId")
//    List<CompanyRoleModuleActions> findModulesByRoleIdAndMpIds(@Param("roleId") int roleId, @Param("moduleActionId") List<Integer> moduleActionId);

    @Query("SELECT rmp.moduleActions.action.actionId FROM CompanyRoleModuleActions rmp WHERE rmp.role.id = :roleId AND rmp.moduleActions.moduleActionId IN :moduleActionId")
    List<Integer> findModuleActionIdsByRoleIdAndMpIds(@Param("roleId") int roleId, @Param("moduleActionId") List<Integer> moduleActionId);


    @Query("SELECT rmp FROM CompanyRoleModuleActions rmp WHERE rmp.role.id = :roleId")
    List<CompanyRoleModuleActions> findByRoleId(@Param("roleId") int roleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CompanyRoleModuleActions rmp WHERE rmp.role.id = :roleId")
    void deleteByRoleId(@Param("roleId") int roleId);
}

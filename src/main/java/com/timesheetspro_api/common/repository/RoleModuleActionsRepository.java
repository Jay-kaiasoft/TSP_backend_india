package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.actions.Actions;
import com.timesheetspro_api.common.model.roleModuleActions.RoleModuleActions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RoleModuleActionsRepository extends JpaRepository<RoleModuleActions, Long> {
    @Query("SELECT rmp.moduleActions.action FROM RoleModuleActions rmp WHERE rmp.role.roleId = :roleId AND rmp.moduleActions.moduleActionId IN :moduleActionId")
    List<Actions> findModulesByRoleIdAndMpIds(@Param("roleId") Long roleId, @Param("moduleActionId") List<Long> moduleActionId);

    @Query("SELECT rmp FROM RoleModuleActions rmp WHERE rmp.role.roleId = :roleId")
    List<RoleModuleActions> findByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RoleModuleActions rmp WHERE rmp.role.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);
}

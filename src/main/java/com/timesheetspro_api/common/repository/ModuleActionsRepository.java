package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.moduleActions.ModuleActions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleActionsRepository extends JpaRepository<ModuleActions, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ModuleActions ma WHERE ma.moduleActionId IN :moduleActionIds")
    void deleteModuleActionByIds(@Param("moduleActionIds") List<Long> moduleActionIds);

    @Query("SELECT ma FROM ModuleActions ma WHERE ma.module.moduleId = :moduleId AND ma.action.actionId = :actionId")
    ModuleActions findModuleActionsByModuleAndActions(Long moduleId, Long actionId);
}

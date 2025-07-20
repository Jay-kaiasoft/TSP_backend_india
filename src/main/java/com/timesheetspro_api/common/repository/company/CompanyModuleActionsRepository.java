package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyModuleActions.CompanyModuleActions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyModuleActionsRepository extends JpaRepository<CompanyModuleActions, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM CompanyModuleActions ma WHERE ma.moduleActionId IN :moduleActionIds")
    void deleteModuleActionByIds(@Param("moduleActionIds") List<Integer> moduleActionIds);

    @Query("SELECT ma FROM CompanyModuleActions ma WHERE ma.module.moduleId = :moduleId AND ma.action.actionId = :actionId")
    CompanyModuleActions findModuleActionsByModuleAndActions(int moduleId, int actionId);
}

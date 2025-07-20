package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyActions.CompanyActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyActionsRepository extends JpaRepository<CompanyActions, Integer> {
    @Query("SELECT r FROM CompanyActions r WHERE LOWER(r.actionName) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<CompanyActions> getActionsByName(@Param("searchKey")String searchKey, Pageable pageable);

    @Query("SELECT r FROM CompanyActions r WHERE r.actionId=:actionId")
    CompanyActions findActionById(int actionId);
}

package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.actions.Actions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionsRepository extends JpaRepository<Actions, Long> {
    @Query("SELECT r FROM Actions r WHERE LOWER(r.actionName) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<Actions> getActionsByName(@Param("searchKey")String searchKey, Pageable pageable);

    @Query("SELECT r FROM Actions r WHERE r.actionId=:actionId")
    Actions findActionById(Long actionId);
}

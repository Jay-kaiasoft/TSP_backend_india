package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.weeklyOff.WeeklyOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyOffRepository extends JpaRepository<WeeklyOff, Integer> {
    @Query("SELECT w FROM WeeklyOff w WHERE w.name=:name AND w.companyDetails.id=:id")
    WeeklyOff existsByName(Integer id, String name);

    @Query("SELECT w FROM WeeklyOff w WHERE w.name=:name AND w.id!=:id")
    WeeklyOff isExits(Integer id, String name);

    @Query("SELECT w FROM WeeklyOff w WHERE w.companyDetails.id=:id")
    List<WeeklyOff> findByCompany(Integer id);

    @Query("SELECT w FROM WeeklyOff w WHERE w.isDefault = 1")
    WeeklyOff findDefault();

    @Query("SELECT w FROM WeeklyOff w WHERE w.isDefault = 1 AND w.id != :id")
    WeeklyOff findDefaultExcludeId(Integer id);
}

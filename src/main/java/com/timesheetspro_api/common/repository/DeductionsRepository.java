package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.deductions.Deductions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeductionsRepository extends JpaRepository<Deductions, Integer> {
    @Query("SELECT d FROM Deductions d WHERE d.companyEmployee.id=:id")
    List<Deductions> findByEmployeeId(Integer id);
}

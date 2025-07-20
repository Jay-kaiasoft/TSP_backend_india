package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.department.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("SELECT d FROM Department d WHERE d.companyDetails.id=:companyId")
    List<Department> findByCompanyId(Integer companyId);
}

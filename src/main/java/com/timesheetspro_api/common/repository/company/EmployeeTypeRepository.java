package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.employeeType.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Integer> {
}

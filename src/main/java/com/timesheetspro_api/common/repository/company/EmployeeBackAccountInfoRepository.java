package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.employeeBackAccountInfo.EmployeeBackAccountInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeBackAccountInfoRepository extends JpaRepository<EmployeeBackAccountInfo, Integer> {
    @Query("SELECT e FROM EmployeeBackAccountInfo e WHERE e.companyEmployee.employeeId=:id")
    EmployeeBackAccountInfo findAccountInfoById(int id);
}

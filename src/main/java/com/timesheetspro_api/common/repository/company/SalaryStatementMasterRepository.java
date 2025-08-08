package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.salaryStatementMaster.SalaryStatementMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryStatementMasterRepository extends JpaRepository<SalaryStatementMaster, Integer> {
    @Query("SELECT s FROM SalaryStatementMaster s WHERE s.companyDetails.id=:companyId")
    List<SalaryStatementMaster> findByCompanyId(int companyId);

    @Query("SELECT s FROM SalaryStatementMaster s WHERE s.companyDetails.id=:companyId AND s.month=:month AND s.year=:year")
    SalaryStatementMaster findByMonthAndYear(int companyId, int month, int year);
}

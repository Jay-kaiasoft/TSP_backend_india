package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.salaryStatementHistory.SalaryStatementHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryStatementHistoryRepository extends JpaRepository<SalaryStatementHistory, Integer>, JpaSpecificationExecutor<SalaryStatementHistory> {
    @Query("SELECT s FROM SalaryStatementHistory s WHERE s.employeeId=:id AND s.companyDetails.id=:companyId")
    SalaryStatementHistory findByEmployeeId(int id, int companyId);

    @Query("SELECT s FROM SalaryStatementHistory s WHERE s.departmentId=:id AND s.companyDetails.id=:companyId")
    List<SalaryStatementHistory> findByDepartmentId(int id, int companyId);

    @Query("SELECT s FROM SalaryStatementHistory s WHERE s.month=:month AND s.companyDetails.id=:companyId")
    SalaryStatementHistory findByMonth(int companyId, String month);

    @Query("SELECT s FROM SalaryStatementHistory s WHERE s.employeeId = :employeeId AND s.month = :month AND s.year = :year AND s.companyDetails.id = :companyId")
    SalaryStatementHistory isExites(@Param("companyId") int companyId,
                                    @Param("employeeId") int employeeId,
                                    @Param("month") Integer month,
                                    @Param("year") Integer year
                                    );

    @Query("""
            SELECT 
                COALESCE(SUM(s.netSalary), 0),
                COALESCE(SUM(s.totalPfAmount), 0),
                COALESCE(SUM(s.ptAmount), 0)
            FROM SalaryStatementHistory s
            WHERE s.month = :month 
              AND s.year = :year
              AND s.companyDetails.id = :companyId
            """)
    List<Object[]> getSalaryTotals(
            @Param("companyId") int companyId,
            @Param("month") int month,
            @Param("year") int year);

}
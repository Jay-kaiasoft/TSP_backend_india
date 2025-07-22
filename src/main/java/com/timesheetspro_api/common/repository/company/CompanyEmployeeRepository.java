package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee, Integer>, JpaSpecificationExecutor<CompanyEmployee> {
    @Query("SELECT c FROM CompanyEmployee c WHERE c.companyDetails.id=:id AND c.userName=:userName")
    CompanyEmployee findByCompanyNoAndUserName(int id, String userName);

    @Query("SELECT c FROM CompanyEmployee c WHERE c.companyDetails.companyNo=:companyNo AND c.id=:employeeId")
    CompanyEmployee findByCompanyNoAndEmployeeId(String companyNo, String employeeId);

    @Query("SELECT c FROM CompanyEmployee c WHERE c.companyDetails.id=:id")
    List<CompanyEmployee> findByCompanyId(int id);

    @Query("SELECT c FROM CompanyEmployee c WHERE c.companyDetails.id=:id AND c.isPf=true")
    List<CompanyEmployee> getReportByPF(int id);

    @Query("SELECT c FROM CompanyEmployee c WHERE c.companyDetails.id=:id AND c.isPt=true")
    List<CompanyEmployee> getReportByPT(int id);

    @Query("SELECT COUNT(c) FROM CompanyEmployee c WHERE companyDetails.id=:id")
    Long getCompanyTotalUserCount(int id);

    @Query("SELECT c FROM CompanyEmployee c WHERE c.companyDetails.companyNo=:companyNo AND c.userName=:userName AND c.email=:email")
    CompanyEmployee findByCompanyNoAndUserName(String companyNo, String userName, String email);

    @Query("SELECT c FROM CompanyEmployee c WHERE c.companyDetails.id=:id")
    List<CompanyEmployee> findAllContractors(int id);
}

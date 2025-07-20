package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyDetailsRepository extends JpaRepository<CompanyDetails, Integer>, JpaSpecificationExecutor<CompanyDetails> {
    @Query("SELECT c FROM CompanyDetails c WHERE c.companyNo=:companyNo")
    CompanyDetails findByCompanyNo(String companyNo);

    @Query("SELECT c FROM CompanyDetails c WHERE c.ein=:ein")
    CompanyDetails findByCompanyEin(String ein);

    @Query("SELECT c FROM CompanyDetails c WHERE c.companyName=:companyName AND c.ein=:ein")
    CompanyDetails findByCompanyName(String companyName, String ein);

    @Query("SELECT c FROM CompanyDetails c WHERE c.isActive=:active")
    List<CompanyDetails> findAllActiveCompany(int active);

    @Query(value = "SELECT * FROM company_details ORDER BY id DESC LIMIT 1", nativeQuery = true)
    CompanyDetails findLastCompany();

    @Query("SELECT c FROM CompanyDetails c WHERE c.id != :companyId AND c.companyName =:companyName AND c.ein=:ein")
    CompanyDetails findAllExceptCompany(@Param("companyId") int companyId, @Param("companyName") String companyName, String ein);

    @Query("SELECT c FROM CompanyDetails c WHERE c.id != :companyId AND c.ein=:ein")
    CompanyDetails findAllExceptCompanyByEin(@Param("companyId") int companyId, String ein);
}

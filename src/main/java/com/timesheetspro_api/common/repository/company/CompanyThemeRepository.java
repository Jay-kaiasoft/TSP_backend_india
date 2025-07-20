package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyTheme.CompanyTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyThemeRepository extends JpaRepository<CompanyTheme, Integer> {
    @Query("SELECT c FROM CompanyTheme c WHERE c.companyDetails.id=:id")
    CompanyTheme findByCompanyId(int id);
}

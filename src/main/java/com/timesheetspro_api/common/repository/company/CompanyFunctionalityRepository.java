package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyFunctionality.CompanyFunctionality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyFunctionalityRepository extends JpaRepository<CompanyFunctionality, Integer> {
}

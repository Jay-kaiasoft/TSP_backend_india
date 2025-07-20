package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyShift.CompanyShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyShiftRepository extends JpaRepository<CompanyShift, Integer> {

    @Query("SELECT c FROM CompanyShift c WHERE c.companyDetails.id=:companyId")
    List<CompanyShift> findByCompanyId(int companyId);
}

package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.overtimeRules.OvertimeRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OvertimeRulesRepository extends JpaRepository<OvertimeRules, Integer> {
    @Query("SELECT o FROM OvertimeRules o WHERE o.companyDetails.id=:id")
    List<OvertimeRules> findByCompanyId(int id);

    @Query("SELECT o FROM OvertimeRules o WHERE o.ruleName=:ruleName")
    OvertimeRules findByRuleName(String ruleName);

    @Query("SELECT o FROM OvertimeRules o WHERE o.id != :id AND o.ruleName =:ruleName")
    OvertimeRules findByRuleName(@Param("id") int id, @Param("ruleName") String ruleName);
}

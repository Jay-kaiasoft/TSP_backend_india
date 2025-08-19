package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.attendancePenaltyRules.AttendancePenaltyRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendancePenaltyRulesRepository extends JpaRepository<AttendancePenaltyRules, Integer> {
    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.companyDetails.id=:id")
    List<AttendancePenaltyRules> findByCompanyId(Integer id);

    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.ruleName =:ruleName AND a.companyDetails.id=:companyId")
    AttendancePenaltyRules findByCompanyIdAndName(@Param("ruleName") String ruleName, @Param("companyId") Integer companyId);

    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.minutes =:minutes AND a.companyDetails.id=:companyId")
    AttendancePenaltyRules findByCompanyIdAndMinutes(@Param("minutes") Integer minutes, @Param("companyId") Integer companyId);

    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.ruleName =:ruleName AND a.companyDetails.id=:companyId AND a.id!=:id")
    AttendancePenaltyRules findAllExceptByCompanyId(@Param("ruleName") String ruleName, @Param("id") Integer id, @Param("companyId") Integer companyId);

    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.minutes =:minutes AND a.companyDetails.id=:companyId AND a.id!=:id")
    AttendancePenaltyRules findAllExceptByCompanyIdWithMinutes(@Param("minutes") Integer minutes, @Param("id") Integer id, @Param("companyId") Integer companyId);
}

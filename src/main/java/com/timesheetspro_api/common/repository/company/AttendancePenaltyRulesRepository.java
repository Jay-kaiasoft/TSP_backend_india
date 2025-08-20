package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.attendancePenaltyRules.AttendancePenaltyRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendancePenaltyRulesRepository extends JpaRepository<AttendancePenaltyRules, Integer> {
    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.companyDetails.id=:id AND a.isEarlyExit=:flag")
    List<AttendancePenaltyRules> findByCompanyId(Integer id, @Param("flag") Boolean flag);

    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.ruleName =:ruleName AND a.companyDetails.id=:companyId AND a.isEarlyExit=:flag")
    AttendancePenaltyRules findByCompanyIdAndName(@Param("ruleName") String ruleName, @Param("companyId") Integer companyId, @Param("flag") Boolean flag);

    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.minutes =:minutes AND a.companyDetails.id=:companyId AND a.isEarlyExit=:flag")
    AttendancePenaltyRules findByCompanyIdAndMinutes(@Param("minutes") Integer minutes, @Param("companyId") Integer companyId, @Param("flag") Boolean flag);

    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.ruleName =:ruleName AND a.companyDetails.id=:companyId AND a.id!=:id AND a.isEarlyExit=:flag")
    AttendancePenaltyRules findAllExceptByCompanyId(@Param("ruleName") String ruleName, @Param("id") Integer id, @Param("companyId") Integer companyId,@Param("flag") Boolean flag);

    @Query("SELECT a FROM AttendancePenaltyRules a WHERE a.minutes =:minutes AND a.companyDetails.id=:companyId AND a.id!=:id AND a.isEarlyExit=:flag")
    AttendancePenaltyRules findAllExceptByCompanyIdWithMinutes(@Param("minutes") Integer minutes, @Param("id") Integer id, @Param("companyId") Integer companyId,@Param("flag") Boolean flag);
}

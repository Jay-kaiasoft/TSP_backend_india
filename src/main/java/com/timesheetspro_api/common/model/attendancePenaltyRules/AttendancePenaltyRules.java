package com.timesheetspro_api.common.model.attendancePenaltyRules;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "attendance_penalty_rules")
@Setter
@Getter
@NoArgsConstructor
public class AttendancePenaltyRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "rule_name")
    private String ruleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private CompanyEmployee companyEmployee;

    @Column(name = "minutes")
    private Integer minutes;

    @Column(name = "deduction_type")
    private String deductionType;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "count")
    private Integer count;

    @Column(name = "is_early_exit")
    private Boolean isEarlyExit;
}

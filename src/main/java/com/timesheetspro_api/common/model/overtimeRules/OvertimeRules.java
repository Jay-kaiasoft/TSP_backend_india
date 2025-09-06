package com.timesheetspro_api.common.model.overtimeRules;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "overtime_rules")
@Setter
@Getter
@NoArgsConstructor
public class OvertimeRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private CompanyEmployee companyEmployee;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "ot_minutes")
    private Integer otMinutes;

    @Column(name = "ot_amount")
    private Float otAmount;

    @Column(name = "ot_type")
    private String otType;
}

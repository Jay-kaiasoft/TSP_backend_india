package com.timesheetspro_api.common.model.employmentInfo;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "employment_info")
@Setter
@Getter
@NoArgsConstructor
public class EmploymentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "work_phone")
    private String workPhone;

    @Column(name = "ext")
    private String ext;

    @Column(name = "work_email")
    private String workEmail;

    @Column(name = "hire_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hireDate;

    @Column(name = "status")
    private String status;

    @Column(name = "paid_pension")
    private String paidPension;

    @Column(name = "statutory_employee")
    private String statutoryEmployee;

    @Column(name = "exclusion_indicator")
    private String exclusionIndicator;

    @Column(name = "key_employee_indicator")
    private String keyEmployeeIndicator;

    @Column(name = "union_indicator")
    private String unionIndicator;

    @Column(name = "hce")
    private String hce;

    @Column(name = "eligibility_indicator")
    private String eligibilityIndicator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private CompanyEmployee companyEmployee;
}

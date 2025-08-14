package com.timesheetspro_api.common.model.salaryStatementHistory;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "salary_statement_history")
@Setter
@Getter
@NoArgsConstructor
public class SalaryStatementHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;

    @Column(name = "clock_in_out_id")
    private Integer clockInOutId;

    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "basic_salary")
    private Integer basicSalary;

    @Column(name = "total_earn_salary")
    private Integer totalEarnSalary;

    @Column(name = "ot_amount")
    private Integer otAmount;

    @Column(name = "pf_amount", nullable = true)
    private Integer pfAmount;

    @Column(name = "pf_percentage", nullable = true)
    private Integer pfPercentage;

    @Column(name = "total_pf_amount")
    private Integer totalPfAmount;

    @Column(name = "pt_amount", nullable = true)
    private Integer ptAmount;;

    @Column(name = "total_earnings")
    private Integer totalEarnings;

    @Column(name = "total_deductions")
    private Integer totalDeductions;

    @Column(name = "other_deductions")
    private Integer otherDeductions;

    @Column(name = "net_salary")
    private Integer netSalary;

    @Column(name = "salary_month_and_year", nullable = true)
    private String monthYear;

    @Column(name = "salary_month", nullable = true)
    private Integer month;

    @Column(name = "salary_year", nullable = true)
    private Integer year;

    @Column(name = "total_paid_days")
    private Integer totalPaidDays;

    @Column(name = "working_days")
    private Integer totalWorkingDays;

    @Column(name = "total_days")
    private Integer totalDays;

    @Column(name = "note",nullable = true)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by", referencedColumnName = "id")
    private CompanyEmployee companyEmployee;

    @Column(name = "generated_date")
    @Temporal(TemporalType.DATE)
    private Date generatedDate;
}

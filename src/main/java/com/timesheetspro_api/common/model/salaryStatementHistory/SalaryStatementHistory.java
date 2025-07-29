package com.timesheetspro_api.common.model.salaryStatementHistory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "company_id")
    private Integer companyId;

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

    @Column(name = "month", nullable = true)
    private String month;
}

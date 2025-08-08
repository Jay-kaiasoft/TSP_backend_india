package com.timesheetspro_api.common.model.salaryStatementMaster;

import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "salary_statement_master")
@Setter
@Getter
@NoArgsConstructor
public class SalaryStatementMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "total_salary")
    private Integer totalSalary;

    @Column(name = "total_pf")
    private Integer totalPf;

    @Column(name = "total_pt")
    private Integer totalPt;

    @Column(name = "note")
    private String note;
}

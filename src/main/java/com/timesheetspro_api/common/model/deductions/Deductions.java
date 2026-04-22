package com.timesheetspro_api.common.model.deductions;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deductions")
@Setter
@Getter
@NoArgsConstructor
public class Deductions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id ", unique = true, nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private CompanyEmployee companyEmployee;

    @Column(name="type", length=250)
    private String type;

    @Column(name="label", length=250)
    private String label;

    @Column(name="amount")
    private Integer amount;
}

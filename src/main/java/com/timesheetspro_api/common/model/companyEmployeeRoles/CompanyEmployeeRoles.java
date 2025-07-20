package com.timesheetspro_api.common.model.companyEmployeeRoles;

import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_employee_roles")
@Setter
@Getter
@NoArgsConstructor
public class CompanyEmployeeRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;

    @Column(name = "role_name", columnDefinition = "Char")
    private String roleName;
}

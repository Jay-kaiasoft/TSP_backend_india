package com.timesheetspro_api.common.model.companyDetails;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "company_details")
@Setter
@Getter
@NoArgsConstructor
public class CompanyDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "company_no")
    private String companyNo;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "DBA")
    private String dba;

    @Column(name = "company_logo")
    private String companyLogo;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "industry_name")
    private String industryName;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "is_active")
    private int isActive;

    @Column(name = "register_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    @Column(name = "EIN")
    private String ein;

    @Column(name = "organization_type")
    private String organizationType;

    @OneToMany(mappedBy = "companyDetails", fetch = FetchType.LAZY)
    private List<CompanyEmployee> companyEmployees;

}

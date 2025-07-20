package com.timesheetspro_api.common.model.companyFunctionality;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_functionality")
@Setter
@Getter
@NoArgsConstructor
public class CompanyFunctionality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "functionality_name", nullable = false)
    private String functionalityName;
}

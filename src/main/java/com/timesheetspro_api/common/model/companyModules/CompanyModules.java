package com.timesheetspro_api.common.model.companyModules;

import com.timesheetspro_api.common.model.companyFunctionality.CompanyFunctionality;
import com.timesheetspro_api.common.model.companyModuleActions.CompanyModuleActions;
import com.timesheetspro_api.common.model.functionality.Functionality;
import com.timesheetspro_api.common.model.moduleActions.ModuleActions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "company_modules")
@Setter
@Getter
@NoArgsConstructor
public class CompanyModules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int moduleId;

    @Column(name = "module_name")
    private String moduleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionality_id", referencedColumnName = "id", columnDefinition = "NUMBER")
    private CompanyFunctionality functionality;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    private Set<CompanyModuleActions> moduleActions;
}

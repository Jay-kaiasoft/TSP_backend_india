package com.timesheetspro_api.common.model.companyModuleActions;


import com.timesheetspro_api.common.model.companyActions.CompanyActions;
import com.timesheetspro_api.common.model.companyModules.CompanyModules;
import com.timesheetspro_api.common.model.companyRoleModuleActions.CompanyRoleModuleActions;
import com.timesheetspro_api.common.model.module.Module;
import com.timesheetspro_api.common.model.roleModuleActions.RoleModuleActions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "company_module_actions")
@Setter
@Getter
@NoArgsConstructor
public class CompanyModuleActions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int moduleActionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id", columnDefinition = "number")
    private CompanyModules module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", referencedColumnName = "id", columnDefinition = "number")
        private CompanyActions action;

    @OneToMany(mappedBy = "moduleActions", fetch = FetchType.LAZY)
    private Set<CompanyRoleModuleActions> roleModuleActions;
}

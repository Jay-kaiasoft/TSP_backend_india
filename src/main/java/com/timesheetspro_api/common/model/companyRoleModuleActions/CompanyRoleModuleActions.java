package com.timesheetspro_api.common.model.companyRoleModuleActions;


import com.timesheetspro_api.common.model.companyEmployeeRoles.CompanyEmployeeRoles;
import com.timesheetspro_api.common.model.companyModuleActions.CompanyModuleActions;
import com.timesheetspro_api.common.model.moduleActions.ModuleActions;
import com.timesheetspro_api.common.model.roles.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_role_module_actions")
@Setter
@Getter
@NoArgsConstructor
public class CompanyRoleModuleActions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int roleActionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id", columnDefinition = "NUMBER")
    private CompanyEmployeeRoles role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_action_Id", referencedColumnName = "id", columnDefinition = "NUMBER")
    private CompanyModuleActions moduleActions;
}

package com.timesheetspro_api.common.model.roleModuleActions;

import com.timesheetspro_api.common.model.moduleActions.ModuleActions;
import com.timesheetspro_api.common.model.roles.Roles;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role_module_actions")
@Setter
@Getter
@NoArgsConstructor
public class RoleModuleActions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_action_Id", unique = true, nullable = false)
    private Long roleActionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", columnDefinition = "NUMBER")
    private Roles role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_action_Id", referencedColumnName = "module_action_Id", columnDefinition = "NUMBER")
    private ModuleActions moduleActions;
}

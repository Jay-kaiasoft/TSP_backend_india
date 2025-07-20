package com.timesheetspro_api.common.model.moduleActions;

import com.timesheetspro_api.common.model.actions.Actions;
import com.timesheetspro_api.common.model.module.Module;
import com.timesheetspro_api.common.model.roleModuleActions.RoleModuleActions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "module_actions")
@Setter
@Getter
@NoArgsConstructor
public class ModuleActions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_action_Id", unique = true, nullable = false)
    private Long moduleActionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "module_Id", columnDefinition = "number")
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", referencedColumnName = "action_id", columnDefinition = "number")
    private Actions action;

    @OneToMany(mappedBy = "moduleActions", fetch = FetchType.LAZY)
    private Set<RoleModuleActions> roleModuleActions;
}

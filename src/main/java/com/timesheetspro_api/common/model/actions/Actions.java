package com.timesheetspro_api.common.model.actions;

import com.timesheetspro_api.common.model.moduleActions.ModuleActions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "actions")
@Setter
@Getter
@NoArgsConstructor
public class Actions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_Id", unique = true, nullable = false)
    private Long actionId;

    @Column(name = "action_name")
    private String actionName;

    @OneToMany(mappedBy = "action")
    private Set<ModuleActions> moduleActionsSet;
}

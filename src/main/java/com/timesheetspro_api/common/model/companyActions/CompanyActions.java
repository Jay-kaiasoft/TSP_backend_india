package com.timesheetspro_api.common.model.companyActions;

import com.timesheetspro_api.common.model.companyModuleActions.CompanyModuleActions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "company_actions")
@Setter
@Getter
@NoArgsConstructor
public class CompanyActions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int actionId;

    @Column(name = "action_name")
    private String actionName;

    @OneToMany(mappedBy = "action")
    private Set<CompanyModuleActions> moduleActionsSet;
}

package com.timesheetspro_api.common.model.module;

import com.timesheetspro_api.common.model.functionality.Functionality;
import com.timesheetspro_api.common.model.moduleActions.ModuleActions;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "module")
@Setter
@Getter
@NoArgsConstructor
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_Id", unique = true, nullable = false)
    private Long moduleId;

    @Column(name = "module_name")
    private String moduleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionality_id", referencedColumnName = "id", columnDefinition = "NUMBER")
    private Functionality functionality;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    private Set<ModuleActions> moduleActions;
}

package com.timesheetspro_api.common.model.roles;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Setter
@Getter
@NoArgsConstructor
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_Id", unique = true, nullable = false)
    private Long roleId;

    @Column(name = "role_name", columnDefinition = "Char")
    private String roleName;
}

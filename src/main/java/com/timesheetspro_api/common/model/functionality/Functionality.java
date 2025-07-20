package com.timesheetspro_api.common.model.functionality;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "functionality")
@Setter
@Getter
@NoArgsConstructor
public class Functionality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "functionality_name", nullable = false)
    private String functionalityName;
}

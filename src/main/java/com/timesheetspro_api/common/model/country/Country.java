package com.timesheetspro_api.common.model.country;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_country")
@Setter
@Getter
@NoArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "iso2", length = 2)
    private Character iso2;

    @Column(name = "cnt_name")
    private String cntName;

    @Column(name = "long_name")
    private String longName;

    @Column(name = "oid")
    private int oid;

    @Column(name = "cnt_code")
    private String cntCode;

    @Column(name = "phone_min_length")
    private int phoneMinLength;

    @Column(name = "phone_max_length")
    private int phoneMaxLength;
}

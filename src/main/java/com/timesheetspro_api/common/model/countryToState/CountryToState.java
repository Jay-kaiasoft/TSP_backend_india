package com.timesheetspro_api.common.model.countryToState;

import com.timesheetspro_api.common.model.country.Country;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "country_to_state")
@Setter
@Getter
@NoArgsConstructor
public class CountryToState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_to_state_id ", unique = true, nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_country_id", referencedColumnName = "id")
    private Country country;

    @Column(name="state_capital", length=100)
    private String stateCapital;

    @Column(name="state_long",columnDefinition = "char", length=100)
    private String stateLong;

    @Column(name="state_short",columnDefinition = "char", length=10)
    private String stateShort;
}

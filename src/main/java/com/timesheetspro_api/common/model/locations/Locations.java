package com.timesheetspro_api.common.model.locations;

import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "locations")
@Setter
@Getter
@NoArgsConstructor
public class Locations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "time_zone")
    private String TimeZone;

    @Column(name = "city")
    private String City;

    @Column(name = "state")
    private String State;

    @Column(name = "country")
    private String Country;

    @Column(name = "address1")
    private String Address1;

    @Column(name = "address2")
    private String Address2;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "employee_count")
    private String employeeCount;

    @Column(name = "radar_external_id")
    private String externalId;

    @Column(name = "geofence_Id")
    private String geofenceId;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "pay_period")
    private Integer payPeriod;

    @Column(name = "pay_period_start")
    @Temporal(TemporalType.DATE)
    private Date payPeriodStart;

    @Column(name = "pay_period_end")
    @Temporal(TemporalType.DATE)
    private Date payPeriodEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;
}

package com.timesheetspro_api.common.model.UserInOut;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.locations.Locations;
import com.timesheetspro_api.common.model.users.Users;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Table(name = "user_inout")
public class UserInOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "time_in")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeIn;

    @Column(name = "time_out")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeOut;

    @Column(name = "created_on")
    @Temporal(TemporalType.DATE)
    private Date createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private CompanyEmployee user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Locations locations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;

    @Column(name = "is_salary_generate")
    private Integer isSalaryGenerate;

}

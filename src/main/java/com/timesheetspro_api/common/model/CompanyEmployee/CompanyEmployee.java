package com.timesheetspro_api.common.model.CompanyEmployee;

import com.timesheetspro_api.common.FloatArrayToJsonConverter;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyEmployeeRoles.CompanyEmployeeRoles;
import com.timesheetspro_api.common.model.companyShift.CompanyShift;
import com.timesheetspro_api.common.model.department.Department;
import com.timesheetspro_api.common.model.employeeType.EmployeeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Entity
@Table(name = "company_employees")
@Setter
@Getter
@NoArgsConstructor
public class CompanyEmployee implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private CompanyEmployeeRoles roles;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "hourly_rate")
    private Float hourlyRate;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "gender")
    private String gender;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dob;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "emergencyContact")
    private String emergencyContact;

    @Column(name = "contactPhone")
    private String contactPhone;

    @Column(name = "relationship")
    private String relationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", referencedColumnName = "id")
    private CompanyShift companyShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_type", referencedColumnName = "id")
    private EmployeeType employeeType;

    @Column(name = "pay_period")
    private String payPeriod;

    @Column(name = "hired_date")
    @Temporal(TemporalType.DATE)
    private Date hiredDate;

    @Column(name = "is_active")
    private int isActive;

    @Column(name = "is_contractor")
    private int isContractor;

    @Column(name = "ext")
    private String ext;

    @Column(name = "work_state")
    private String workState;

    @Column(name = "work_location")
    private String workLocation;

    @Column(name = "company_location")
    private String companyLocation;

    @Column(name = "check_geofence")
    private Integer checkGeofence = 1;

    @Column(name = "embedding", columnDefinition = "JSON")
    @Convert(converter = FloatArrayToJsonConverter.class)
    private float[] embedding;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "aadhar_image")
    private String aadharImage;

    @Column(name = "is_pf")
    private Boolean isPf;

    @Column(name = "pf_percentage")
    private Integer pfPercentage;

    @Column(name = "is_pt")
    private Boolean isPt;

    @Column(name = "pt_amount")
    private Integer ptAmount;

    @Column(name = "pf_amount")
    private Integer pfAmount;

    @Column(name = "basic_salary")
    private Integer basicSalary;

    @Column(name = "gross_salary")
    private Integer grossSalary;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.userName;
    }
}

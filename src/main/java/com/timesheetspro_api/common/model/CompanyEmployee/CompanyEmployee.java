package com.timesheetspro_api.common.model.CompanyEmployee;

import com.timesheetspro_api.common.FloatArrayToJsonConverter;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyEmployeeRoles.CompanyEmployeeRoles;
import com.timesheetspro_api.common.model.companyShift.CompanyShift;
import com.timesheetspro_api.common.model.department.Department;
import com.timesheetspro_api.common.model.employeeType.EmployeeType;
import com.timesheetspro_api.common.model.overtimeRules.OvertimeRules;
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

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone")
    private String phone;

    @Column(name = "emergency_phone", nullable = true)
    private String emergencyPhone;

    @Column(name = "alt_phone", nullable = true)
    private String altPhone;

    @Column(name = "profile_image", nullable = true)
    private String profileImage;

    @Column(name = "city", nullable = true)
    private String city;

    @Column(name = "state", nullable = true)
    private String state;

    @Column(name = "country", nullable = true)
    private String country;

    @Column(name = "hourly_rate", nullable = true)
    private Float hourlyRate;

    @Column(name = "address1", nullable = true)
    private String address1;

    @Column(name = "address2", nullable = true)
    private String address2;

    @Column(name = "gender", nullable = true)
    private String gender;

    @Column(name = "zip_code", nullable = true)
    private String zipCode;

    @Column(name = "dob", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dob;

    @Column(name = "middle_name", nullable = true)
    private String middleName;

    @Column(name = "emergencyContact", nullable = true)
    private String emergencyContact;

    @Column(name = "contactPhone", nullable = true)
    private String contactPhone;

    @Column(name = "relationship", nullable = true)
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

    @Column(name = "pay_period", nullable = true)
    private String payPeriod;

    @Column(name = "hired_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date hiredDate;

    @Column(name = "is_active", nullable = true)
    private int isActive;

    @Column(name = "company_location", nullable = true)
    private String companyLocation;

    @Column(name = "check_geofence")
    private Integer checkGeofence = 1;

    @Column(name = "embedding", columnDefinition = "JSON")
    @Convert(converter = FloatArrayToJsonConverter.class)
    private float[] embedding;

    @Column(name = "blood_group", nullable = true)
    private String bloodGroup;

    @Column(name = "aadhar_image", nullable = true)
    private String aadharImage;

    @Column(name = "is_pf", nullable = true)
    private Boolean isPf;

    @Column(name = "pf_type", nullable = true)
    private String pfType;

    @Column(name = "pf_percentage", nullable = true)
    private Integer pfPercentage;

    @Column(name = "is_pt", nullable = true)
    private Boolean isPt;

    @Column(name = "pt_amount", nullable = true)
    private Integer ptAmount;

    @Column(name = "pf_amount", nullable = true)
    private Integer pfAmount;

    @Column(name = "basic_salary", nullable = true)
    private Integer basicSalary;

    @Column(name = "gross_salary", nullable = true)
    private Integer grossSalary;

    @Column(name = "canteen_type", nullable = true)
    private String canteenType;

    @Column(name = "canteen_amount", nullable = true)
    private Integer canteenAmount;

    @Column(name = "lunch_break", nullable = true)
    private Integer lunchBreak;

    @Column(name = "working_hours_include_lunch", nullable = true)
    private Integer workingHoursIncludeLunch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ot_id ", referencedColumnName = "id")
    private OvertimeRules overtimeRules;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.userName;
    }
}

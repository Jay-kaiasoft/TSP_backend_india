package com.timesheetspro_api.common.model.users;

import com.timesheetspro_api.common.model.contractor.Contractor;
import com.timesheetspro_api.common.model.department.Department;
import com.timesheetspro_api.common.model.roles.Roles;
import com.timesheetspro_api.common.model.userShift.UserShift;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor
public class Users implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_Id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "password")
    private String password;

    @Column(name = "personalIdentificationNumber")
    private String personalIdentificationNumber;

    @Column(name = "gender")
    private String gender;

    @Column(name = "hourly_rate")
    private Long hourlyRate;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "city")
    private String city;

    @Column(name = "zipCode")
    private String zipCode;

    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;

    @Column(name = "birthDate")
    private String birthDate;

    @Column(name = "emergencyContact")
    private String emergencyContact;

    @Column(name = "contactPhone")
    private String contactPhone;

    @Column(name = "relationship")
    private String relationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Roles role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_shift_id", referencedColumnName = "id")
    private UserShift userShift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id", referencedColumnName = "id")
    private Contractor contractor;

    @Column(name = "profile_img")
    private String profileImage;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "userName")
    private String userName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return this.userName;
    }
}

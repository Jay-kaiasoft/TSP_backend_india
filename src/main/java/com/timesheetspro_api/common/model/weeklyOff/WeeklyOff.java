package com.timesheetspro_api.common.model.weeklyOff;

import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weekly_off")
@Setter
@Getter
@NoArgsConstructor
public class WeeklyOff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_default", columnDefinition = "INT DEFAULT 0")
    private Integer isDefault = 0;

    @Column(name = "sunday_all")
    private boolean sundayAll;

    @Column(name = "sunday_1st")
    private boolean sunday1st;

    @Column(name = "sunday_2nd")
    private boolean sunday2nd;

    @Column(name = "sunday_3rd")
    private boolean sunday3rd;

    @Column(name = "sunday_4th")
    private boolean sunday4th;

    @Column(name = "sunday_5th")
    private boolean sunday5th;

    // Monday flags
    @Column(name = "monday_all")
    private boolean mondayAll;

    @Column(name = "monday_1st")
    private boolean monday1st;

    @Column(name = "monday_2nd")
    private boolean monday2nd;

    @Column(name = "monday_3rd")
    private boolean monday3rd;

    @Column(name = "monday_4th")
    private boolean monday4th;

    @Column(name = "monday_5th")
    private boolean monday5th;

    // Tuesday flags
    @Column(name = "tuesday_all")
    private boolean tuesdayAll;

    @Column(name = "tuesday_1st")
    private boolean tuesday1st;

    @Column(name = "tuesday_2nd")
    private boolean tuesday2nd;

    @Column(name = "tuesday_3rd")
    private boolean tuesday3rd;

    @Column(name = "tuesday_4th")
    private boolean tuesday4th;

    @Column(name = "tuesday_5th")
    private boolean tuesday5th;

    // Wednesday flags
    @Column(name = "wednesday_all")
    private boolean wednesdayAll;

    @Column(name = "wednesday_1st")
    private boolean wednesday1st;

    @Column(name = "wednesday_2nd")
    private boolean wednesday2nd;

    @Column(name = "wednesday_3rd")
    private boolean wednesday3rd;

    @Column(name = "wednesday_4th")
    private boolean wednesday4th;

    @Column(name = "wednesday_5th")
    private boolean wednesday5th;

    // Thursday flags
    @Column(name = "thursday_all")
    private boolean thursdayAll;

    @Column(name = "thursday_1st")
    private boolean thursday1st;

    @Column(name = "thursday_2nd")
    private boolean thursday2nd;

    @Column(name = "thursday_3rd")
    private boolean thursday3rd;

    @Column(name = "thursday_4th")
    private boolean thursday4th;

    @Column(name = "thursday_5th")
    private boolean thursday5th;

    // Friday flags
    @Column(name = "friday_all")
    private boolean fridayAll;

    @Column(name = "friday_1st")
    private boolean friday1st;

    @Column(name = "friday_2nd")
    private boolean friday2nd;

    @Column(name = "friday_3rd")
    private boolean friday3rd;

    @Column(name = "friday_4th")
    private boolean friday4th;

    @Column(name = "friday_5th")
    private boolean friday5th;

    // Saturday flags
    @Column(name = "saturday_all")
    private boolean saturdayAll;

    @Column(name = "saturday_1st")
    private boolean saturday1st;

    @Column(name = "saturday_2nd")
    private boolean saturday2nd;

    @Column(name = "saturday_3rd")
    private boolean saturday3rd;

    @Column(name = "saturday_4th")
    private boolean saturday4th;

    @Column(name = "saturday_5th")
    private boolean saturday5th;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private CompanyEmployee companyEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;
}

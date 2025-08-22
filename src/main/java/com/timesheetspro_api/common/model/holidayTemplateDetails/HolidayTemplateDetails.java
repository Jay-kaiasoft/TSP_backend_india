package com.timesheetspro_api.common.model.holidayTemplateDetails;

import com.timesheetspro_api.common.model.holidayTemplates.HolidayTemplates;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "holiday_template_details")
@Setter
@Getter
@NoArgsConstructor
public class HolidayTemplateDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_template_id", referencedColumnName = "id")
    private HolidayTemplates holidayTemplates;
}

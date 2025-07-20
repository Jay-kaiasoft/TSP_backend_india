package com.timesheetspro_api.common.model.companyTheme;


import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_theme")
@Setter
@Getter
@NoArgsConstructor
public class CompanyTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private CompanyDetails companyDetails;

    @Column(name = "primary_color")
    private String primaryColor;

    @Column(name = "side_navigation_bg_color")
    private String sideNavigationBgColor;

    @Column(name = "content_bg_color")
    private String contentBgColor;

    @Column(name = "content_bg_color2")
    private String contentBgColor2;

    @Column(name = "header_bg_color")
    private String headerBgColor;

    @Column(name = "text_color")
    private String textColor;

    @Column(name = "icon_color")
    private String iconColor;
}

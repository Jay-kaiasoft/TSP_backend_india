package com.timesheetspro_api.companyTheme.serviceImpl;

import com.timesheetspro_api.common.dto.companyTheme.CompanyThemeDto;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyTheme.CompanyTheme;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyThemeRepository;
import com.timesheetspro_api.companyTheme.service.CompanyThemeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "companyThemeService")
public class CompanyThemeServiceImpl implements CompanyThemeService {

    @Autowired
    private CompanyThemeRepository companyThemeRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Override
    public CompanyThemeDto getAllTheme(int id) {
        try {
            CompanyTheme companyThemes = this.companyThemeRepository.findByCompanyId(id);
            CompanyThemeDto companyThemeDtos = new CompanyThemeDto();

            BeanUtils.copyProperties(this.getTheme(companyThemes.getId()), companyThemes);
            return companyThemeDtos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyThemeDto getTheme(int id) {
        try {
            CompanyTheme companyTheme = this.companyThemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Theme not found"));
            CompanyThemeDto companyThemeDto = new CompanyThemeDto();
            companyThemeDto.setCompanyId(companyTheme.getCompanyDetails().getId());
            BeanUtils.copyProperties(companyTheme, companyThemeDto);
            return companyThemeDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyThemeDto createTheme(CompanyThemeDto companyThemeDto) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyThemeDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            CompanyTheme companyTheme = new CompanyTheme();
            companyTheme.setCompanyDetails(companyDetails);
            BeanUtils.copyProperties(companyThemeDto, companyTheme);
            this.companyThemeRepository.save(companyTheme);
            return companyThemeDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyThemeDto updateTheme(int id, CompanyThemeDto companyThemeDto) {
        try {
            CompanyTheme companyTheme = this.companyThemeRepository.findByCompanyId(companyThemeDto.getCompanyId());
            if (companyTheme != null) {
                if (companyThemeDto.getType().equals("setColor")) {
                    companyTheme.setPrimaryColor(companyThemeDto.getPrimaryColor());
                } else if (companyThemeDto.getType().equals("setSideNavigationBgColor")) {
                    companyTheme.setSideNavigationBgColor(companyThemeDto.getSideNavigationBgColor());
                } else if (companyThemeDto.getType().equals("setHeaderBgColor")) {
                    companyTheme.setHeaderBgColor(companyThemeDto.getHeaderBgColor());
                } else if (companyThemeDto.getType().equals("setContentBgColor")) {
                    companyTheme.setContentBgColor(companyThemeDto.getContentBgColor());
                } else if (companyThemeDto.getType().equals("setIconColor")) {
                    companyTheme.setIconColor(companyThemeDto.getIconColor());
                } else if (companyThemeDto.getType().equals("setTextColor")) {
                    companyTheme.setTextColor(companyThemeDto.getTextColor());
                } else {
                    BeanUtils.copyProperties(companyThemeDto, companyTheme, "id");
                }
                this.companyThemeRepository.save(companyTheme);
                return companyThemeDto;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteTheme(int id) {
        try {
            CompanyTheme companyTheme = this.companyThemeRepository.findById(id).orElseThrow(() -> new RuntimeException("Theme not found"));
            this.companyThemeRepository.delete(companyTheme);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

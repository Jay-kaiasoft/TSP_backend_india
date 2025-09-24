package com.timesheetspro_api.companyDetails.serviceImpl;

import com.timesheetspro_api.common.dto.CompanyEmployeeDto.CompanyEmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeDto.EmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesDto;
import com.timesheetspro_api.common.dto.companyDetails.CompanyDetailsDto;
import com.timesheetspro_api.common.dto.companyTheme.CompanyThemeDto;
import com.timesheetspro_api.common.dto.location.LocationDto;
import com.timesheetspro_api.common.exception.GlobalException;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyEmployeeRoles.CompanyEmployeeRoles;
import com.timesheetspro_api.common.model.companyTheme.CompanyTheme;
import com.timesheetspro_api.common.model.locations.Locations;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.CompanyThemeRepository;
import com.timesheetspro_api.common.repository.company.LocationsRepository;
import com.timesheetspro_api.common.repository.RolesRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.common.specification.CompanySpecification;
import com.timesheetspro_api.companyDetails.service.CompanyDetailsService;
import com.timesheetspro_api.companyEmployee.service.CompanyEmployeeService;
import com.timesheetspro_api.companyEmployeeRole.service.CompanyEmployeeRoleService;
import com.timesheetspro_api.companyTheme.service.CompanyThemeService;
import com.timesheetspro_api.locations.service.LocationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service(value = "companyDetailsService")
public class CompanyDetailsServiceImpl implements CompanyDetailsService {

    @Autowired
    private CommonService commonService;

    @Autowired
    private CompanyEmployeeService companyEmployeeService;

    @Autowired
    private CompanyEmployeeRoleService companyEmployeeRoleService;

    @Value("${timeSheetProDrive}")
    String FILE_DIRECTORY;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    private CompanyThemeRepository companyThemeRepository;

    @Override
    public List<Map<String, Object>> searchCompanies(String name, int active) {
        try {
            List<Map<String, Object>> simplifiedList = new ArrayList<>();

            // Always search by name
            Specification<CompanyDetails> spec = CompanySpecification.searchByName(name);

            // Add active filter only if active is 0 or 1
            if (active == 0 || active == 1) {
                spec = spec.and(CompanySpecification.isActive(active == 1));
            }

            List<CompanyDetails> companyDetailsList = this.companyDetailsRepository.findAll(spec);

            for (CompanyDetails companyDetails : companyDetailsList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", companyDetails.getId());
                map.put("companyName", companyDetails.getCompanyName());
                map.put("companyLogo", companyDetails.getCompanyLogo());
                simplifiedList.add(map);
            }

            return simplifiedList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllCompanyDetails(int active) {
        try {
            List<Map<String, Object>> companyDetailsDtoList = new ArrayList<>();
            List<CompanyDetails> companyDetailsList = new ArrayList<>();

            if (active == 2) {
                companyDetailsList = this.companyDetailsRepository.findAll();
            } else {
                companyDetailsList = this.companyDetailsRepository.findAllActiveCompany(active);
            }
            if (!companyDetailsList.isEmpty()) {
                for (CompanyDetails companyDetails : companyDetailsList) {
                    CompanyDetailsDto companyDetailsDto = this.getCompanyDetails(companyDetails.getId());
                    Map<String, Object> objectMap = new HashMap<>();
                    objectMap.put("id", companyDetailsDto.getId());
                    objectMap.put("companyName", companyDetailsDto.getCompanyName());
                    objectMap.put("companyLogo", companyDetailsDto.getCompanyLogo());
                    companyDetailsDtoList.add(objectMap);
                }
            }
            return companyDetailsDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyDetailsDto getCompanyDetails(Integer id) {
        try {
            CompanyDetailsDto companyDetailsDto = new CompanyDetailsDto();
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Company details not found"));
            List<LocationDto> locationsDtoList = new ArrayList<>();

            companyDetailsDto.setId(companyDetails.getId());
            companyDetailsDto.setCompanyNo(companyDetails.getCompanyNo());
            companyDetailsDto.setCompanyName(companyDetails.getCompanyName());
            companyDetailsDto.setEin(companyDetails.getEin());
            companyDetailsDto.setOrganizationType(companyDetails.getOrganizationType());

            companyDetailsDto.setDba(companyDetails.getDba());
            companyDetailsDto.setEmail(companyDetails.getEmail());
            companyDetailsDto.setIndustryName(companyDetails.getIndustryName());
            companyDetailsDto.setPhone(companyDetails.getPhone());
            companyDetailsDto.setWebsiteUrl(companyDetails.getWebsiteUrl());
            companyDetailsDto.setRegisterDate(this.commonService.convertDateToString(companyDetails.getRegisterDate()));
            companyDetailsDto.setCompanyLogo(companyDetails.getCompanyLogo());
            List<Locations> locationsList = this.locationsRepository.findByCompanyId(id);
            if (!locationsList.isEmpty()) {
                for (Locations locations : locationsList) {
                    LocationDto locationDto = new LocationDto();
                    locationDto.setCompanyId(locations.getCompanyDetails().getId());
                    locationDto.setId(locations.getId());
                    locationDto.setCity(locations.getCity());
                    locationDto.setState(locations.getState());
                    locationDto.setCountry(locations.getCountry());
//                    locationDto.setTimeZone(locations.getTimeZone());
                    locationDto.setAddress1(locations.getAddress1());
                    locationDto.setAddress2(locations.getAddress2());
                    locationDto.setEmployeeCount(locations.getEmployeeCount());
                    locationDto.setLocationName(locations.getLocationName());
                    locationDto.setExternalId(locations.getExternalId());
                    locationDto.setZipCode(locations.getZipCode());
                    locationDto.setGeofenceId(locations.getGeofenceId());
                    locationDto.setIsActive(locations.getIsActive());
                    locationsDtoList.add(locationDto);
                }
            }
            companyDetailsDto.setLocations(locationsDtoList);

            return companyDetailsDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyDetailsDto createCompanyDetails(CompanyDetailsDto companyDetailsDto, String step) {
        try {
            if (step.equals("1")) {
                CompanyDetails companyDetails = new CompanyDetails();
                CompanyDetails isExits = this.companyDetailsRepository.findByCompanyName(companyDetailsDto.getCompanyName(), companyDetailsDto.getEin());
                if (isExits != null) {
                    throw new GlobalException("" + companyDetailsDto.getCompanyName() + " is already registered");
                }
                CompanyDetails isEinExits = this.companyDetailsRepository.findByCompanyEin(companyDetailsDto.getEin());
                if (isEinExits != null) {
                    throw new GlobalException("GST number " + companyDetailsDto.getEin() + " is already registered");
                }
                companyDetails.setIsActive(1);
                Date currentDate = new Date();
                companyDetails.setRegisterDate(currentDate);
                BeanUtils.copyProperties(companyDetailsDto, companyDetails);
                this.companyDetailsRepository.save(companyDetails);
                companyDetailsDto.setId(companyDetails.getId());

                CompanyTheme companyTheme = new CompanyTheme();
                companyTheme.setCompanyDetails(companyDetails);
                companyTheme.setPrimaryColor("#666cff");
                companyTheme.setTextColor("#262b43");
                companyTheme.setSideNavigationBgColor("#ffffff");
                companyTheme.setHeaderBgColor("#ffffff");
                companyTheme.setContentBgColor("#F5F5F7");
                companyTheme.setIconColor("#262b43");

                this.companyThemeRepository.save(companyTheme);
                return companyDetailsDto;
            } else {
                throw new GlobalException("Server Error");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyDetailsDto updateCompanyDetails(Integer id, CompanyDetailsDto companyDetailsDto, String step) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Company details not found"));
            if (step.equals("1")) {
                CompanyDetails isExits = this.companyDetailsRepository.findAllExceptCompany(id, companyDetailsDto.getCompanyName(), companyDetailsDto.getEin());
                if (isExits != null) {
                    throw new GlobalException("" + companyDetailsDto.getCompanyName() + " is already registered");
                }
                CompanyDetails isEinExits = this.companyDetailsRepository.findAllExceptCompanyByEin(id, companyDetailsDto.getEin());
                if (isEinExits != null) {
                    throw new GlobalException("GST number " + companyDetailsDto.getEin() + " is already registered");
                }
                companyDetails.setIsActive(1);
                BeanUtils.copyProperties(companyDetailsDto, companyDetails, "id");
                this.companyDetailsRepository.save(companyDetails);
                companyDetailsDto.setId(companyDetails.getId());

                return companyDetailsDto;
            } else if (step.equals("3")) {
                if (!companyDetailsDto.getDeletedEmployeeId().isEmpty()) {
                    for (int employeeId : companyDetailsDto.getDeletedEmployeeId()) {
                        CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
                        this.companyEmployeeRepository.delete(companyEmployee);
                    }
                }
                if (!companyDetailsDto.getEmployees().isEmpty()) {
                    List<CompanyEmployeeRolesDto> companyEmployeeRolesList = this.companyEmployeeRoleService.getAllRolesByCompanyId(companyDetails.getId());
                    CompanyEmployeeRolesDto roles = null;

                    if (companyEmployeeRolesList.isEmpty()) {
                        for (CompanyEmployeeDto companyEmployeeDto : companyDetailsDto.getEmployees()) {
                            // ====================== add employee role =================
                            for (CompanyEmployeeRolesDto companyEmployeeRolesDto : companyDetailsDto.getRoles()) {
                                CompanyEmployeeRolesDto rolesDto = new CompanyEmployeeRolesDto();
                                if (!companyEmployeeRolesDto.getRoleName().equals(companyEmployeeDto.getRoleName())) {
                                    rolesDto.setCompanyId(companyDetails.getId());
                                    rolesDto.setRoleName(companyEmployeeRolesDto.getRoleName());
                                    rolesDto.setRolesActions(companyEmployeeRolesDto.getRolesActions());
                                    this.companyEmployeeRoleService.createRole(rolesDto);
                                }
                            }
                            CompanyEmployeeRolesDto companyEmployeeRolesDto = new CompanyEmployeeRolesDto();
                            companyEmployeeRolesDto.setCompanyId(id);
                            companyEmployeeRolesDto.setRoleName(companyEmployeeDto.getRoleName());
                            companyEmployeeRolesDto.setRolesActions(companyDetailsDto.getRoles().get(companyDetailsDto.getRoles().size() - 1).getRolesActions());
                            roles = this.companyEmployeeRoleService.createRole(companyEmployeeRolesDto);
                            // ====================== add employee =================
                            EmployeeDto newCompanyEmployeeDto = new EmployeeDto();
                            newCompanyEmployeeDto.setCompanyId(id);
                            newCompanyEmployeeDto.setRoleId(roles.getRoleId());
                            newCompanyEmployeeDto.setFirstName(companyEmployeeDto.getFirstName());
                            newCompanyEmployeeDto.setLastName(companyEmployeeDto.getLastName());
                            newCompanyEmployeeDto.setEmail(companyEmployeeDto.getEmail());
                            newCompanyEmployeeDto.setPhone(companyEmployeeDto.getPhone());
                            newCompanyEmployeeDto.setAddress1(companyEmployeeDto.getAddress1());
                            newCompanyEmployeeDto.setCity(companyEmployeeDto.getCity());
                            newCompanyEmployeeDto.setState(companyEmployeeDto.getState());
                            newCompanyEmployeeDto.setCountry(companyEmployeeDto.getCountry());
                            newCompanyEmployeeDto.setUserName(companyEmployeeDto.getUserName());
                            newCompanyEmployeeDto.setPassword(companyEmployeeDto.getPassword());
                            newCompanyEmployeeDto.setRoles(companyDetailsDto.getRoles());
                            this.companyEmployeeService.createEmployeeFromTSP(newCompanyEmployeeDto);
                        }
                    } else {
                        for (CompanyEmployeeDto companyEmployeeDto : companyDetailsDto.getEmployees()) {
                            // ====================== add employee =================
                            EmployeeDto newCompanyEmployeeDto = new EmployeeDto();
                            newCompanyEmployeeDto.setCompanyId(id);
                            newCompanyEmployeeDto.setRoleId(roles.getRoleId());
                            newCompanyEmployeeDto.setFirstName(companyEmployeeDto.getFirstName());
                            newCompanyEmployeeDto.setLastName(companyEmployeeDto.getLastName());
                            newCompanyEmployeeDto.setEmail(companyEmployeeDto.getEmail());
                            newCompanyEmployeeDto.setPhone(companyEmployeeDto.getPhone());
                            newCompanyEmployeeDto.setAddress1(companyEmployeeDto.getAddress1());
                            newCompanyEmployeeDto.setCity(companyEmployeeDto.getCity());
                            newCompanyEmployeeDto.setState(companyEmployeeDto.getState());
                            newCompanyEmployeeDto.setCountry(companyEmployeeDto.getCountry());
                            newCompanyEmployeeDto.setUserName(companyEmployeeDto.getUserName());
                            newCompanyEmployeeDto.setPassword(companyEmployeeDto.getPassword());
                            newCompanyEmployeeDto.setRoles(companyDetailsDto.getRoles());
                            this.companyEmployeeService.createEmployeeFromTSP(newCompanyEmployeeDto);
                        }
                    }
                }
                companyDetailsDto.setId(companyDetails.getId());
                return companyDetailsDto;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteCompanyDetails(Integer id) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Company details not found"));
            companyDetails.setIsActive(0);
            this.companyDetailsRepository.save(companyDetails);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String uploadCompanyLogo(Integer companyId, String imagePath) {
        try {
            this.deleteCompanyLogo(companyId);
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found"));
            String updatedPath = this.commonService.updateFileLocationForProfile(imagePath, Integer.parseInt(companyId.toString()), "companyLogo");
            if (updatedPath.equals("Error")) {
                return "Error";
            } else {
                companyDetails.setCompanyLogo(updatedPath);
                this.companyDetailsRepository.save(companyDetails);
                return updatedPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploadCompanyLogo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteCompanyLogo(Integer companyId) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found"));
            File existingImagePath = new File(FILE_DIRECTORY + companyId + "/companyLogo/");
            if (existingImagePath.exists()) {
                this.commonService.deleteDirectoryRecursively(existingImagePath);
                companyDetails.setCompanyLogo("");
                this.companyDetailsRepository.save(companyDetails);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleteCompanyLogo: " + e.getMessage(), e);
        }
    }

    @Override
    public String getLastCompany() {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findLastCompany();
            if (companyDetails != null) {
                return companyDetails.getCompanyNo();
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
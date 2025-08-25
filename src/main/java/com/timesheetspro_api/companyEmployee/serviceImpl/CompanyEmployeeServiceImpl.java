package com.timesheetspro_api.companyEmployee.serviceImpl;

import com.timesheetspro_api.common.dto.CompanyEmployeeDto.CompanyEmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeDto.EmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesDto;
import com.timesheetspro_api.common.exception.GlobalException;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyEmployeeRoles.CompanyEmployeeRoles;
import com.timesheetspro_api.common.model.companyShift.CompanyShift;
import com.timesheetspro_api.common.model.department.Department;
import com.timesheetspro_api.common.model.employeeBackAccountInfo.EmployeeBackAccountInfo;
import com.timesheetspro_api.common.model.employeeType.EmployeeType;
import com.timesheetspro_api.common.model.holidayTemplates.HolidayTemplates;
import com.timesheetspro_api.common.model.overtimeRules.OvertimeRules;
import com.timesheetspro_api.common.model.weeklyOff.WeeklyOff;
import com.timesheetspro_api.common.repository.DepartmentRepository;
import com.timesheetspro_api.common.repository.OvertimeRulesRepository;
import com.timesheetspro_api.common.repository.UserInOutRepository;
import com.timesheetspro_api.common.repository.company.*;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.common.specification.EmployeeStatementSpecification;
import com.timesheetspro_api.common.specification.UserInOutSpecification;
import com.timesheetspro_api.companyEmployee.service.CompanyEmployeeService;
import com.timesheetspro_api.companyEmployeeRole.service.CompanyEmployeeRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar; // Import Calendar class

import java.io.File;
import java.security.Key;
import java.util.*;

@Service(value = "companyEmployeeService")
public class CompanyEmployeeServiceImpl implements CompanyEmployeeService {
    @Value("${timeSheetProDrive}")
    String FILE_DIRECTORY;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeTypeRepository employeeTypeRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private CompanyEmployeeRoleRepository companyEmployeeRoleRepository;

    @Autowired
    private EmployeeBackAccountInfoRepository employeeBackAccountInfoRepository;

    @Autowired
    private CompanyShiftRepository companyShiftRepository;

    @Autowired
    private UserInOutRepository userInOutRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CompanyEmployeeRoleService companyEmployeeRoleService;

    @Autowired
    private OvertimeRulesRepository overtimeRulesRepository;

    @Autowired
    private WeeklyOffRepository weeklyOffRepository;

    @Autowired
    private HolidayTemplatesRepository holidayTemplatesRepository;

    @Override
    public List<Map<String, Object>> getReports(int companyId, String type, int month) {
        try {
            List<Map<String, Object>> response = new ArrayList<>();
            List<Date[]> dateRanges = getLastNMonthDateRanges(month);
            Specification<UserInOut> dateSpec = Specification.where(null);
            dateSpec.and(UserInOutSpecification.hasCompany(companyId));

            Long totalDays = 0L;

            for (Date[] range : dateRanges) {
                dateSpec = dateSpec.or(EmployeeStatementSpecification.betweenCreatedOn(range[0], range[1]));

                LocalDate start = range[0].toLocalDate();
                LocalDate end = range[1].toLocalDate();
                long daysBetween = ChronoUnit.DAYS.between(start, end) + 1;
                totalDays += daysBetween;
            }

            List<UserInOut> userInOutList = this.userInOutRepository.findAll(dateSpec);

            Map<Integer, Set<LocalDate>> employeeWorkDays = new HashMap<>();
            for (UserInOut userInOut : userInOutList) {
                CompanyEmployee employee = userInOut.getUser();
                if (employee == null) continue;

                int empId = employee.getEmployeeId();
                LocalDate workDate = ((java.sql.Date) userInOut.getCreatedOn()).toLocalDate();
                employeeWorkDays.computeIfAbsent(empId, k -> new HashSet<>()).add(workDate);
            }
            for (Map.Entry<Integer, Set<LocalDate>> entry : employeeWorkDays.entrySet()) {
                Integer empId = entry.getKey();
                int daysWorked = entry.getValue().size();
                Optional<CompanyEmployee> employeeOpt = this.companyEmployeeRepository.findById(empId);
                if (employeeOpt.isPresent()) {
                    CompanyEmployee emp = employeeOpt.get();
                    if ("PF".equals(type) && Boolean.TRUE.equals(emp.getIsPf())) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("employeeId", empId);
                        map.put("userName", emp.getUsername());
                        map.put("daysWorked", daysWorked);
                        map.put("totalDays", totalDays);
                        map.put("pf_type", emp.getPfType());

                        if ("Percentage".equals(emp.getPfType())) {
                            Integer pfPercentage = Optional.ofNullable(emp.getPfPercentage()).orElse(0);
                            Integer totalBasicSalary = emp.getBasicSalary() * month;

                            BigDecimal basicSalaryPerMonth = BigDecimal.valueOf(emp.getBasicSalary());
//                            BigDecimal basicSalaryPerDay = basicSalaryPerMonth.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);

                            BigDecimal pfAmount = basicSalaryPerMonth
                                    .multiply(BigDecimal.valueOf(pfPercentage))
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

//                            BigDecimal totalPfAmount = pfAmountPerDay.multiply(BigDecimal.valueOf(daysWorked));
                            map.put("basic_salary", emp.getBasicSalary());
                            map.put("total_basic_salary", totalBasicSalary);
                            map.put("employee_pf_amount", pfAmount.compareTo(BigDecimal.valueOf(900)) > 0 ? 900 : pfAmount);
                            map.put("employer_pf_amount", pfAmount.compareTo(BigDecimal.valueOf(900)) > 0 ? 900 : pfAmount);
                            map.put("total_amount", pfAmount.multiply(BigDecimal.valueOf(2)).compareTo(BigDecimal.valueOf(1800)) > 0 ? BigDecimal.valueOf(1800) : pfAmount.multiply(BigDecimal.valueOf(2)));
                            map.put("pf_percentage", pfPercentage);
                        } else {
                            Integer totalBasicSalary = emp.getBasicSalary() * month;

                            BigDecimal monthlyPfAmount = BigDecimal.valueOf(emp.getPfAmount());
                            BigDecimal totalPfAmountForMonths = monthlyPfAmount.multiply(BigDecimal.valueOf(month));

                            BigDecimal perDayPf = totalPfAmountForMonths
                                    .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

                            BigDecimal pfAmount = perDayPf.multiply(BigDecimal.valueOf(daysWorked)).setScale(0, RoundingMode.HALF_UP); // Round final amount

                            map.put("basic_salary", emp.getBasicSalary());
                            map.put("total_basic_salary", totalBasicSalary);
                            map.put("employee_pf_amount", pfAmount);
                            map.put("employer_pf_amount", pfAmount);
                            map.put("total_amount", pfAmount.multiply(BigDecimal.valueOf(2)));
                            map.put("pf_amount", emp.getPfAmount());
                        }
                        response.add(map);
                    }

                    if ("PT".equals(type) && Boolean.TRUE.equals(emp.getIsPt())) {
                        Map<String, Object> map = new HashMap<>();
                        int grossSalary = emp.getGrossSalary();
                        int ptAmount = emp.getPtAmount();

                        map.put("employeeId", empId);
                        map.put("userName", emp.getUsername());
                        map.put("gross_salary", grossSalary);
                        map.put("total_gross_salary", grossSalary * month);
                        map.put("pt_amount", ptAmount * month);
                        response.add(map);
                    }
                }
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllEmployeeListByCompanyId(int companyId) {
        try {
            List<Map<String, Object>> response = new ArrayList<>();
            List<CompanyEmployee> companyEmployeeList = this.companyEmployeeRepository.findByCompanyId(companyId);

            if (!companyEmployeeList.isEmpty()) {
                for (CompanyEmployee companyEmployee : companyEmployeeList) {
                    Map<String, Object> res = new HashMap<>();
                    CompanyEmployeeDto companyEmployeeDto = this.getEmployee(companyEmployee.getEmployeeId());
                    res.put("employeeId", companyEmployeeDto.getEmployeeId());
                    res.put("userName", companyEmployeeDto.getUserName());
                    response.add(res);
                }
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CompanyEmployeeDto> getAllEmployeeByCompanyId(int companyId) {
        try {
            List<CompanyEmployee> companyEmployeeList = this.companyEmployeeRepository.findAllContractors(companyId);
            List<CompanyEmployeeDto> companyEmployeeDtoList = new ArrayList<>();

            if (!companyEmployeeList.isEmpty()) {
                for (CompanyEmployee companyEmployee : companyEmployeeList) {
                    companyEmployeeDtoList.add(this.getEmployee(companyEmployee.getEmployeeId()));
                }
            }
            return companyEmployeeDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyEmployeeDto getEmployee(int id) {
        try {
            CompanyEmployeeDto companyEmployeeDto = new CompanyEmployeeDto();
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
            EmployeeBackAccountInfo employeeBackAccountInfo = this.employeeBackAccountInfoRepository.findAccountInfoById(id);

            if (employeeBackAccountInfo != null) {
                companyEmployeeDto.setBankAccountId(employeeBackAccountInfo.getId());
            }

            companyEmployeeDto.setUserName(companyEmployee.getUsername());
            companyEmployeeDto.setCompanyId(companyEmployee.getCompanyDetails().getId());

            if (companyEmployee.getCompanyShift() != null) {
                companyEmployeeDto.setShiftId(companyEmployee.getCompanyShift().getId());
            }

            if (companyEmployee.getDepartment() != null) {
                companyEmployeeDto.setDepartmentId(companyEmployee.getDepartment().getId());
            }

            if (companyEmployee.getEmployeeType() != null) {
                companyEmployeeDto.setEmployeeTypeId(companyEmployee.getEmployeeType().getId());
            }

            if (companyEmployee.getDob() != null) {
                companyEmployeeDto.setDob(this.commonService.convertDateToString(companyEmployee.getDob()));
            }

            if (companyEmployee.getHiredDate() != null) {
                companyEmployeeDto.setHiredDate(this.commonService.convertDateToString(companyEmployee.getHiredDate()));
            }

            if (companyEmployee.getOvertimeRules() != null) {
                companyEmployeeDto.setOtId(companyEmployee.getOvertimeRules().getId());
            }

            if (companyEmployee.getWeeklyOff() != null) {
                companyEmployeeDto.setWeeklyOffId(companyEmployee.getWeeklyOff().getId());
            }

            if (companyEmployee.getHolidayTemplates() != null){
                companyEmployeeDto.setHolidayTemplateId(companyEmployee.getHolidayTemplates().getId());
            }

            BeanUtils.copyProperties(companyEmployee, companyEmployeeDto);

            CompanyEmployeeRoles role = this.companyEmployeeRoleRepository.findById(companyEmployee.getRoles().getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
            CompanyEmployeeRolesDto companyEmployeeRolesDto = new CompanyEmployeeRolesDto();
            companyEmployeeDto.setRoleId(role.getRoleId());
            companyEmployeeDto.setRoleName(role.getRoleName());
            companyEmployeeDto.setEmbedding(companyEmployee.getEmbedding());

            BeanUtils.copyProperties(role, companyEmployeeRolesDto);
            companyEmployeeDto.setCompanyEmployeeRolesDto(companyEmployeeRolesDto);

            companyEmployeeDto.setCompanyLocation(companyEmployee.getCompanyLocation());
            return companyEmployeeDto;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyEmployeeDto createEmployee(CompanyEmployeeDto companyEmployeeDto) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyEmployeeDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            CompanyEmployeeRoles companyEmployeeRoles = this.companyEmployeeRoleRepository.findById(companyEmployeeDto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
            Department department = this.departmentRepository.findById(companyEmployeeDto.getDepartmentId()).orElseThrow(() -> new RuntimeException("Department not found"));
            EmployeeType employeeType = this.employeeTypeRepository.findById(companyEmployeeDto.getEmployeeTypeId()).orElseThrow(() -> new RuntimeException("Employee type not found"));
            CompanyShift companyShift = this.companyShiftRepository.findById(companyEmployeeDto.getShiftId()).orElseThrow(() -> new RuntimeException("Shift not found"));

            CompanyEmployee companyEmployee = new CompanyEmployee();

            CompanyEmployee isEmployeeExits = this.companyEmployeeRepository.findByCompanyNoAndUserName(companyEmployeeDto.getCompanyId(), companyEmployeeDto.getUserName());
            if (isEmployeeExits != null) {
                throw new GlobalException("User name is already taken");
            }
            if (companyEmployeeDto.getHiredDate() != null) {
                companyEmployee.setHiredDate(this.commonService.convertStringToDate(companyEmployeeDto.getHiredDate()));
            }
            if (companyEmployeeDto.getDob() != null) {
                companyEmployee.setDob(this.commonService.convertStringToDate(companyEmployeeDto.getDob()));
            }
            if (companyEmployeeDto.getOtId() != null) {
                OvertimeRules overtimeRules = this.overtimeRulesRepository.findById(companyEmployeeDto.getOtId()).orElseThrow(() -> new RuntimeException("Overtime rule not found"));
                companyEmployee.setOvertimeRules(overtimeRules);
            } else {
                companyEmployee.setOvertimeRules(null);
            }
            if (companyEmployeeDto.getWeeklyOffId() != null) {
                WeeklyOff weeklyOff = this.weeklyOffRepository.findById(companyEmployeeDto.getWeeklyOffId()).orElseThrow(() -> new RuntimeException("Weekly off not found"));
                companyEmployee.setWeeklyOff(weeklyOff);
            } else {
                WeeklyOff weeklyOff = this.weeklyOffRepository.findDefault();
                companyEmployee.setWeeklyOff(weeklyOff);
            }
            if (companyEmployeeDto.getHolidayTemplateId() != null){
                HolidayTemplates holidayTemplates = this.holidayTemplatesRepository.findById(companyEmployeeDto.getHolidayTemplateId()).orElseThrow(() -> new RuntimeException("Holiday template not found"));
                companyEmployee.setHolidayTemplates(holidayTemplates);
            }

            companyEmployee.setCompanyDetails(companyDetails);
            companyEmployee.setRoles(companyEmployeeRoles);
            companyEmployee.setDepartment(department);
            companyEmployee.setEmployeeType(employeeType);
            companyEmployee.setCompanyShift(companyShift);

            BeanUtils.copyProperties(companyEmployeeDto, companyEmployee);
            this.companyEmployeeRepository.save(companyEmployee);
            companyEmployeeDto.setEmployeeId(companyEmployee.getEmployeeId());
            return companyEmployeeDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompanyEmployeeDto updateEmployee(int id, CompanyEmployeeDto companyEmployeeDto) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyEmployeeDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            CompanyEmployeeRoles companyEmployeeRoles = this.companyEmployeeRoleRepository.findById(companyEmployeeDto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
            Department department = this.departmentRepository.findById(companyEmployeeDto.getDepartmentId()).orElseThrow(() -> new RuntimeException("Department not found"));
            EmployeeType employeeType = this.employeeTypeRepository.findById(companyEmployeeDto.getEmployeeTypeId()).orElseThrow(() -> new RuntimeException("Employee type not found"));
            CompanyShift companyShift = this.companyShiftRepository.findById(companyEmployeeDto.getShiftId()).orElseThrow(() -> new RuntimeException("Shift not found"));

            CompanyEmployee isEmployeeExits = this.companyEmployeeRepository.findByCompanyNoAndUserName(companyEmployeeDto.getCompanyId(), companyEmployeeDto.getUserName());
            if (isEmployeeExits != null && !companyEmployeeDto.getUserName().equals(companyEmployee.getUsername())) {
                throw new GlobalException("User name is already taken");
            }

            if (companyEmployeeDto.getHiredDate() != null) {
                companyEmployee.setHiredDate(this.commonService.convertStringToDate(companyEmployeeDto.getHiredDate()));
            }

            if (companyEmployeeDto.getDob() != null) {
                companyEmployee.setDob(this.commonService.convertStringToDate(companyEmployeeDto.getDob()));
            }

            if (companyEmployeeDto.getOtId() != null) {
                OvertimeRules overtimeRules = this.overtimeRulesRepository.findById(companyEmployeeDto.getOtId()).orElseThrow(() -> new RuntimeException("Overtime rule not found"));
                companyEmployee.setOvertimeRules(overtimeRules);
            } else {
                companyEmployee.setOvertimeRules(null);
            }

            if (companyEmployeeDto.getWeeklyOffId() != null) {
                WeeklyOff weeklyOff = this.weeklyOffRepository.findById(companyEmployeeDto.getWeeklyOffId()).orElseThrow(() -> new RuntimeException("Weekly off not found"));
                companyEmployee.setWeeklyOff(weeklyOff);
            } else {
                WeeklyOff weeklyOff = this.weeklyOffRepository.findDefault();
                companyEmployee.setWeeklyOff(weeklyOff);
            }
            if (companyEmployeeDto.getHolidayTemplateId() != null){
                HolidayTemplates holidayTemplates = this.holidayTemplatesRepository.findById(companyEmployeeDto.getHolidayTemplateId()).orElseThrow(() -> new RuntimeException("Holiday template not found"));
                companyEmployee.setHolidayTemplates(holidayTemplates);
            }
            companyEmployee.setCompanyShift(companyShift);
            companyEmployee.setCompanyDetails(companyDetails);
            companyEmployee.setRoles(companyEmployeeRoles);
            companyEmployee.setDepartment(department);
            companyEmployee.setEmployeeType(employeeType);
            companyEmployee.setCompanyLocation(companyEmployeeDto.getCompanyLocation());
            BeanUtils.copyProperties(companyEmployeeDto, companyEmployee);
            this.companyEmployeeRepository.save(companyEmployee);
            companyEmployeeDto.setEmployeeId(companyEmployee.getEmployeeId());
            return companyEmployeeDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadEmployeeProfile(Integer companyId, Integer employeeId, String imagePath) {
        try {
            this.deleteEmployeeProfile(companyId, employeeId);
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
            String updatedPath = this.commonService.updateFileLocationForProfile(imagePath, Long.parseLong(companyId.toString()), "employeeProfile/" + employeeId);
            if (updatedPath.equals("Error")) {
                return "Error";
            } else {
                companyEmployee.setProfileImage(updatedPath);
                this.companyEmployeeRepository.save(companyEmployee);
                return updatedPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploadEmployee: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteEmployeeProfile(Integer companyId, Integer employeeId) {
        try {
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
            File existingImagePath = new File(FILE_DIRECTORY + companyId + "/employeeProfile/" + employeeId);
            if (existingImagePath.exists()) {
                this.commonService.deleteDirectoryRecursively(existingImagePath);
                companyEmployee.setProfileImage("");
                this.companyEmployeeRepository.save(companyEmployee);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleteCompanyLogo: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadEmployeeAadharImage(Integer companyId, Integer employeeId, String imagePath) {
        try {
            this.deleteEmployeeProfile(companyId, employeeId);
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
            String updatedPath = this.commonService.updateFileLocationForProfile(imagePath, Long.parseLong(companyId.toString()), "employeeProfile/aadharImage/" + employeeId);
            if (updatedPath.equals("Error")) {
                return "Error";
            } else {
                companyEmployee.setAadharImage(updatedPath);
                this.companyEmployeeRepository.save(companyEmployee);
                return updatedPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploadEmployee: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteEmployeeAadharImage(Integer companyId, Integer employeeId) {
        try {
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
            File existingImagePath = new File(FILE_DIRECTORY + companyId + "/employeeProfile/aadharImage/" + employeeId);
            if (existingImagePath.exists()) {
                this.commonService.deleteDirectoryRecursively(existingImagePath);
                companyEmployee.setAadharImage("");
                this.companyEmployeeRepository.save(companyEmployee);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleteCompanyLogo: " + e.getMessage(), e);
        }
    }

    @Override
    public Long getLastUserId() {
        try {
            Long lastUserId = this.companyEmployeeRepository.getLastUserId();
            if (lastUserId == null) {
                return 0L;
            }
            return lastUserId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteEmployee(int id) {
        try {
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
            this.deleteEmployeeProfile(companyEmployee.getCompanyDetails().getId(), id);
            this.companyEmployeeRepository.delete(companyEmployee);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmployeeDto createEmployeeFromTSP(EmployeeDto employeeDto) {
        try {
            CompanyEmployeeRolesDto role = null;
            List<CompanyEmployeeRoles> companyEmployeeRolesList = this.companyEmployeeRoleRepository.findByCompanyId(employeeDto.getCompanyId());
            if (companyEmployeeRolesList.isEmpty()) {
                for (CompanyEmployeeRolesDto companyEmployeeRolesDto : employeeDto.getRoles()) {
                    CompanyEmployeeRolesDto rolesDto = new CompanyEmployeeRolesDto();
                    if (!companyEmployeeRolesDto.getRoleName().equals(employeeDto.getRoleName())) {
                        rolesDto.setCompanyId(employeeDto.getCompanyId());
                        rolesDto.setRoleName(companyEmployeeRolesDto.getRoleName());
                        rolesDto.setRolesActions(companyEmployeeRolesDto.getRolesActions());
                        this.companyEmployeeRoleService.createRole(rolesDto);
                    }
                }
                CompanyEmployeeRolesDto companyEmployeeRolesDto = new CompanyEmployeeRolesDto();
                companyEmployeeRolesDto.setCompanyId(employeeDto.getCompanyId());
                companyEmployeeRolesDto.setRoleName(employeeDto.getRoleName());
                companyEmployeeRolesDto.setRolesActions(employeeDto.getRoles().get(employeeDto.getRoles().size() - 1).getRolesActions());
                role = this.companyEmployeeRoleService.createRole(companyEmployeeRolesDto);
            }

            CompanyDetails companyDetails = this.companyDetailsRepository.findById(employeeDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            CompanyEmployeeRoles companyEmployeeRoles = this.companyEmployeeRoleRepository.findById(role != null ? role.getRoleId() : employeeDto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
            CompanyEmployee companyEmployee = new CompanyEmployee();

            CompanyEmployee isEmployeeExits = this.companyEmployeeRepository.findByCompanyNoAndUserName(employeeDto.getCompanyId(), employeeDto.getUserName());
            if (isEmployeeExits != null) {
                throw new GlobalException("User name is already taken");
            }

            companyEmployee.setCompanyDetails(companyDetails);
            companyEmployee.setRoles(companyEmployeeRoles);
            companyEmployee.setLateEntryPenaltyRule(true);
            companyEmployee.setEarlyExitPenaltyRule(true);
            BeanUtils.copyProperties(employeeDto, companyEmployee);
            this.companyEmployeeRepository.save(companyEmployee);
            employeeDto.setEmployeeId(companyEmployee.getEmployeeId());
            return employeeDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmployeeDto updateEmployeeFromTSP(int id, EmployeeDto employeeDto) {
        try {
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(employeeDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            CompanyEmployeeRoles companyEmployeeRoles = this.companyEmployeeRoleRepository.findById(employeeDto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(id).orElseThrow(() -> new RuntimeException("CompanyEmployee not found"));

            CompanyEmployee isEmployeeExits = this.companyEmployeeRepository.findByCompanyNoAndUserName(employeeDto.getCompanyId(), employeeDto.getUserName());
            if (isEmployeeExits != null && !employeeDto.getUserName().equals(companyEmployee.getUsername())) {
                throw new GlobalException("User name is already taken");
            }

            companyEmployee.setCompanyDetails(companyDetails);
            companyEmployee.setRoles(companyEmployeeRoles);
            companyEmployee.setLateEntryPenaltyRule(true);
            companyEmployee.setEarlyExitPenaltyRule(true);
            BeanUtils.copyProperties(employeeDto, companyEmployee);
            this.companyEmployeeRepository.save(companyEmployee);
            employeeDto.setEmployeeId(companyEmployee.getEmployeeId());
            return employeeDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static List<java.sql.Date[]> getLastNMonthDateRanges(int monthCount) {
        List<java.sql.Date[]> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = monthCount; i >= 1; i--) {
            LocalDate start = today.minusMonths(i).withDayOfMonth(1);
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

            result.add(new Date[]{
                    java.sql.Date.valueOf(start),
                    java.sql.Date.valueOf(end)
            });
        }
        return result;
    }
}

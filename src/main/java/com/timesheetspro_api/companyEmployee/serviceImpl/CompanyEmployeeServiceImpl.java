package com.timesheetspro_api.companyEmployee.serviceImpl;

import com.timesheetspro_api.common.constants.Constants;
import com.timesheetspro_api.common.dto.CompanyEmployeeDto.CompanyEmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeDto.EmployeeDto;
import com.timesheetspro_api.common.dto.CompanyEmployeeRoles.CompanyEmployeeRolesDto;
import com.timesheetspro_api.common.exception.GlobalException;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyEmployeeRoles.CompanyEmployeeRoles;
import com.timesheetspro_api.common.model.companyShift.CompanyShift;
import com.timesheetspro_api.common.model.department.Department;
import com.timesheetspro_api.common.model.employeeBackAccountInfo.EmployeeBackAccountInfo;
import com.timesheetspro_api.common.model.employeeType.EmployeeType;
import com.timesheetspro_api.common.repository.DepartmentRepository;
import com.timesheetspro_api.common.repository.company.*;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.companyEmployee.service.CompanyEmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    private CommonService commonService;

    @Override
    public List<Map<String, Object>> getReports(int companyId, String type,int month) {
        try {
            List<Map<String, Object>> response = new ArrayList<>();
            List<CompanyEmployee> companyEmployeeList = new ArrayList<>();
            if (type.equals("PF")){
                companyEmployeeList = this.companyEmployeeRepository.getReportByPF(companyId);
            }
            if (type.equals("PT")){
                companyEmployeeList = this.companyEmployeeRepository.getReportByPT(companyId);
            }
            if (!companyEmployeeList.isEmpty()) {
                for (CompanyEmployee companyEmployee : companyEmployeeList) {
                    Map<String, Object> res = new HashMap<>();
                    CompanyEmployeeDto companyEmployeeDto = this.getEmployee(companyEmployee.getEmployeeId());
                    res.put("employeeId",companyEmployeeDto.getEmployeeId());
                    res.put("userName",companyEmployeeDto.getUserName());

                    if (type.equals("PF") && companyEmployee.getIsPf()) {
                        Integer pfPercentage = companyEmployee.getPfPercentage();
                        Integer pfAmount = companyEmployee.getPfAmount();

                        if (pfPercentage != null && pfPercentage != 0){
                            Integer basicSalaryPerMonth = companyEmployeeDto.getBasicSalary();

                            Integer totalBasicSalary = basicSalaryPerMonth * month;
                            Integer pfAmountPerMonth = (basicSalaryPerMonth * pfPercentage) / 100;
                            Integer totalPfAmount = pfAmountPerMonth * month;

                            res.put("basic_salary", basicSalaryPerMonth);
                            res.put("total_basic_salary", totalBasicSalary);

                            res.put("employee_pf_amount", totalPfAmount);
                            res.put("employer_pf_amount", totalPfAmount);
                            res.put("pf_percentage", pfPercentage);
                            res.put("total_amount", totalPfAmount * 2);
                        }else{
                            Integer basicSalaryPerMonth = companyEmployeeDto.getBasicSalary();

                            Integer totalBasicSalary = basicSalaryPerMonth * month;
                            Integer totalPfAmount = pfAmount * month;

                            res.put("basic_salary", basicSalaryPerMonth);
                            res.put("total_basic_salary", totalBasicSalary);

                            res.put("employee_pf_amount", totalPfAmount);
                            res.put("employer_pf_amount", totalPfAmount);
                            res.put("total_amount", totalPfAmount * 2);
                        }
                    }

                    if (type.equals("PT") && companyEmployee.getIsPt()) {
                       Integer totalBasicSalary = companyEmployee.getGrossSalary() * month;
                       Integer totalPtAmount = companyEmployee.getPtAmount() * month;

                        res.put("gross_salary", companyEmployee.getGrossSalary());
                        res.put("total_gross_salary", totalBasicSalary);
                        res.put("pt_amount", totalPtAmount);
                    }
                    response.add(res);
                }
            }
            return response;
        } catch (Exception e) {
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
                    res.put("employeeId",companyEmployeeDto.getEmployeeId());
                    res.put("userName",companyEmployeeDto.getUserName());
                    response.add(res);
                }
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CompanyEmployeeDto> getAllEmployeeByCompanyId(int companyId, int id) {
        try {
            List<CompanyEmployee> companyEmployeeList = this.companyEmployeeRepository.findAllContractors(companyId, id);
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
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(employeeDto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
            CompanyEmployeeRoles companyEmployeeRoles = this.companyEmployeeRoleRepository.findById(employeeDto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
            CompanyEmployee companyEmployee = new CompanyEmployee();

            CompanyEmployee isEmployeeExits = this.companyEmployeeRepository.findByCompanyNoAndUserName(employeeDto.getCompanyId(), employeeDto.getUserName());
            if (isEmployeeExits != null) {
                throw new GlobalException("User name is already taken");
            }

            companyEmployee.setCompanyDetails(companyDetails);
            companyEmployee.setRoles(companyEmployeeRoles);
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
            BeanUtils.copyProperties(employeeDto, companyEmployee);
            this.companyEmployeeRepository.save(companyEmployee);
            employeeDto.setEmployeeId(companyEmployee.getEmployeeId());
            return employeeDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

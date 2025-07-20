package com.timesheetspro_api.department.serviceImpl;

import com.timesheetspro_api.common.dto.department.DepartmentDto;
import com.timesheetspro_api.common.model.department.Department;
import com.timesheetspro_api.common.repository.DepartmentRepository;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.department.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "departmentService")
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    public List<DepartmentDto> getALlDepartment(Integer companyId) {
        try {
            List<Department> departments = this.departmentRepository.findByCompanyId(companyId);
            List<DepartmentDto> departmentDtoList = new ArrayList<>();
            if (!departments.isEmpty()) {
                for (Department department : departments) {
                    departmentDtoList.add(this.getDepartment(department.getId()));
                }
            }
            return departmentDtoList;
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    public DepartmentDto getDepartment(Long departmentId) {
        try {
            Department department = this.departmentRepository.findById(departmentId).orElseThrow(() -> new RuntimeException("Department not found"));
            DepartmentDto departmentDto = new DepartmentDto();
            departmentDto.setId(department.getId());
            if (department.getCompanyDetails() != null) {
                departmentDto.setCompanyId(department.getCompanyDetails().getId());
            }
            departmentDto.setDepartmentName(department.getDepartmentName());
            return departmentDto;
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        try {
            Department department = new Department();
            department.setDepartmentName(departmentDto.getDepartmentName());
            if (departmentDto.getCompanyId() != null) {
                department.setCompanyDetails(this.companyDetailsRepository.findById(departmentDto.getCompanyId())
                        .orElseThrow(() -> new RuntimeException("Company not found")));
            }
            this.departmentRepository.save(department);
            return departmentDto;
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto departmentDto) {
        try {
            Department department = this.departmentRepository.findById(departmentId).orElseThrow(() -> new RuntimeException("Department not found"));
            department.setDepartmentName(departmentDto.getDepartmentName());
            if (departmentDto.getCompanyId() != null) {
                department.setCompanyDetails(this.companyDetailsRepository.findById(departmentDto.getCompanyId())
                        .orElseThrow(() -> new RuntimeException("Company not found")));
            }
            this.departmentRepository.save(department);
            return departmentDto;
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

    public void deleteDepartment(Long departmentId) {
        try {
            Department department = this.departmentRepository.findById(departmentId).orElseThrow(() -> new RuntimeException("Department not found"));
            this.departmentRepository.delete(department);
        } catch (Exception e) {
            throw new RuntimeException("Error :" + e.getMessage());
        }
    }

}

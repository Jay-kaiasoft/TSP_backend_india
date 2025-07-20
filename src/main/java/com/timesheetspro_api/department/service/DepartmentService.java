package com.timesheetspro_api.department.service;

import com.timesheetspro_api.common.dto.department.DepartmentDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {

    List<DepartmentDto> getALlDepartment(Integer companyId);

    DepartmentDto getDepartment(Long departmentId);

    DepartmentDto createDepartment(DepartmentDto departmentDto);

    DepartmentDto updateDepartment(Long departmentId, DepartmentDto departmentDto);

    void deleteDepartment(Long departmentId);

}

package com.timesheetspro_api.employeeType.service;


import com.timesheetspro_api.common.dto.employeeType.EmployeeTypeDto;
import com.timesheetspro_api.common.model.employeeType.EmployeeType;

import java.util.List;

public interface EmployeeTypeService {

    List<EmployeeTypeDto> getAllEmployeeType();

    EmployeeTypeDto getEmployeeType(int id);

    EmployeeType createEmployeeType(EmployeeTypeDto employeeTypeDto);

    EmployeeType updateEmployeeType(int id, EmployeeTypeDto employeeTypeDto);

    void deleteEmployeeType(int id);

}

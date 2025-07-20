package com.timesheetspro_api.employeeType.serviceImpl;

import com.timesheetspro_api.common.dto.employeeType.EmployeeTypeDto;
import com.timesheetspro_api.common.model.employeeType.EmployeeType;
import com.timesheetspro_api.common.repository.company.EmployeeTypeRepository;
import com.timesheetspro_api.employeeType.service.EmployeeTypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "employeeTypeService")
public class EmployeeTypeServiceImpl implements EmployeeTypeService {

    @Autowired
    private EmployeeTypeRepository employeeTypeRepository;

    @Override
    public List<EmployeeTypeDto> getAllEmployeeType() {
        try {
            List<EmployeeType> employeeTypeList = this.employeeTypeRepository.findAll();
            List<EmployeeTypeDto> employeeTypeDtoList = new ArrayList<>();

            if (!employeeTypeList.isEmpty()){
                for (EmployeeType employeeType:employeeTypeList){
                    employeeTypeDtoList.add(this.getEmployeeType(employeeType.getId()));
                }
            }
            return employeeTypeDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmployeeTypeDto getEmployeeType(int id) {
        try {
            EmployeeType employeeType = this.employeeTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Type not found"));
            EmployeeTypeDto employeeTypeDto = new EmployeeTypeDto();
            BeanUtils.copyProperties(employeeType, employeeTypeDto);
            return employeeTypeDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmployeeType createEmployeeType(EmployeeTypeDto employeeTypeDto) {
        try {
            EmployeeType employeeType = new EmployeeType();
            BeanUtils.copyProperties(employeeTypeDto, employeeType);
            this.employeeTypeRepository.save(employeeType);
            return employeeType;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmployeeType updateEmployeeType(int id, EmployeeTypeDto employeeTypeDto) {
        try {
            EmployeeType employeeType = this.employeeTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Type not found"));
            BeanUtils.copyProperties(employeeTypeDto, employeeType);
            this.employeeTypeRepository.save(employeeType);
            return employeeType;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteEmployeeType(int id) {
        try {
            EmployeeType employeeType = this.employeeTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Type not found"));
            this.employeeTypeRepository.delete(employeeType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

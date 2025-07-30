package com.timesheetspro_api.salaryStatementHistory.serviceImpl;

import com.timesheetspro_api.common.dto.employeeStatement.EmployeeSalaryStatementDto;
import com.timesheetspro_api.common.dto.employeeStatement.SalaryStatementRequestDto;
import com.timesheetspro_api.common.dto.salaryStatementHistory.SalaryStatementHistoryDto;
import com.timesheetspro_api.common.model.salaryStatementHistory.SalaryStatementHistory;
import com.timesheetspro_api.common.repository.company.SalaryStatementHistoryRepository;
import com.timesheetspro_api.common.specification.SalaryStatementHistorySpecification;
import com.timesheetspro_api.salaryStatementHistory.service.SalaryStatementHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "SalaryStatementHistoryService")
public class SalaryStatementHistoryServiceImpl implements SalaryStatementHistoryService {

    @Autowired
    private SalaryStatementHistoryRepository salaryStatementHistoryRepository;

    @Override
    public List<SalaryStatementHistoryDto> filterSalaryStatementHistory(List<Integer> employeeId, List<Integer> departmentId, List<String> month) {
        try {
            Specification<SalaryStatementHistory> spec = Specification.where(null);
            if (employeeId != null && !employeeId.isEmpty()) {
                spec = spec.and(SalaryStatementHistorySpecification.hasUserIds(employeeId));
            }
            if (departmentId != null && !departmentId.isEmpty()) {
                spec = spec.and(SalaryStatementHistorySpecification.hasDepartmentIds(departmentId));
            }
            if (month != null && !month.isEmpty()) {
                spec = spec.and(SalaryStatementHistorySpecification.hasMonth(month));
            }
            List<SalaryStatementHistory> salaryStatementHistories = this.salaryStatementHistoryRepository.findAll(spec);
            List<SalaryStatementHistoryDto> salaryStatementHistoryDtoList = new ArrayList<>();

            if (!salaryStatementHistories.isEmpty()) {
                for (SalaryStatementHistory salaryStatementHistory : salaryStatementHistories) {
                    salaryStatementHistoryDtoList.add(this.getSalaryStatementHistory(salaryStatementHistory.getId()));
                }
            }
            return salaryStatementHistoryDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalaryStatementHistoryDto getSalaryStatementHistory(Integer id) {
        try {
            SalaryStatementHistory salaryStatementHistory = this.salaryStatementHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Statement History not found"));
            SalaryStatementHistoryDto salaryStatementHistoryDto = new SalaryStatementHistoryDto();
            BeanUtils.copyProperties(salaryStatementHistory, salaryStatementHistoryDto);
            return salaryStatementHistoryDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> addSalaryStatement(List<SalaryStatementHistoryDto> salaryStatement) {
        Map<String, Object> response = new HashMap<>();
        List<SalaryStatementHistoryDto> addedStatements = new ArrayList<>();

        try {
            for (SalaryStatementHistoryDto dto : salaryStatement) {
                boolean exists = this.salaryStatementHistoryRepository.isExites(
                        dto.getCompanyId(), dto.getEmployeeId(), dto.getMonth());

                if (!exists) {
                    SalaryStatementHistory entity = new SalaryStatementHistory();
                    BeanUtils.copyProperties(dto, entity);
                    this.salaryStatementHistoryRepository.save(entity);
                    addedStatements.add(dto);
                } else {
                    throw new RuntimeException("Salary statement already exists for month " + dto.getMonth());
                }
            }
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public SalaryStatementHistoryDto updateSalaryStatement(Integer id, SalaryStatementHistoryDto salaryStatement) {
        try {
            SalaryStatementHistory salaryStatementHistory = this.salaryStatementHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Statement History not found"));
            BeanUtils.copyProperties(salaryStatement, salaryStatementHistory);
            this.salaryStatementHistoryRepository.save(salaryStatementHistory);
            return salaryStatement;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSalaryStatement(Integer id) {
        try {
            SalaryStatementHistory salaryStatementHistory = this.salaryStatementHistoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Statement History not found"));
            this.salaryStatementHistoryRepository.delete(salaryStatementHistory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.timesheetspro_api.salaryStatementHistory.serviceImpl;

import com.timesheetspro_api.common.dto.salaryStatementHistory.SalaryStatementHistoryDto;
import com.timesheetspro_api.common.model.salaryStatementHistory.SalaryStatementHistory;
import com.timesheetspro_api.common.repository.company.SalaryStatementHistoryRepository;
import com.timesheetspro_api.common.specification.SalaryStatementHistorySpecification;
import com.timesheetspro_api.salaryStatementHistory.service.SalaryStatementHistoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "SalaryStatementHistoryService")
public class SalaryStatementHistoryServiceImpl implements SalaryStatementHistoryService {

    @Autowired
    private SalaryStatementHistoryRepository salaryStatementHistoryRepository;

    @Override
    public List<Map<String, Object>> filterSalaryStatementHistory(List<Integer> employeeId, List<Integer> departmentId, List<String> month) {
        try {
            boolean noFilters =
                    (employeeId == null || employeeId.isEmpty()) &&
                            (departmentId == null || departmentId.isEmpty()) &&
                            (month == null || month.isEmpty());

            if (noFilters) {
                System.out.println("All filters are empty, returning empty list.");
                return new ArrayList<>();
            }

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
            List<SalaryStatementHistoryDto> dtoList = new ArrayList<>();

            for (SalaryStatementHistory entity : salaryStatementHistories) {
                dtoList.add(this.getSalaryStatementHistory(entity.getId()));
            }

            // Group by month
            Map<String, List<SalaryStatementHistoryDto>> grouped = dtoList.stream()
                    .collect(Collectors.groupingBy(SalaryStatementHistoryDto::getMonth));

            // Build result without using DTO
            List<Map<String, Object>> result = new ArrayList<>();

            grouped.entrySet().stream()
                    .sorted(Comparator.comparing(e -> parseMonthYear(e.getKey())))
                    .forEach(entry -> {
                        Map<String, Object> group = new HashMap<>();
                        group.put("month", entry.getKey());
                        group.put("data", entry.getValue());
                        result.add(group);
                    });

            return result;

        } catch (Exception e) {
            e.printStackTrace();
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

    private YearMonth parseMonthYear(String monthYear) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH);
        return YearMonth.parse(monthYear, formatter);
    }

}

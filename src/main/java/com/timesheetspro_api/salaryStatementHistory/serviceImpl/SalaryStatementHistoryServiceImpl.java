package com.timesheetspro_api.salaryStatementHistory.serviceImpl;

import com.timesheetspro_api.common.dto.SalaryStatementMaster.SalaryStatementMasterDto;
import com.timesheetspro_api.common.dto.salaryStatementHistory.SalaryStatementHistoryDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.salaryStatementHistory.SalaryStatementHistory;
import com.timesheetspro_api.common.repository.UserInOutRepository;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.SalaryStatementHistoryRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.common.specification.EmployeeStatementSpecification;
import com.timesheetspro_api.common.specification.SalaryStatementHistorySpecification;
import com.timesheetspro_api.salaryStatementHistory.service.SalaryStatementHistoryService;
import com.timesheetspro_api.salaryStatementMaster.service.SalaryStatementMasterService;
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

    @Autowired
    private UserInOutRepository userInOutRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private SalaryStatementMasterService salaryStatementMasterService;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

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
                    .collect(Collectors.groupingBy(SalaryStatementHistoryDto::getMonthYear));

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
        try {
            for (SalaryStatementHistoryDto dto : salaryStatement) {
                java.util.Date startDate, endDate;
                startDate = this.commonService.convertLocalToUtc(dto.getStartDate(), dto.getTimeZone(), false);
                endDate = this.commonService.convertLocalToUtc(dto.getEndDate(), dto.getTimeZone(), false);

                Specification<UserInOut> userSpec = Specification.where(EmployeeStatementSpecification.hasUserIds(List.of(dto.getEmployeeId())));
                userSpec = userSpec.and(EmployeeStatementSpecification.betweenCreatedOn(startDate, endDate));

                List<UserInOut> userInOuts = this.userInOutRepository.findAll(userSpec);
                for (UserInOut userInOut : userInOuts) {
                    userInOut.setIsSalaryGenerate(1); // Set to 1 to indicate salary has been generated
                    this.userInOutRepository.save(userInOut);
                }

                SalaryStatementHistory entity = this.salaryStatementHistoryRepository.isExites(dto.getEmployeeId(), dto.getCompanyId(), dto.getMonthNumber(), dto.getYear());
                if (entity != null) {
                    System.out.println("================ existing entity found ================");

                    entity.setOtAmount(dto.getOtAmount() + entity.getOtAmount());
                    entity.setTotalEarnSalary(dto.getTotalEarnings() + entity.getTotalEarnSalary());
                    entity.setTotalPfAmount(dto.getTotalPfAmount() + entity.getTotalPfAmount());
                    entity.setPtAmount(dto.getPtAmount() + entity.getPtAmount());
                    entity.setNetSalary(dto.getNetSalary() + entity.getNetSalary());
                    entity.setOtherDeductions(dto.getOtherDeductions() + entity.getOtherDeductions());
                    entity.setTotalDeductions(dto.getTotalDeductions() + entity.getTotalDeductions());
                    entity.setTotalEarnings(dto.getTotalEarnings() + entity.getTotalEarnings());
                    entity.setNote(dto.getNote());
                    this.salaryStatementHistoryRepository.save(entity);
                } else {
                    entity = new SalaryStatementHistory();
                    CompanyDetails companyDetails = this.companyDetailsRepository.findById(dto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
                    entity.setCompanyDetails(companyDetails);
                    CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(dto.getGeneratedBy())
                            .orElseThrow(() -> new RuntimeException("Company Employee not found"));
                    entity.setCompanyEmployee(companyEmployee);
                    entity.setClockInOutId(Integer.parseInt(dto.getClockInOutId().toString()));
                    entity.setMonth(dto.getMonthNumber());
                    entity.setYear(dto.getYear());
                    Date currentDate = new Date();
                    entity.setGeneratedDate(currentDate);
                    BeanUtils.copyProperties(dto, entity, "id", "companyId", "month");
                    this.salaryStatementHistoryRepository.save(entity);
                }

                SalaryStatementMasterDto salaryStatementMasterDto = this.salaryStatementMasterService
                        .getSalaryStatementMastersByMonthAndYear(dto.getCompanyId(), dto.getMonthNumber(), dto.getYear());

                if (salaryStatementMasterDto != null) {
                    Integer totalSalary = salaryStatementMasterDto.getTotalSalary() != null ? dto.getNetSalary() + salaryStatementMasterDto.getTotalSalary() : 0;
                    System.out.println("========== Total Salary: " + totalSalary);
                    salaryStatementMasterDto.setTotalSalary(totalSalary);

                    Integer pfAmount = dto.getTotalPfAmount() != null ? dto.getTotalPfAmount() + salaryStatementMasterDto.getTotalPf() : 0;
                    System.out.println("========== Total pfAmount: " + pfAmount);
                    salaryStatementMasterDto.setTotalPf(pfAmount);

                    Integer ptAmount = dto.getPtAmount() != null ? dto.getPtAmount() + salaryStatementMasterDto.getTotalPt() : 0;
                    System.out.println("========== Total ptAmount: " + ptAmount);
                    salaryStatementMasterDto.setTotalPt(ptAmount);

                    salaryStatementMasterDto.setNote(salaryStatementMasterDto.getNote());

                    this.salaryStatementMasterService.updateSalaryStatementMaster(
                            salaryStatementMasterDto.getId(), salaryStatementMasterDto
                    );
                } else {
                    SalaryStatementMasterDto salaryStatementMasterDto2 = new SalaryStatementMasterDto();
                    salaryStatementMasterDto2.setCompanyId(salaryStatement.get(0).getCompanyId());
                    salaryStatementMasterDto2.setMonth(salaryStatement.get(0).getMonthNumber());
                    salaryStatementMasterDto2.setYear(salaryStatement.get(0).getYear());
                    salaryStatementMasterDto2.setNote(dto.getNote());
                    salaryStatementMasterDto2.setTotalSalary(dto.getNetSalary());
                    salaryStatementMasterDto2.setTotalPf(dto.getTotalPfAmount());
                    salaryStatementMasterDto2.setTotalPt(dto.getPtAmount());
                    this.salaryStatementMasterService.createSalaryStatementMaster(salaryStatementMasterDto2);
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

            Object[] result = this.salaryStatementHistoryRepository
                    .getSalaryTotals(salaryStatement.getCompanyId(), salaryStatement.getMonthNumber(), salaryStatement.getYear())
                    .get(0);

            Integer totalNetSalary = ((Number) result[0]).intValue();
            Integer totalPfAmount = ((Number) result[1]).intValue();
            Integer totalPtAmount = ((Number) result[2]).intValue();

            SalaryStatementMasterDto salaryStatementMasterDto = this.salaryStatementMasterService.getSalaryStatementMastersByMonthAndYear(salaryStatement.getCompanyId(), salaryStatement.getMonthNumber(), salaryStatement.getYear());
            if (salaryStatementMasterDto != null) {
                salaryStatementMasterDto.setTotalSalary(totalNetSalary);
                salaryStatementMasterDto.setTotalPf(totalPfAmount);
                salaryStatementMasterDto.setTotalPt(totalPtAmount);
                this.salaryStatementMasterService.updateSalaryStatementMaster(salaryStatementMasterDto.getId(), salaryStatementMasterDto);
            }
            return salaryStatement;
        } catch (Exception e) {
            e.printStackTrace();
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

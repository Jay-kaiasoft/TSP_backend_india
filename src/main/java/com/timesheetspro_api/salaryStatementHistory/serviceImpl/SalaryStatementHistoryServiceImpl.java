package com.timesheetspro_api.salaryStatementHistory.serviceImpl;

import com.timesheetspro_api.common.dto.SalaryStatementMaster.SalaryStatementMasterDto;
import com.timesheetspro_api.common.dto.salaryStatementHistory.SalaryStatementHistoryDto;
import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.salaryStatementHistory.SalaryStatementHistory;
import com.timesheetspro_api.common.model.salaryStatementMaster.SalaryStatementMaster;
import com.timesheetspro_api.common.repository.UserInOutRepository;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
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

                SalaryStatementHistory entity = this.salaryStatementHistoryRepository.isExites(dto.getEmployeeId(),dto.getCompanyId(), dto.getMonth());
                if (entity != null) {
                    Integer totalOtAmount = entity.getOtAmount() != null ? dto.getOtAmount() + dto.getOtAmount() : 0;
                    entity.setOtAmount(totalOtAmount);
                    Integer totalEarningsAmount = entity.getTotalEarnSalary() != null ? dto.getTotalEarnSalary() + dto.getTotalEarnSalary() : 0;
                    entity.setTotalEarnSalary(totalEarningsAmount);
                    Integer totalPfAmount = entity.getTotalPfAmount() != null ? dto.getTotalPfAmount() + dto.getTotalPfAmount() : 0;
                    entity.setTotalPfAmount(totalPfAmount);
                    Integer totalPtAmount = entity.getPtAmount() != null ? dto.getPtAmount() + dto.getPtAmount() : 0;
                    entity.setPtAmount(totalPtAmount);
                    Integer totalNetSalary = entity.getNetSalary() != null ? dto.getNetSalary() + dto.getNetSalary() : 0;
                    entity.setNetSalary(totalNetSalary);
                    Integer otherDeduction = entity.getOtherDeductions() != null ? dto.getOtherDeductions() + dto.getOtherDeductions() : 0;
                    entity.setOtherDeductions(otherDeduction);
                    Integer totalDeduction = entity.getTotalDeductions() != null ? dto.getTotalDeductions() + dto.getOtherDeductions() : 0;
                    entity.setTotalDeductions(totalDeduction);
                    Integer totalEarnings = entity.getTotalEarnings() != null ? dto.getTotalEarnings() + dto.getTotalEarnings() : 0;
                    entity.setTotalEarnings(totalEarnings);
                    entity.setNote(dto.getNote());
                    this.salaryStatementHistoryRepository.save(entity);
                }else{
                    entity = new SalaryStatementHistory();
                    CompanyDetails companyDetails = this.companyDetailsRepository.findById(dto.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found"));
                    entity.setCompanyDetails(companyDetails);
                    entity.setClockInOutId(Integer.parseInt(dto.getClockInOutId().toString()));
                    BeanUtils.copyProperties(dto, entity);
                    this.salaryStatementHistoryRepository.save(entity);
                }

                SalaryStatementMasterDto salaryStatementMasterDto = this.salaryStatementMasterService
                        .getSalaryStatementMastersByMonthAndYear(dto.getCompanyId(), dto.getMonthNumber(), dto.getYear());

                if (salaryStatementMasterDto != null) {
                    Integer currentTotalSalary = salaryStatementMasterDto.getTotalSalary() != null ? salaryStatementMasterDto.getTotalSalary() : 0;
                    Integer netSalary = dto.getNetSalary() != null ? dto.getNetSalary() : 0;
                    salaryStatementMasterDto.setTotalSalary(currentTotalSalary + netSalary);

                    Integer pfAmount = dto.getTotalPfAmount() != null ? dto.getTotalPfAmount() : 0;
                    salaryStatementMasterDto.setTotalPf(pfAmount);

                    Integer ptAmount = dto.getPtAmount() != null ? dto.getPtAmount() : 0;
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

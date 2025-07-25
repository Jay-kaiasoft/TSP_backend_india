package com.timesheetspro_api.employeeStatements.serviceImpl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import com.timesheetspro_api.common.dto.employeeStatement.EmployeeSalaryStatementDto;
import com.timesheetspro_api.common.dto.employeeStatement.SalaryStatementRequestDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import com.timesheetspro_api.common.model.overtimeRules.OvertimeRules;
import com.timesheetspro_api.common.repository.OvertimeRulesRepository;
import com.timesheetspro_api.common.repository.UserInOutRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.specification.EmployeeStatementSpecification;
import com.timesheetspro_api.employeeStatements.service.EmployeeSalaryStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "EmployeeSalaryStatementService")
public class EmployeeSalaryStatementServiceImpl implements EmployeeSalaryStatementService {

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    private UserInOutRepository userInOutRepository;

    @Autowired
    private OvertimeRulesRepository overtimeRulesRepository;

    @Override
    public List<EmployeeSalaryStatementDto> getEmployeeSalaryStatements(SalaryStatementRequestDto salaryStatementRequestDto) {
        try {
            List<EmployeeSalaryStatementDto> salaryStatementList = new ArrayList<>();
            List<CompanyEmployee> companyEmployees;

            boolean hasEmployeeFilter = salaryStatementRequestDto.getEmployeeIds() != null && !salaryStatementRequestDto.getEmployeeIds().isEmpty();
            boolean hasDepartmentFilter = salaryStatementRequestDto.getDepartmentIds() != null && !salaryStatementRequestDto.getDepartmentIds().isEmpty();

            if (!hasEmployeeFilter && !hasDepartmentFilter) {
                companyEmployees = this.companyEmployeeRepository.findAll();
            } else {
                Specification<CompanyEmployee> spec = Specification.where(null);

                if (hasEmployeeFilter) {
                    spec = spec.and(EmployeeStatementSpecification.hasEmployeeIds(salaryStatementRequestDto.getEmployeeIds()));
                }

                if (hasDepartmentFilter) {
                    spec = spec.and(EmployeeStatementSpecification.hasDepartmentIds(salaryStatementRequestDto.getDepartmentIds()));
                }

                companyEmployees = this.companyEmployeeRepository.findAll(spec);
            }

            for (CompanyEmployee employee : companyEmployees) {
                EmployeeSalaryStatementDto dto = buildEmployeeSalaryStatement(employee, salaryStatementRequestDto.getMonth());
                if (dto != null) {
                    salaryStatementList.add(dto);
                }
            }

            return salaryStatementList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private EmployeeSalaryStatementDto buildEmployeeSalaryStatement(CompanyEmployee companyEmployee, Integer month) {
        EmployeeSalaryStatementDto dto = new EmployeeSalaryStatementDto();
        Integer employeeId = companyEmployee.getEmployeeId();
        dto.setEmployeeId(employeeId);
        dto.setEmployeeName(companyEmployee.getUsername());

        if (companyEmployee.getBasicSalary() != null) {
            dto.setBasicSalary(companyEmployee.getBasicSalary() * month);
        }

        dto.setDepartmentName(companyEmployee.getDepartment().getDepartmentName());

        Integer otAmountFinal = 0;
        Integer otFinalMinutes = 0;

        long totalWorkedMillis = 0;
        Integer employeeShiftHours = companyEmployee.getCompanyShift().getTotalHours();

        List<Date[]> dateRanges = getLastNMonthDateRanges(month);
        Specification<UserInOut> userSpec = Specification.where(EmployeeStatementSpecification.hasUserIds(List.of(employeeId)));
        Specification<UserInOut> dateSpec = Specification.where(null);
        Long totalDays = 0L;

        for (Date[] range : dateRanges) {
            dateSpec = dateSpec.or(EmployeeStatementSpecification.betweenCreatedOn(range[0], range[1]));

            LocalDate start = range[0].toLocalDate();
            LocalDate end = range[1].toLocalDate();

            long daysBetween = ChronoUnit.DAYS.between(start, end) + 1;
            totalDays += daysBetween;
        }

        userSpec = userSpec.and(dateSpec);
        List<UserInOut> userInOutList = this.userInOutRepository.findAll(userSpec);

        Map<LocalDate, Long> dailyWorkedMinutes = new HashMap<>();

        if (!userInOutList.isEmpty()) {
            for (UserInOut userInOut : userInOutList) {
                Date timeIn = userInOut.getTimeIn() != null ? new Date(userInOut.getTimeIn().getTime()) : null;
                Date timeOut = userInOut.getTimeOut() != null ? new Date(userInOut.getTimeOut().getTime()) : null;

                if (timeIn != null && timeOut != null) {
                    long workedMillis = timeOut.getTime() - timeIn.getTime();
                    totalWorkedMillis += workedMillis;

                    // Safe conversion for LocalDate
                    Instant instant = new java.util.Date(timeIn.getTime()).toInstant();
                    LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                    long workedMinutes = workedMillis / (1000 * 60);

                    dailyWorkedMinutes.merge(date, workedMinutes, Long::sum);
                }

            }

            long totalWorkedMinutes = totalWorkedMillis / (1000 * 60);
            long shiftMinutes = employeeShiftHours * 60L;
            long otMinutes = Math.max(totalWorkedMinutes - shiftMinutes, 0);
            otFinalMinutes = (int) otMinutes;

            List<OvertimeRules> overtimeRules = this.overtimeRulesRepository.findByCompanyId(companyEmployee.getCompanyDetails().getId());
            if (overtimeRules != null && !overtimeRules.isEmpty()) {
                for (OvertimeRules rule : overtimeRules) {
                    String userIds = rule.getUserIds();
                    if (userIds != null && userIds.contains(String.valueOf(employeeId))) {
                        Float otPayPerSlab = rule.getOtAmount();
                        String otType = rule.getOtType();

                        if (otType.equals("Fixed Amount")) {
                            otAmountFinal = otPayPerSlab != null ? otPayPerSlab.intValue() : 0;
                        } else if (otType.equals("Fixed Amount Per Hour")) {
                            long otHours = (long) Math.ceil(otFinalMinutes / 60.0);
                            otAmountFinal = (int) (otHours * otPayPerSlab);
                        } else if (otType.equals("1x Salary")) {
                            otAmountFinal = companyEmployee.getBasicSalary() * month;
                        } else if (otType.equals("1.5x Salary")) {
                            otAmountFinal = (int) ((companyEmployee.getBasicSalary() * month) * 1.5);
                        } else if (otType.equals("2x Salary")) {
                            otAmountFinal = companyEmployee.getBasicSalary() * month * 2;
                        } else if (otType.equals("2.5x Salary")) {
                            otAmountFinal = (int) ((companyEmployee.getBasicSalary() * month) * 2.5);
                        } else if (otType.equals("3x Salary")) {
                            otAmountFinal = companyEmployee.getBasicSalary() * month * 3;
                        }
                    }
                }
            }
        }

        dto.setOverTime(otFinalMinutes);
        dto.setOtAmount(otAmountFinal);

        // PF calculation for multiple months
        Integer pfAmount = 0;
        if (companyEmployee.getIsPf()) {
            Integer basicSalary = companyEmployee.getBasicSalary();
            Integer fixedPfAmount = companyEmployee.getPfAmount();
            Integer pfPercentage = companyEmployee.getPfPercentage();

            if (fixedPfAmount != null) {
                dto.setPfAmount(fixedPfAmount);
                pfAmount = (basicSalary - fixedPfAmount) * month;
            } else if (pfPercentage != null) {
                dto.setPfPercentage(pfPercentage);
                pfAmount = ((basicSalary * pfPercentage) / 100) * month;
            }
        }
        dto.setTotalPfAmount(pfAmount);

        // PT calculation for multiple months
        Integer ptAmount = 0;
        if (companyEmployee.getIsPt()) {
            Integer monthlyPt = companyEmployee.getPtAmount() != null ? companyEmployee.getPtAmount() : 0;
            ptAmount = monthlyPt * month;
        }
        dto.setPtAmount(ptAmount);

        // Final salary calculations
        Integer totalEarnings = dto.getBasicSalary() + dto.getOtAmount();
        Integer totalDeductions = pfAmount + ptAmount;
        Integer otherDeductions = 0;

        if (companyEmployee.getCanteenType().equals("Office Type")) {
            otherDeductions = companyEmployee.getCanteenAmount() * month;
            totalDeductions += otherDeductions;
        } else {
            int heavyWorkingDays = 0;

            // Count number of days user worked > 15 hours
            for (Map.Entry<LocalDate, Long> entry : dailyWorkedMinutes.entrySet()) {
                long workedMinutes = entry.getValue();
                if (workedMinutes > 15 * 60) {
                    heavyWorkingDays++;
                }
            }

            int totalWorkingDays = totalDays.intValue();
            int lightWorkingDays = totalWorkingDays - heavyWorkingDays;

            int perDayCanteen = companyEmployee.getCanteenAmount();
            int adjustedCanteenAmount = (lightWorkingDays * perDayCanteen * 2) + (heavyWorkingDays * perDayCanteen);

            otherDeductions = adjustedCanteenAmount;
            totalDeductions += otherDeductions;
        }

        totalDeductions += otherDeductions;

        Integer netSalary = totalEarnings - totalDeductions;
        dto.setOtherDeductions(otherDeductions);
        dto.setTotalEarnings(totalEarnings);
        dto.setTotalDeductions(totalDeductions);
        dto.setNetSalary(netSalary);

        return dto;
    }


    private static List<Date[]> getLastNMonthDateRanges(int monthCount) {
        List<Date[]> result = new ArrayList<>();
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

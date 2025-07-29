package com.timesheetspro_api.employeeStatements.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import java.util.*;

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
                EmployeeSalaryStatementDto dto = buildEmployeeSalaryStatement(employee, salaryStatementRequestDto.getYear(), salaryStatementRequestDto.getMonth());
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

    private EmployeeSalaryStatementDto buildEmployeeSalaryStatement(CompanyEmployee companyEmployee, Integer year, Integer month) {
        EmployeeSalaryStatementDto dto = new EmployeeSalaryStatementDto();
        Integer employeeId = companyEmployee.getEmployeeId();
        dto.setEmployeeId(employeeId);
        dto.setCompanyId(companyEmployee.getCompanyDetails().getId());
        dto.setEmployeeName(companyEmployee.getUsername());

        if (companyEmployee.getBasicSalary() != null) {
            dto.setBasicSalary(companyEmployee.getBasicSalary());
        }

        dto.setDepartmentId(companyEmployee.getDepartment().getId());
        dto.setDepartmentName(companyEmployee.getDepartment().getDepartmentName());

        Integer otAmountFinal = 0;
        Integer otFinalMinutes = 0;
        long totalWorkedMillis = 0;
        Integer employeeShiftHours = companyEmployee.getCompanyShift().getTotalHours();

        List<Date[]> dateRanges = getDateRangeForYearAndMonth(year, month);
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

        // Skip salary statement if no In-Out records
        if (userInOutList == null || userInOutList.isEmpty()) {
            return null;
        }

        Map<LocalDate, Long> dailyWorkedMinutes = new HashMap<>();
        Set<LocalDate> workDays = new HashSet<>();

        for (UserInOut userInOut : userInOutList) {
            Date timeIn = userInOut.getTimeIn() != null ? new Date(userInOut.getTimeIn().getTime()) : null;
            Date timeOut = userInOut.getTimeOut() != null ? new Date(userInOut.getTimeOut().getTime()) : null;

            if (timeIn != null && timeOut != null) {
                long workedMillis = timeOut.getTime() - timeIn.getTime();
                totalWorkedMillis += workedMillis;

                Instant instant = new java.util.Date(timeIn.getTime()).toInstant();
                LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                long workedMinutes = workedMillis / (1000 * 60);
                dailyWorkedMinutes.merge(date, workedMinutes, Long::sum);
                workDays.add(date);
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
                    switch (otType) {
                        case "Fixed Amount":
                            otAmountFinal = otPayPerSlab != null ? otPayPerSlab.intValue() : 0;
                            break;
                        case "Fixed Amount Per Hour":
                            long otHours = (long) Math.ceil(otFinalMinutes / 60.0);
                            otAmountFinal = (int) (otHours * otPayPerSlab);
                            break;
                        case "1x Salary":
                            otAmountFinal = companyEmployee.getBasicSalary();
                            break;
                        case "1.5x Salary":
                            otAmountFinal = (int) ((companyEmployee.getBasicSalary()) * 1.5);
                            break;
                        case "2x Salary":
                            otAmountFinal = companyEmployee.getBasicSalary() * 2;
                            break;
                        case "2.5x Salary":
                            otAmountFinal = (int) ((companyEmployee.getBasicSalary()) * 2.5);
                            break;
                        case "3x Salary":
                            otAmountFinal = companyEmployee.getBasicSalary() * 3;
                            break;
                    }
                }
            }
        }

        dto.setOverTime(otFinalMinutes);
        dto.setOtAmount(otAmountFinal);

        // Calculate PF only if timeIn/out exists (userInOutList present)
        Integer pfAmount = 0;
        if (Boolean.TRUE.equals(companyEmployee.getIsPf())) {
            int daysWorked = workDays.size();
            if ("Percentage".equals(companyEmployee.getPfType())) {
                Integer pfPercentage = Optional.ofNullable(companyEmployee.getPfPercentage()).orElse(0);
                BigDecimal basicSalaryPerMonth = BigDecimal.valueOf(companyEmployee.getBasicSalary());
                BigDecimal basicSalaryPerDay = basicSalaryPerMonth.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
                BigDecimal pfAmountPerDay = basicSalaryPerDay.multiply(BigDecimal.valueOf(pfPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal totalPfAmount = pfAmountPerDay.multiply(BigDecimal.valueOf(daysWorked)).setScale(0, RoundingMode.HALF_UP);
                pfAmount = totalPfAmount.intValue();
                dto.setPfPercentage(pfPercentage);
            } else {
                Integer pfAmt = Optional.ofNullable(companyEmployee.getPfAmount()).orElse(0);
                BigDecimal monthlyPfAmount = BigDecimal.valueOf(pfAmt);
//                BigDecimal totalPfAmountForMonths = monthlyPfAmount.multiply(BigDecimal.valueOf(month));
                BigDecimal perDayPf = monthlyPfAmount.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
                BigDecimal totalPfAmount = perDayPf.multiply(BigDecimal.valueOf(daysWorked)).setScale(0, RoundingMode.HALF_UP);
                pfAmount = totalPfAmount.intValue();
                dto.setPfAmount(pfAmt);
            }
        }
        dto.setTotalPfAmount(pfAmount);

        // PT for timeIn/timeOut users only
        Integer ptAmount = 0;
        if (Boolean.TRUE.equals(companyEmployee.getIsPt())) {
            ptAmount = companyEmployee.getPtAmount();
        }
        dto.setPtAmount(ptAmount);

        Integer totalEarnings = dto.getBasicSalary() + dto.getOtAmount();
        Integer totalDeductions = pfAmount + ptAmount;
        Integer otherDeductions = 0;

        if (companyEmployee.getCanteenType().equals("Office Type")) {
            otherDeductions = companyEmployee.getCanteenAmount();
            totalDeductions += otherDeductions;
        } else {
            int heavyWorkingDays = 0;
            for (Map.Entry<LocalDate, Long> entry : dailyWorkedMinutes.entrySet()) {
                if (entry.getValue() > 15 * 60) {
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

        Integer netSalary = totalEarnings - totalDeductions;
        dto.setOtherDeductions(otherDeductions);
        dto.setTotalEarnings(totalEarnings);
        dto.setTotalDeductions(totalDeductions);
        dto.setNetSalary(netSalary);

        return dto;
    }

    private List<Date[]> getDateRangeForYearAndMonth(int year, int month) {
        List<Date[]> result = new ArrayList<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        result.add(new Date[]{ java.sql.Date.valueOf(start), java.sql.Date.valueOf(end) });
        return result;
    }

//    public List<EmployeeSalaryStatementDto> getEmployeeSalaryStatements(SalaryStatementRequestDto salaryStatementRequestDto) {
//        try {
//            List<EmployeeSalaryStatementDto> salaryStatementList = new ArrayList<>();
//            List<CompanyEmployee> companyEmployees;
//
//            boolean hasEmployeeFilter = salaryStatementRequestDto.getEmployeeIds() != null && !salaryStatementRequestDto.getEmployeeIds().isEmpty();
//            boolean hasDepartmentFilter = salaryStatementRequestDto.getDepartmentIds() != null && !salaryStatementRequestDto.getDepartmentIds().isEmpty();
//
//            if (!hasEmployeeFilter && !hasDepartmentFilter) {
//                companyEmployees = this.companyEmployeeRepository.findAll();
//            } else {
//                Specification<CompanyEmployee> spec = Specification.where(null);
//
//                if (hasEmployeeFilter) {
//                    spec = spec.and(EmployeeStatementSpecification.hasEmployeeIds(salaryStatementRequestDto.getEmployeeIds()));
//                }
//
//                if (hasDepartmentFilter) {
//                    spec = spec.and(EmployeeStatementSpecification.hasDepartmentIds(salaryStatementRequestDto.getDepartmentIds()));
//                }
//
//                companyEmployees = this.companyEmployeeRepository.findAll(spec);
//            }
//
//            for (CompanyEmployee employee : companyEmployees) {
//                EmployeeSalaryStatementDto dto = buildEmployeeSalaryStatement(employee, salaryStatementRequestDto.getYear(), salaryStatementRequestDto.getMonth());
//                if (dto != null) {
//                    salaryStatementList.add(dto);
//                }
//            }
//
//            return salaryStatementList;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//    private EmployeeSalaryStatementDto buildEmployeeSalaryStatement(CompanyEmployee companyEmployee, Integer year, Integer month) {
//        EmployeeSalaryStatementDto dto = new EmployeeSalaryStatementDto();
//        Integer employeeId = companyEmployee.getEmployeeId();
//        dto.setEmployeeId(employeeId);
//        dto.setEmployeeName(companyEmployee.getUsername());
//
//        if (companyEmployee.getBasicSalary() != null) {
//            dto.setBasicSalary(companyEmployee.getBasicSalary() * month);
//        }
//
//        dto.setDepartmentName(companyEmployee.getDepartment().getDepartmentName());
//
//        Integer otAmountFinal = 0;
//        Integer otFinalMinutes = 0;
//
//        long totalWorkedMillis = 0;
//        Integer employeeShiftHours = companyEmployee.getCompanyShift().getTotalHours();
//
//        List<Date[]> dateRanges = getDateRangeForYearAndMonth(year, month);
//        Specification<UserInOut> userSpec = Specification.where(EmployeeStatementSpecification.hasUserIds(List.of(employeeId)));
//        Specification<UserInOut> dateSpec = Specification.where(null);
//        Long totalDays = 0L;
//
//        for (Date[] range : dateRanges) {
//            dateSpec = dateSpec.or(EmployeeStatementSpecification.betweenCreatedOn(range[0], range[1]));
//            LocalDate start = range[0].toLocalDate();
//            LocalDate end = range[1].toLocalDate();
//            long daysBetween = ChronoUnit.DAYS.between(start, end) + 1;
//            totalDays += daysBetween;
//        }
//
//        userSpec = userSpec.and(dateSpec);
//        List<UserInOut> userInOutList = this.userInOutRepository.findAll(userSpec);
//
//        Map<LocalDate, Long> dailyWorkedMinutes = new HashMap<>();
//        Set<LocalDate> workDays = new HashSet<>();
//
//        if (!userInOutList.isEmpty()) {
//            for (UserInOut userInOut : userInOutList) {
//                Date timeIn = userInOut.getTimeIn() != null ? new Date(userInOut.getTimeIn().getTime()) : null;
//                Date timeOut = userInOut.getTimeOut() != null ? new Date(userInOut.getTimeOut().getTime()) : null;
//
//                if (timeIn != null && timeOut != null) {
//                    long workedMillis = timeOut.getTime() - timeIn.getTime();
//                    totalWorkedMillis += workedMillis;
//
//                    Instant instant = new java.util.Date(timeIn.getTime()).toInstant();
//                    LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
//
//                    long workedMinutes = workedMillis / (1000 * 60);
//                    dailyWorkedMinutes.merge(date, workedMinutes, Long::sum);
//
//                    workDays.add(date);
//                }
//            }
//
//            long totalWorkedMinutes = totalWorkedMillis / (1000 * 60);
//            long shiftMinutes = employeeShiftHours * 60L;
//            long otMinutes = Math.max(totalWorkedMinutes - shiftMinutes, 0);
//            otFinalMinutes = (int) otMinutes;
//
//            List<OvertimeRules> overtimeRules = this.overtimeRulesRepository.findByCompanyId(companyEmployee.getCompanyDetails().getId());
//            if (overtimeRules != null && !overtimeRules.isEmpty()) {
//                for (OvertimeRules rule : overtimeRules) {
//                    String userIds = rule.getUserIds();
//                    if (userIds != null && userIds.contains(String.valueOf(employeeId))) {
//                        Float otPayPerSlab = rule.getOtAmount();
//                        String otType = rule.getOtType();
//
//                        switch (otType) {
//                            case "Fixed Amount":
//                                otAmountFinal = otPayPerSlab != null ? otPayPerSlab.intValue() : 0;
//                                break;
//                            case "Fixed Amount Per Hour":
//                                long otHours = (long) Math.ceil(otFinalMinutes / 60.0);
//                                otAmountFinal = (int) (otHours * otPayPerSlab);
//                                break;
//                            case "1x Salary":
//                                otAmountFinal = companyEmployee.getBasicSalary() * month;
//                                break;
//                            case "1.5x Salary":
//                                otAmountFinal = (int) ((companyEmployee.getBasicSalary() * month) * 1.5);
//                                break;
//                            case "2x Salary":
//                                otAmountFinal = companyEmployee.getBasicSalary() * month * 2;
//                                break;
//                            case "2.5x Salary":
//                                otAmountFinal = (int) ((companyEmployee.getBasicSalary() * month) * 2.5);
//                                break;
//                            case "3x Salary":
//                                otAmountFinal = companyEmployee.getBasicSalary() * month * 3;
//                                break;
//                        }
//                    }
//                }
//            }
//        }
//
//        dto.setOverTime(otFinalMinutes);
//        dto.setOtAmount(otAmountFinal);
//
//        // Replaced: New PF logic
//        Integer pfAmount = 0;
//        if (Boolean.TRUE.equals(companyEmployee.getIsPf())) {
//            int daysWorked = workDays.size();
//
//            if ("Percentage".equals(companyEmployee.getPfType())) {
//                Integer pfPercentage = Optional.ofNullable(companyEmployee.getPfPercentage()).orElse(0);
//                BigDecimal basicSalaryPerMonth = BigDecimal.valueOf(companyEmployee.getBasicSalary());
//                BigDecimal basicSalaryPerDay = basicSalaryPerMonth.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
//                BigDecimal pfAmountPerDay = basicSalaryPerDay.multiply(BigDecimal.valueOf(pfPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//                BigDecimal totalPfAmount = pfAmountPerDay.multiply(BigDecimal.valueOf(daysWorked)).setScale(0, RoundingMode.HALF_UP);
//                pfAmount = totalPfAmount.intValue();
//                dto.setPfPercentage(pfPercentage);
//            } else {
//                Integer pfAmt = Optional.ofNullable(companyEmployee.getPfAmount()).orElse(0);
//                BigDecimal monthlyPfAmount = BigDecimal.valueOf(pfAmt);
//                BigDecimal totalPfAmountForMonths = monthlyPfAmount.multiply(BigDecimal.valueOf(month));
//                BigDecimal perDayPf = totalPfAmountForMonths.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
//                BigDecimal totalPfAmount = perDayPf.multiply(BigDecimal.valueOf(daysWorked)).setScale(0, RoundingMode.HALF_UP);
//                pfAmount = totalPfAmount.intValue();
//                dto.setPfAmount(pfAmt);
//            }
//        }
//        dto.setTotalPfAmount(pfAmount);
//
//        // Replaced: New PT logic
//        Integer ptAmount = 0;
//        if (Boolean.TRUE.equals(companyEmployee.getIsPt())) {
//            ptAmount = companyEmployee.getPtAmount() * month;
//        }
//        dto.setPtAmount(ptAmount);
//
//        // Final salary calculations
//        Integer totalEarnings = dto.getBasicSalary() + dto.getOtAmount();
//        Integer totalDeductions = pfAmount + ptAmount;
//        Integer otherDeductions = 0;
//
//        if (companyEmployee.getCanteenType().equals("Office Type")) {
//            otherDeductions = companyEmployee.getCanteenAmount() * month;
//            totalDeductions += otherDeductions;
//        } else {
//            int heavyWorkingDays = 0;
//            for (Map.Entry<LocalDate, Long> entry : dailyWorkedMinutes.entrySet()) {
//                if (entry.getValue() > 15 * 60) {
//                    heavyWorkingDays++;
//                }
//            }
//
//            int totalWorkingDays = totalDays.intValue();
//            int lightWorkingDays = totalWorkingDays - heavyWorkingDays;
//
//            int perDayCanteen = companyEmployee.getCanteenAmount();
//            int adjustedCanteenAmount = (lightWorkingDays * perDayCanteen * 2) + (heavyWorkingDays * perDayCanteen);
//            otherDeductions = adjustedCanteenAmount;
//            totalDeductions += otherDeductions;
//        }
//
//        Integer netSalary = totalEarnings - totalDeductions;
//        dto.setOtherDeductions(otherDeductions);
//        dto.setTotalEarnings(totalEarnings);
//        dto.setTotalDeductions(totalDeductions);
//        dto.setNetSalary(netSalary);
//
//        return dto;
//    }
//
//    private List<Date[]> getDateRangeForYearAndMonth(int year, int month) {
//        List<Date[]> result = new ArrayList<>();
//
//        LocalDate start = LocalDate.of(year, month, 1);
//        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
//
//        result.add(new Date[]{
//                java.sql.Date.valueOf(start),
//                java.sql.Date.valueOf(end)
//        });
//
//        return result;
//    }

}

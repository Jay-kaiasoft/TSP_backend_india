package com.timesheetspro_api.employeeStatements.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
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
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.common.specification.EmployeeStatementSpecification;
import com.timesheetspro_api.common.specification.UserInOutSpecification;
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

    @Autowired
    private CommonService commonService;

    @Override
    public List<EmployeeSalaryStatementDto> getEmployeeSalaryStatements(SalaryStatementRequestDto salaryStatementRequestDto) {

        try {
            List<EmployeeSalaryStatementDto> salaryStatementList = new ArrayList<>();
            List<CompanyEmployee> companyEmployees;
            Specification<CompanyEmployee> spec = Specification.where(null);
            spec.and(EmployeeStatementSpecification.hasCompanyId(salaryStatementRequestDto.getCompanyId()));

            boolean hasEmployeeFilter = salaryStatementRequestDto.getEmployeeIds() != null && !salaryStatementRequestDto.getEmployeeIds().isEmpty();
            boolean hasDepartmentFilter = salaryStatementRequestDto.getDepartmentIds() != null && !salaryStatementRequestDto.getDepartmentIds().isEmpty();

            if (!hasEmployeeFilter && !hasDepartmentFilter) {
                companyEmployees = this.companyEmployeeRepository.findAll();
            } else {

                if (hasEmployeeFilter) {
                    spec = spec.and(EmployeeStatementSpecification.hasEmployeeIds(salaryStatementRequestDto.getEmployeeIds()));
                }

                if (hasDepartmentFilter) {
                    spec = spec.and(EmployeeStatementSpecification.hasDepartmentIds(salaryStatementRequestDto.getDepartmentIds()));
                }

                companyEmployees = this.companyEmployeeRepository.findAll(spec);
            }

            for (CompanyEmployee employee : companyEmployees) {
                EmployeeSalaryStatementDto dto = buildEmployeeSalaryStatement(employee, salaryStatementRequestDto);
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

    private EmployeeSalaryStatementDto buildEmployeeSalaryStatement(CompanyEmployee companyEmployee, SalaryStatementRequestDto salaryStatementRequestDto) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        java.util.Date startDate, endDate;
        if (salaryStatementRequestDto.getStartDate() == null || salaryStatementRequestDto.getEndDate() == null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            startDate = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            endDate = calendar.getTime();
        } else {
            startDate = this.commonService.convertLocalToUtc(salaryStatementRequestDto.getStartDate(), salaryStatementRequestDto.getTimeZone(), false);
            endDate = this.commonService.convertLocalToUtc(salaryStatementRequestDto.getEndDate(), salaryStatementRequestDto.getTimeZone(), true);
        }

        EmployeeSalaryStatementDto dto = new EmployeeSalaryStatementDto();
        Integer employeeId = companyEmployee.getEmployeeId();
        dto.setEmployeeId(employeeId);
        dto.setCompanyId(companyEmployee.getCompanyDetails().getId());
        dto.setEmployeeName(companyEmployee.getUsername());

        if (companyEmployee.getBasicSalary() != null) {
            dto.setBasicSalary(companyEmployee.getBasicSalary());
        }
        if (companyEmployee.getDepartment() != null) {
            dto.setDepartmentId(companyEmployee.getDepartment().getId());
            dto.setDepartmentName(companyEmployee.getDepartment().getDepartmentName());
        }

        Integer otAmountFinal = 0;
        Integer otFinalMinutes = 0;
        long totalWorkedMillis = 0;
        Integer employeeShiftHours = companyEmployee.getCompanyShift() != null ? companyEmployee.getCompanyShift().getTotalHours() : 0;

        Specification<UserInOut> userSpec = Specification.where(EmployeeStatementSpecification.hasUserIds(List.of(employeeId)));
        Specification<UserInOut> dateSpec = Specification.where(null);
        Long totalDays = 30L;

        userSpec = userSpec.and(dateSpec);
        userSpec = userSpec.and(UserInOutSpecification.createdOnGreaterThanEqual(startDate));
        userSpec = userSpec.and(UserInOutSpecification.createdOnLessThanEqual(endDate));
        userSpec = userSpec.and(UserInOutSpecification.isSalaryGenerate());

        List<UserInOut> userInOutList = this.userInOutRepository.findAll(userSpec);

        if (userInOutList == null || userInOutList.isEmpty()) {
            return null;
        }

        Map<LocalDate, Long> dailyWorkedMinutes = new HashMap<>();
        Set<LocalDate> workDays = new HashSet<>();

        for (UserInOut userInOut : userInOutList) {
            dto.setClockInOutId(userInOut.getId());
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
        long employeeWorkedMinutes = totalWorkedMillis / (1000 * 60);
        long totalWorkedMinutes = employeeWorkedMinutes - (workDays.size() * companyEmployee.getLunchBreak());

        long shiftMinutes = employeeShiftHours * 60L;
        long otMinutes = Math.max(totalWorkedMinutes - shiftMinutes, 0);
        otFinalMinutes = (int) otMinutes;

        List<OvertimeRules> overtimeRules = this.overtimeRulesRepository.findByCompanyId(companyEmployee.getCompanyDetails().getId());
        if (overtimeRules != null && !overtimeRules.isEmpty() && otFinalMinutes > 0) {
            for (OvertimeRules rule : overtimeRules) {
                if (companyEmployee.getOvertimeRules() != null && rule.getId() == companyEmployee.getOvertimeRules().getId()) {
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
        dto.setTotalDays(totalDays.intValue());
        dto.setTotalWorkingDays(workDays.size());

        Integer pfAmount = 0;
        if (Boolean.TRUE.equals(companyEmployee.getIsPf())) {
            int daysWorked = workDays.size();
            if ("Percentage".equals(companyEmployee.getPfType())) {
                Integer pfPercentage = Optional.ofNullable(companyEmployee.getPfPercentage()).orElse(0);
                BigDecimal basicSalaryPerMonth = BigDecimal.valueOf(companyEmployee.getBasicSalary());

//                BigDecimal basicSalaryPerDay = basicSalaryPerMonth.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
                BigDecimal pfAmountPerDay = basicSalaryPerMonth.multiply(BigDecimal.valueOf(pfPercentage)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal totalPfAmount = pfAmountPerDay.multiply(BigDecimal.valueOf(daysWorked)).setScale(0, RoundingMode.HALF_UP);
                pfAmount = totalPfAmount.intValue();
                dto.setPfPercentage(pfPercentage);
            }
            if ("Fixed Amount".equals(companyEmployee.getPfType())) {
                Integer pfAmt = Optional.ofNullable(companyEmployee.getPfAmount()).orElse(0);
                BigDecimal monthlyPfAmount = BigDecimal.valueOf(pfAmt);
                pfAmount = monthlyPfAmount.intValue();
                dto.setPfAmount(pfAmt);
            }
        }
        dto.setTotalPfAmount(pfAmount > 900 ? 900 : pfAmount); // PF amount capped at 1800
        pfAmount = pfAmount > 900 ? 900 : pfAmount;

        Integer ptAmount = 0;
        if (Boolean.TRUE.equals(companyEmployee.getIsPt())) {
            ptAmount = companyEmployee.getPtAmount();
        }
        dto.setPtAmount(ptAmount);

        Integer totalEarnings = 0;
        Integer totalDeductions = pfAmount + ptAmount;
        Integer otherDeductions = 0;

        if (companyEmployee.getCanteenType().equals("Office Type")) {
            otherDeductions = companyEmployee.getCanteenAmount();
            totalDeductions += otherDeductions;
        } else {
            int heavyWorkingDays = 0;
            long workingHoursThresholdInMinutes = (long) (companyEmployee.getWorkingHoursIncludeLunch() * 60);

            for (LocalDate date : workDays) {
                Long workedMinutes = dailyWorkedMinutes.getOrDefault(date, 0L);
                if (workedMinutes > workingHoursThresholdInMinutes) {
                    heavyWorkingDays++;
                }
            }

            int totalWorkingDays = workDays.size();
            int lightWorkingDays = totalWorkingDays - heavyWorkingDays;

            int perDayCanteen = companyEmployee.getCanteenAmount();
            int adjustedCanteenAmount = (lightWorkingDays * perDayCanteen * 2) + (heavyWorkingDays * perDayCanteen);

            otherDeductions = adjustedCanteenAmount;
            totalDeductions += otherDeductions;
        }

        Long dailySalary = companyEmployee.getBasicSalary() / totalDays;
        dto.setTotalEarnSalary((int) (dailySalary * workDays.size()));
        totalEarnings = (int) (dailySalary * workDays.size()) + otAmountFinal;
        Integer netSalary = totalEarnings - totalDeductions;
        dto.setOtherDeductions(otherDeductions);
        dto.setTotalEarnings(totalEarnings);
        dto.setTotalDeductions(totalDeductions);
        dto.setNetSalary(netSalary);

        if (employeeId == 94) {
            System.out.println("Debugging Employee Salary Statement for Employee ID: " + employeeId);
            System.out.println("Start Date: " + dateFormat.format(startDate));
            System.out.println("End Date: " + dateFormat.format(endDate));
            System.out.println("Total Days: " + totalDays);
            System.out.println("Total Worked Days: " + workDays.size());
            System.out.println("Total Worked Minutes: " + totalWorkedMinutes);
            System.out.println("Overtime Minutes: " + otFinalMinutes);
            System.out.println("Overtime Amount: " + otAmountFinal);
            System.out.println("Total Earnings: " + totalEarnings);
            System.out.println("Total Deductions: " + totalDeductions);
            System.out.println("Net Salary: " + netSalary);
        }
        return dto;
    }

    private List<Date[]> getDateRangeForYearAndMonth(int year, int month) {
        List<Date[]> result = new ArrayList<>();
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        result.add(new Date[]{java.sql.Date.valueOf(start), java.sql.Date.valueOf(end)});
        return result;
    }

    public long calculateTotalDaysBetweenDates(String startDateStr, String endDateStr, String timeZone) {
        if (startDateStr == null || endDateStr == null || timeZone == null) {
            throw new IllegalArgumentException("startDate, endDate, and timeZone must not be null");
        }

        // Parse strings to java.util.Date
        java.util.Date startDate = this.commonService.convertStringToDate(startDateStr);
        java.util.Date endDate = this.commonService.convertStringToDate(endDateStr);

        // Convert to LocalDate using the timezone
        LocalDate localStartDate = startDate.toInstant()
                .atZone(ZoneId.of(timeZone))
                .toLocalDate();

        LocalDate localEndDate = endDate.toInstant()
                .atZone(ZoneId.of(timeZone))
                .toLocalDate();

        return ChronoUnit.DAYS.between(localStartDate, localEndDate) + 1; // inclusive
    }
}

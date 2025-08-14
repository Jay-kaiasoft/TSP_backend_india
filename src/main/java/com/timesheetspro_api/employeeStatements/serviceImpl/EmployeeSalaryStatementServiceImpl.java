package com.timesheetspro_api.employeeStatements.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import com.timesheetspro_api.common.dto.employeeStatement.EmployeeSalaryStatementDto;
import com.timesheetspro_api.common.dto.employeeStatement.SalaryStatementRequestDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import com.timesheetspro_api.common.model.overtimeRules.OvertimeRules;
import com.timesheetspro_api.common.model.weeklyOff.WeeklyOff;
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
import java.util.stream.Collectors;

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

        // Date range handling
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

        // Initialize DTO with basic employee info
        EmployeeSalaryStatementDto dto = new EmployeeSalaryStatementDto();
        dto.setEmployeeId(companyEmployee.getEmployeeId());
        dto.setCompanyId(companyEmployee.getCompanyDetails().getId());
        dto.setEmployeeName(companyEmployee.getUsername());

        if (companyEmployee.getBasicSalary() != null) {
            dto.setBasicSalary(companyEmployee.getBasicSalary());
        }
        if (companyEmployee.getDepartment() != null) {
            dto.setDepartmentId(companyEmployee.getDepartment().getId());
            dto.setDepartmentName(companyEmployee.getDepartment().getDepartmentName());
        }

        // Get working day configuration
        WeeklyOff workingDayConfig = companyEmployee.getWeeklyOff();

        // Calculate all working days for the period (including paid weekly-offs)
        int totalPaidDays = 0;
        if (workingDayConfig != null) {
            Set<LocalDate> allPaidDays = calculatePaidDays(startDate, endDate, workingDayConfig);
            totalPaidDays = totalPaidDays + allPaidDays.size();
        }

        // Get actual attendance data
        Specification<UserInOut> userSpec = Specification.where(EmployeeStatementSpecification.hasUserIds(List.of(companyEmployee.getEmployeeId())))
                .and(UserInOutSpecification.createdOnGreaterThanEqual(startDate))
                .and(UserInOutSpecification.createdOnLessThanEqual(endDate))
                .and(UserInOutSpecification.isSalaryGenerate());

        List<UserInOut> userInOutList = this.userInOutRepository.findAll(userSpec);
        if (userInOutList.isEmpty()) {
            return null;
        }

        // Process attendance records
        Map<LocalDate, Long> dailyWorkedMinutes = new HashMap<>();
        Set<LocalDate> actualWorkDays = new HashSet<>();
        long totalWorkedMillis = 0;

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
                actualWorkDays.add(date);
            }
        }

        // Calculate overtime
        int employeeShiftHours = companyEmployee.getCompanyShift() != null ? companyEmployee.getCompanyShift().getTotalHours() : 0;
        long employeeWorkedMinutes = totalWorkedMillis / (1000 * 60);
        long totalWorkedMinutes = employeeWorkedMinutes - (actualWorkDays.size() * companyEmployee.getLunchBreak());
        long shiftMinutes = employeeShiftHours * 60L;
        long otMinutes = Math.max(totalWorkedMinutes - (actualWorkDays.size() * shiftMinutes), 0);
        int otFinalMinutes = (int) otMinutes;
        int otAmountFinal = calculateOvertimeAmount(companyEmployee, otFinalMinutes);

        // Calculate PF
        int pfAmount = calculatePfAmount(companyEmployee);
        if (companyEmployee.getPfPercentage() != null && companyEmployee.getPfPercentage() > 0) {
            dto.setPfPercentage(companyEmployee.getPfPercentage());
        } else {
            dto.setPfAmount(companyEmployee.getPfAmount());
        }
        dto.setTotalPfAmount(pfAmount > 900 ? 900 : pfAmount);
        pfAmount = pfAmount > 900 ? 900 : pfAmount;

        // Calculate PT
        int ptAmount = Boolean.TRUE.equals(companyEmployee.getIsPt()) ? companyEmployee.getPtAmount() : 0;
        dto.setPtAmount(ptAmount);

        // Calculate canteen deductions
        int otherDeductions = calculateCanteenDeductions(companyEmployee, dailyWorkedMinutes, actualWorkDays);
        int totalDeductions = pfAmount + ptAmount + otherDeductions;

        // Calculate earnings
        long dailySalary = companyEmployee.getBasicSalary() / 30;
        int baseSalary = (int) (dailySalary * (totalPaidDays + actualWorkDays.size()));
        int totalEarnings = baseSalary + otAmountFinal;

//        if (companyEmployee.getEmployeeId() == 94) {
//            System.out.println("Debugging Employee Salary Statement for Employee ID: " + companyEmployee.getEmployeeId());
//            System.out.println("Start Date: " + dateFormat.format(startDate));
//            System.out.println("End Date: " + dateFormat.format(endDate));
//            System.out.println("Paid Days: " + totalPaidDays);
//            System.out.println("Worked Days: " + actualWorkDays.size());
//            System.out.println("Total Worked Days: " + (actualWorkDays.size() + totalPaidDays));
//            System.out.println("Total Worked Minutes: " + totalWorkedMinutes);
//            System.out.println("Overtime Minutes: " + otFinalMinutes);
//            System.out.println("Overtime Amount: " + otAmountFinal);
//            System.out.println("Total Earnings: " + totalEarnings);
//            System.out.println("Total Deductions: " + totalDeductions);
//            System.out.println("Net Salary: " + (totalEarnings - totalDeductions));
//        }

        // Set all calculated values
        dto.setOverTime(otFinalMinutes);
        dto.setOtAmount(otAmountFinal);
        dto.setTotalPaidDays(totalPaidDays);
        dto.setTotalWorkingDays(actualWorkDays.size());
        dto.setTotalDays((actualWorkDays.size() + totalPaidDays));
        dto.setTotalEarnSalary(baseSalary);
        dto.setOtherDeductions(otherDeductions);
        dto.setTotalEarnings(totalEarnings);
        dto.setTotalDeductions(totalDeductions);
        dto.setNetSalary(totalEarnings - totalDeductions);

        return dto;
    }

    private Set<LocalDate> calculatePaidDays(java.util.Date startDate, java.util.Date endDate, WeeklyOff config) {
        Set<LocalDate> paidDays = new HashSet<>();

        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();

        LocalDate start = startInstant.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endInstant.atZone(ZoneId.systemDefault()).toLocalDate();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            int dayOfMonth = date.getDayOfMonth();
            int weekOfMonth = (dayOfMonth - 1) / 7 + 1;

            boolean isWorkingDay = false;

            if (config != null) {
                switch (dayOfWeek) {
                    case SUNDAY:
                        isWorkingDay = config.isSundayAll() ||
                                (weekOfMonth == 1 && config.isSunday1st()) ||
                                (weekOfMonth == 2 && config.isSunday2nd()) ||
                                (weekOfMonth == 3 && config.isSunday3rd()) ||
                                (weekOfMonth == 4 && config.isSunday4th()) ||
                                (weekOfMonth == 5 && config.isSunday5th());
                        break;
                    case MONDAY:
                        isWorkingDay = config.isMondayAll() ||
                                (weekOfMonth == 1 && config.isMonday1st()) ||
                                (weekOfMonth == 2 && config.isMonday2nd()) ||
                                (weekOfMonth == 3 && config.isMonday3rd()) ||
                                (weekOfMonth == 4 && config.isMonday4th()) ||
                                (weekOfMonth == 5 && config.isMonday5th());
                        break;
                    case TUESDAY:
                        isWorkingDay = config.isTuesdayAll() ||
                                (weekOfMonth == 1 && config.isTuesday1st()) ||
                                (weekOfMonth == 2 && config.isTuesday2nd()) ||
                                (weekOfMonth == 3 && config.isTuesday3rd()) ||
                                (weekOfMonth == 4 && config.isTuesday4th()) ||
                                (weekOfMonth == 5 && config.isTuesday5th());
                        break;
                    case WEDNESDAY:
                        isWorkingDay = config.isWednesdayAll() ||
                                (weekOfMonth == 1 && config.isWednesday1st()) ||
                                (weekOfMonth == 2 && config.isWednesday2nd()) ||
                                (weekOfMonth == 3 && config.isWednesday3rd()) ||
                                (weekOfMonth == 4 && config.isWednesday4th()) ||
                                (weekOfMonth == 5 && config.isWednesday5th());
                        break;
                    case THURSDAY:
                        isWorkingDay = config.isThursdayAll() ||
                                (weekOfMonth == 1 && config.isThursday1st()) ||
                                (weekOfMonth == 2 && config.isThursday2nd()) ||
                                (weekOfMonth == 3 && config.isThursday3rd()) ||
                                (weekOfMonth == 4 && config.isThursday4th()) ||
                                (weekOfMonth == 5 && config.isThursday5th());
                        break;
                    case FRIDAY:
                        isWorkingDay = config.isFridayAll() ||
                                (weekOfMonth == 1 && config.isFriday1st()) ||
                                (weekOfMonth == 2 && config.isFriday2nd()) ||
                                (weekOfMonth == 3 && config.isFriday3rd()) ||
                                (weekOfMonth == 4 && config.isFriday4th()) ||
                                (weekOfMonth == 5 && config.isFriday5th());
                        break;
                    case SATURDAY:
                        isWorkingDay = config.isSaturdayAll() ||
                                (weekOfMonth == 1 && config.isSaturday1st()) ||
                                (weekOfMonth == 2 && config.isSaturday2nd()) ||
                                (weekOfMonth == 3 && config.isSaturday3rd()) ||
                                (weekOfMonth == 4 && config.isSaturday4th()) ||
                                (weekOfMonth == 5 && config.isSaturday5th());
                        break;
                }
            }

            if (isWorkingDay) {
                paidDays.add(date);
            }
        }

        return paidDays;
    }

    // Helper method to calculate overtime amount
    private int calculateOvertimeAmount(CompanyEmployee employee, int otMinutes) {
        if (otMinutes <= 0 || employee.getOvertimeRules() == null) {
            return 0;
        }

        OvertimeRules rule = employee.getOvertimeRules();
        Float otPayPerSlab = rule.getOtAmount();
        if (otPayPerSlab == null) return 0;

        switch (rule.getOtType()) {
            case "Fixed Amount":
                return otPayPerSlab.intValue();
            case "Fixed Amount Per Hour":
                long otHours = (long) Math.ceil(otMinutes / 60.0);
                return (int) (otHours * otPayPerSlab);
            case "1x Salary":
                return employee.getBasicSalary();
            case "1.5x Salary":
                return (int) (employee.getBasicSalary() * 1.5);
            case "2x Salary":
                return employee.getBasicSalary() * 2;
            case "2.5x Salary":
                return (int) (employee.getBasicSalary() * 2.5);
            case "3x Salary":
                return employee.getBasicSalary() * 3;
            default:
                return 0;
        }
    }

    // Helper method to calculate PF amount
    private int calculatePfAmount(CompanyEmployee employee) {
        if (!Boolean.TRUE.equals(employee.getIsPf())) {
            return 0;
        }

        if ("Percentage".equals(employee.getPfType())) {
            Integer pfPercentage = Optional.ofNullable(employee.getPfPercentage()).orElse(0);
            BigDecimal basicSalaryPerMonth = BigDecimal.valueOf(employee.getBasicSalary());
//          BigDecimal basicSalaryPerDay = basicSalaryPerMonth.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
            BigDecimal pfAmount = basicSalaryPerMonth
                    .multiply(BigDecimal.valueOf(pfPercentage))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return pfAmount.intValue();
        } else if ("Fixed Amount".equals(employee.getPfType())) {
            return Optional.ofNullable(employee.getPfAmount()).orElse(0);
        }
        return 0;
    }

    // Helper method to calculate canteen deductions
    private int calculateCanteenDeductions(CompanyEmployee employee, Map<LocalDate, Long> dailyWorkedMinutes, Set<LocalDate> workDays) {
        if ("Office Type".equals(employee.getCanteenType())) {
            return employee.getCanteenAmount();
        }

        int heavyWorkingDays = 0;
        long threshold = (long) (employee.getWorkingHoursIncludeLunch() * 60);
        for (LocalDate date : workDays) {
            if (dailyWorkedMinutes.getOrDefault(date, 0L) > threshold) {
                heavyWorkingDays++;
            }
        }

        int lightDays = workDays.size() - heavyWorkingDays;
        int perDayAmount = employee.getCanteenAmount();
//        if (heavyWorkingDays > 0){
//            System.out.println("============= Canteen Amount ============"+(lightDays * perDayAmount * 2) + (heavyWorkingDays * perDayAmount));
//        } else {
//            System.out.println("============= Canteen Amount ============"+(lightDays * perDayAmount * 2));
//        }
        return (lightDays * perDayAmount * 2) + (heavyWorkingDays * perDayAmount);
    }
}
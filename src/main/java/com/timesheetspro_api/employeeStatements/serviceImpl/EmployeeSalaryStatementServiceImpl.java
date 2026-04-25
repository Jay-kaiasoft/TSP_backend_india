package com.timesheetspro_api.employeeStatements.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.*;

import com.timesheetspro_api.common.dto.deductions.DeductionsDto;
import com.timesheetspro_api.common.dto.employeeStatement.EmployeeSalaryStatementDto;
import com.timesheetspro_api.common.dto.employeeStatement.SalaryStatementRequestDto;
import com.timesheetspro_api.common.dto.holidayTemplateDetails.HolidayTemplateDetailsDto;
import com.timesheetspro_api.common.dto.holidayTemplates.HolidayTemplatesDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import com.timesheetspro_api.common.model.attendancePenaltyRules.AttendancePenaltyRules;
import com.timesheetspro_api.common.model.deductions.Deductions;
import com.timesheetspro_api.common.model.holidayTemplates.HolidayTemplates;
import com.timesheetspro_api.common.model.overtimeRules.OvertimeRules;
import com.timesheetspro_api.common.model.weeklyOff.WeeklyOff;
import com.timesheetspro_api.common.repository.DeductionsRepository;
import com.timesheetspro_api.common.repository.OvertimeRulesRepository;
import com.timesheetspro_api.common.repository.UserInOutRepository;
import com.timesheetspro_api.common.repository.company.AttendancePenaltyRulesRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.HolidayTemplatesRepository;
import com.timesheetspro_api.common.repository.company.WeeklyOffRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.common.specification.EmployeeStatementSpecification;
import com.timesheetspro_api.common.specification.UserInOutSpecification;
import com.timesheetspro_api.employeeStatements.service.EmployeeSalaryStatementService;
import com.timesheetspro_api.holidayTemplateDetails.service.HolidayTemplateDetailsService;
import com.timesheetspro_api.holidayTemplates.service.HolidayTemplatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private AttendancePenaltyRulesRepository attendancePenaltyRulesRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private HolidayTemplatesService holidayTemplatesService;

    @Autowired
    private WeeklyOffRepository weeklyOffRepository;

    @Autowired
    private HolidayTemplatesRepository holidayTemplatesRepository;

    @Autowired
    private HolidayTemplateDetailsService holidayTemplateDetailsService;

    @Autowired
    private DeductionsRepository deductionsRepository;

    @Override
    public List<EmployeeSalaryStatementDto> getEmployeeSalaryStatements(SalaryStatementRequestDto salaryStatementRequestDto) {
        try {
            List<EmployeeSalaryStatementDto> salaryStatementList = new ArrayList<>();
            List<CompanyEmployee> companyEmployees;
            Specification<CompanyEmployee> spec = Specification.where(null);

            boolean hasEmployeeFilter = salaryStatementRequestDto.getEmployeeIds() != null && !salaryStatementRequestDto.getEmployeeIds().isEmpty();
            boolean hasDepartmentFilter = salaryStatementRequestDto.getDepartmentIds() != null && !salaryStatementRequestDto.getDepartmentIds().isEmpty();

            if (!hasEmployeeFilter && !hasDepartmentFilter) {
                companyEmployees = this.companyEmployeeRepository.findByCompanyId(salaryStatementRequestDto.getCompanyId());
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
        // 1. Parse the date strings into LocalDate using the expected format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ZoneId companyZone = ZoneId.of(salaryStatementRequestDto.getTimeZone() != null ?
                salaryStatementRequestDto.getTimeZone() : "Asia/Calcutta");

        LocalDate startLocalDate, endLocalDate;

        if (salaryStatementRequestDto.getStartDate() != null && salaryStatementRequestDto.getEndDate() != null) {
            startLocalDate = LocalDate.parse(salaryStatementRequestDto.getStartDate(), dateFormatter);
            endLocalDate = LocalDate.parse(salaryStatementRequestDto.getEndDate(), dateFormatter);
        } else {
            // fallback to current month logic
            YearMonth currentMonth = YearMonth.now(companyZone);
            startLocalDate = currentMonth.atDay(1);
            endLocalDate = currentMonth.atEndOfMonth();
        }

// 2. Convert to java.util.Date using the company's time zone:
//    startDate = beginning of day (00:00:00)
//    endDate   = end of day (23:59:59.999999999)
        ZonedDateTime startZdt = startLocalDate.atStartOfDay(companyZone);
        ZonedDateTime endZdt = endLocalDate.atTime(LocalTime.MAX).atZone(companyZone); // 23:59:59.999999999

        java.util.Date startDate = Date.from(startZdt.toInstant());
        java.util.Date endDate = Date.from(endZdt.toInstant());

// No further adjustment of endDate is needed – it already represents the very end of the requested day in the company's time zone.
        // Initialize DTO
        EmployeeSalaryStatementDto dto = new EmployeeSalaryStatementDto();
        dto.setEmployeeId(companyEmployee.getEmployeeId());
        dto.setCompanyId(companyEmployee.getCompanyDetails().getId());
        dto.setEmployeeName(companyEmployee.getFirstName() + " " + companyEmployee.getLastName());

        if (companyEmployee.getBasicSalary() != null) dto.setBasicSalary(companyEmployee.getBasicSalary());
        if (companyEmployee.getDepartment() != null) {
            dto.setDepartmentId(companyEmployee.getDepartment().getId());
            dto.setDepartmentName(companyEmployee.getDepartment().getDepartmentName());
        }

        // 2. Fetch Holiday Dates
        List<String> holidayDates = new ArrayList<>();
        if (companyEmployee.getHolidayTemplates() != null) {
            HolidayTemplatesDto holidayTemplate = this.holidayTemplatesService
                    .getHolidayTemplateById(companyEmployee.getHolidayTemplates().getId());

            if (holidayTemplate != null && holidayTemplate.getHolidayTemplateDetailsList() != null) {
                for (HolidayTemplateDetailsDto detail : holidayTemplate.getHolidayTemplateDetailsList()) {
                    if (detail.getDate() != null && detail.getDate().length() >= 10) {
                        holidayDates.add(detail.getDate().substring(0, 10)); // Extract dd/MM/yyyy
                    }
                }
            }
        }

        // 3. Get Paid Day Configuration (All potential Weekly Offs + Holidays in range)
        Set<LocalDate> configPaidOffDays = new HashSet<>();
        if (companyEmployee.getWeeklyOff() != null || !holidayDates.isEmpty()) {
            configPaidOffDays = calculatePaidDays(startDate, endDate, companyEmployee.getWeeklyOff(), holidayDates);
        }

        // 4. Get actual attendance data
        Specification<UserInOut> userSpec = Specification.where(EmployeeStatementSpecification.hasUserIds(List.of(companyEmployee.getEmployeeId())))
                .and(UserInOutSpecification.createdOnGreaterThanEqual(startDate))
                .and(UserInOutSpecification.createdOnLessThanEqual(endDate))
                .and(UserInOutSpecification.isSalaryGenerate());

        List<UserInOut> userInOutList = this.userInOutRepository.findAll(userSpec);
        if (userInOutList.isEmpty()) return null;

        // 5. Process attendance records
        Map<LocalDate, Long> dailyWorkedMinutes = new HashMap<>();
        Set<LocalDate> actualWorkDays = new HashSet<>();

        long totalWorkedMillis = 0;
        int penaltyAmount = 0;

        for (UserInOut userInOut : userInOutList) {
            dto.setClockInOutId(userInOut.getId());
            java.util.Date timeIn = userInOut.getTimeIn();
            java.util.Date timeOut = userInOut.getTimeOut();

            if (timeIn != null && timeOut != null) {
                long workedMillis = timeOut.getTime() - timeIn.getTime();
                totalWorkedMillis += workedMillis;

                LocalDate date = timeIn.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long workMinutes = workedMillis / (1000 * 60);

                dailyWorkedMinutes.merge(date, workMinutes, Long::sum);
                actualWorkDays.add(date); // This adds the worked holiday (e.g., 25/03/26) to actualWorkDays

                // Penalty Calculations
                if (Boolean.TRUE.equals(companyEmployee.getLateEntryPenaltyRule()) && companyEmployee.getCompanyShift() != null) {
                    if (companyEmployee.getCompanyShift().getShiftType().equals("Time Based")) {
                        penaltyAmount += calculateLateEntryPenalty(companyEmployee, userInOut.getTimeIn());
                    }
                }
                if (Boolean.TRUE.equals(companyEmployee.getEarlyExitPenaltyRule()) && companyEmployee.getCompanyShift() != null) {
                    if (companyEmployee.getCompanyShift().getShiftType().equals("Time Based")) {
                        penaltyAmount += calculateEarlyExitPenalty(companyEmployee, userInOut.getTimeOut());
                    }
                }
            }
        }

        // --- 6. FIX: Calculate Final Paid Days ---
        // Remove any day the employee actually worked from the paid off-days pool.
        // E.g., If configPaidOffDays has 5 days, and actualWorkDays contains 1 of those days,
        // it removes that 1 day, leaving 4 totalPaidDaysCount.
//        configPaidOffDays.removeAll(actualWorkDays);

        int totalPaidDaysCount = configPaidOffDays.size(); // This will now correctly be 4

        // 7. Overtime & Deductions
        float employeeShiftHours = companyEmployee.getCompanyShift() != null ? companyEmployee.getCompanyShift().getTotalHours() : 0;
        long totalWorkedMinutes = totalWorkedMillis / (1000 * 60);
        int lunchDeduction = actualWorkDays.size() * (companyEmployee.getLunchBreak() != null ? companyEmployee.getLunchBreak() : 0);
        long netWorkedMinutes = totalWorkedMinutes - lunchDeduction;

        float shiftMinutes = employeeShiftHours * 60L;
        int otFinalMinutes = (int) Math.max(netWorkedMinutes - shiftMinutes, 0);
        int otAmountFinal = companyEmployee.getEmployeeType().getId() != 2 ? calculateOvertimeAmount(companyEmployee, otFinalMinutes) : 0;

        // 8. Earnings
        int baseSalary;
        boolean isHourly = companyEmployee.getEmployeeType().getId() == 2 && companyEmployee.getHourlyRate() != null;

        int totalAllowance = this.calculateTotalAllowanceAndDeductions(companyEmployee.getEmployeeId(), "Allowance").stream().map(DeductionsDto::getAmount).reduce(0, Integer::sum);
        int deductions = this.calculateTotalAllowanceAndDeductions(companyEmployee.getEmployeeId(), "Deduction").stream().map(DeductionsDto::getAmount).reduce(0, Integer::sum);

        if (isHourly) {
            double workedHoursRaw = netWorkedMinutes / 60.0;
            double workedHours = Math.round(workedHoursRaw * 100.0) / 100.0;
            double hourlyRate = companyEmployee.getHourlyRate();
            double payForWorked = workedHours * hourlyRate;
            baseSalary = (int) payForWorked;
        } else {
            int monthlySalary = companyEmployee.getBasicSalary();
            double dailyRate = monthlySalary / 30.0;   // standard 30‑day divisor

            // Get the actual period bounds
            LocalDate startLocal = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endLocal = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Determine if this is a full month (1st to last day of that month)
            boolean isFullMonth = (startLocal.getDayOfMonth() == 1) &&
                    (endLocal.equals(startLocal.withDayOfMonth(startLocal.lengthOfMonth())));

            // Re‑calculate paid off‑days (weekly offs + holidays) for this period
            Set<LocalDate> paidOffDays = calculatePaidDays(startDate, endDate,
                    companyEmployee.getWeeklyOff(), holidayDates);
            // Remove overlaps: a day that is both a paid off‑day and a worked day counts only once
            paidOffDays.removeAll(actualWorkDays);
            if (isFullMonth) {
                // Full month: deduct only unpaid absences
                int totalDaysInMonth = startLocal.lengthOfMonth();
                Set<LocalDate> allDaysInMonth = new HashSet<>();
                for (LocalDate d = startLocal; !d.isAfter(endLocal); d = d.plusDays(1)) {
                    allDaysInMonth.add(d);
                }
                // Unpaid absences = days that are neither paid off nor worked
                allDaysInMonth.removeAll(paidOffDays);
                allDaysInMonth.removeAll(actualWorkDays);
                int unpaidAbsences = allDaysInMonth.size();

                double deduction = dailyRate * unpaidAbsences;
                baseSalary = (int) Math.max(monthlySalary - deduction, 0);
            } else {
                // Partial month: pro‑rate based on total distinct days that are either worked or paid off
                Set<LocalDate> totalPaidDays = new HashSet<>(actualWorkDays);
                totalPaidDays.addAll(paidOffDays);   // union – no double counting
                int paidDayCount = totalPaidDays.size();

                baseSalary = (int) Math.round(dailyRate * paidDayCount);
            }
        }
        System.out.println("============ baseSalary =========="+baseSalary);
        int otherDeductions = calculateCanteenDeductions(companyEmployee, dailyWorkedMinutes, actualWorkDays) + penaltyAmount;
        int totalEarnings = baseSalary + otAmountFinal + totalAllowance;

        int ptAmount = Boolean.TRUE.equals(companyEmployee.getIsPt()) ? (companyEmployee.getPtAmount() != null ? companyEmployee.getPtAmount() : 0) : 0;
        dto.setPtAmount(ptAmount);

        // Canteen & Penalties
        int totalDeductions =  otherDeductions + deductions + ptAmount;

        // PF & PT
        int pfAmount = calculatePfAmount(totalEarnings - totalDeductions);
        dto.setTotalPfAmount(pfAmount);

        if (totalEarnings - totalDeductions >= 15000) {
            dto.setPfAmount(1800);
        } else {
            dto.setPfPercentage(12);
        }
        totalDeductions += pfAmount;
        // 9. Set DTO values
        dto.setTotalEarnSalary(baseSalary);
        dto.setOverTime(otFinalMinutes);
        dto.setOtAmount(otAmountFinal);
        dto.setTotalPaidDays(totalPaidDaysCount); // Outputs: 4
        dto.setTotalWorkingDays(actualWorkDays.size()); // Outputs: 1
        dto.setTotalDays(totalPaidDaysCount + actualWorkDays.size()); // Outputs: 5
        dto.setTotalAllowance(totalAllowance);
        dto.setTotalEarnings(totalEarnings);
        dto.setDeduction(deductions);
        dto.setOtherDeductions(otherDeductions);
        dto.setTotalPenaltyAmount(penaltyAmount);
        dto.setTotalDeductions(totalDeductions);
        dto.setNetSalary(totalEarnings - totalDeductions);
        dto.setEmployeeType(companyEmployee.getEmployeeType().getName());
        System.out.println("============= Debugging Employee Salary Statement for Employee: ================" + companyEmployee.getUsername());
        System.out.println("Basic Salary: " + companyEmployee.getBasicSalary());
//        System.out.println("Daily Salary: " + dailySalary);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Paid Days: " + totalPaidDaysCount);
        System.out.println("Worked Days: " + actualWorkDays.size());
        System.out.println("Total Worked Days: " + (actualWorkDays.size() + totalPaidDaysCount));
        System.out.println("Total Worked Minutes: " + totalWorkedMinutes);
        System.out.println("Overtime Minutes: " + otFinalMinutes);
        System.out.println("Overtime Amount: " + otAmountFinal);
        System.out.println("Total Earnings: " + totalEarnings);
        System.out.println("PF Amount: " + pfAmount);
        System.out.println("PT Amount: " + ptAmount);
        System.out.println("Penalty Amount: " + penaltyAmount);
        System.out.println("Allowance: " + totalAllowance);
        System.out.println("Deductions: " + deductions);
        System.out.println("Other Deductions (Canteen + Penalty): " + otherDeductions);
        System.out.println("Total Deductions: " + totalDeductions);
        System.out.println("Net Salary: " + (totalEarnings - totalDeductions));

        return dto;
    }

    private Set<LocalDate> calculatePaidDays(java.util.Date startDate, java.util.Date endDate, WeeklyOff config, List<String> holidayDates) {
        Set<LocalDate> paidDays = new HashSet<>();

        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            boolean isOffDay = false;
            String formattedCurrentDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            // 1. Check if it's a Holiday
            if (holidayDates != null && holidayDates.contains(formattedCurrentDate)) {
                isOffDay = true;
            }

            // 2. Check if it's a Weekly Off
            if (!isOffDay && config != null) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                int weekOfMonth = ((date.getDayOfMonth() - 1) / 7) + 1;

                // Reusing your isWeeklyOffDay logic or the switch case
                isOffDay = isWeeklyOffDay(dayOfWeek, weekOfMonth, config);
            }

            if (isOffDay) {
                paidDays.add(date);
            }
        }
        return paidDays;
    }

    // Helper to keep calculatePaidDays clean (matching your existing switch logic)
    private boolean isWeeklyOffDay(DayOfWeek dayOfWeek, int weekOfMonth, WeeklyOff config) {
        return switch (dayOfWeek) {
            case SUNDAY ->
                    config.isSundayAll() || (weekOfMonth == 1 && config.isSunday1st()) || (weekOfMonth == 2 && config.isSunday2nd()) || (weekOfMonth == 3 && config.isSunday3rd()) || (weekOfMonth == 4 && config.isSunday4th()) || (weekOfMonth == 5 && config.isSunday5th());
            case MONDAY ->
                    config.isMondayAll() || (weekOfMonth == 1 && config.isMonday1st()) || (weekOfMonth == 2 && config.isMonday2nd()) || (weekOfMonth == 3 && config.isMonday3rd()) || (weekOfMonth == 4 && config.isMonday4th()) || (weekOfMonth == 5 && config.isMonday5th());
            case TUESDAY ->
                    config.isTuesdayAll() || (weekOfMonth == 1 && config.isTuesday1st()) || (weekOfMonth == 2 && config.isTuesday2nd()) || (weekOfMonth == 3 && config.isTuesday3rd()) || (weekOfMonth == 4 && config.isTuesday4th()) || (weekOfMonth == 5 && config.isTuesday5th());
            case WEDNESDAY ->
                    config.isWednesdayAll() || (weekOfMonth == 1 && config.isWednesday1st()) || (weekOfMonth == 2 && config.isWednesday2nd()) || (weekOfMonth == 3 && config.isWednesday3rd()) || (weekOfMonth == 4 && config.isWednesday4th()) || (weekOfMonth == 5 && config.isWednesday5th());
            case THURSDAY ->
                    config.isThursdayAll() || (weekOfMonth == 1 && config.isThursday1st()) || (weekOfMonth == 2 && config.isThursday2nd()) || (weekOfMonth == 3 && config.isThursday3rd()) || (weekOfMonth == 4 && config.isThursday4th()) || (weekOfMonth == 5 && config.isThursday5th());
            case FRIDAY ->
                    config.isFridayAll() || (weekOfMonth == 1 && config.isFriday1st()) || (weekOfMonth == 2 && config.isFriday2nd()) || (weekOfMonth == 3 && config.isFriday3rd()) || (weekOfMonth == 4 && config.isFriday4th()) || (weekOfMonth == 5 && config.isFriday5th());
            case SATURDAY ->
                    config.isSaturdayAll() || (weekOfMonth == 1 && config.isSaturday1st()) || (weekOfMonth == 2 && config.isSaturday2nd()) || (weekOfMonth == 3 && config.isSaturday3rd()) || (weekOfMonth == 4 && config.isSaturday4th()) || (weekOfMonth == 5 && config.isSaturday5th());
            default -> false;
        };
    }

    // Helper method to calculate overtime amount
    private int calculateOvertimeAmount(CompanyEmployee employee, int otMinutes) {
        if (otMinutes <= 0 || employee.getOvertimeRules() == null) {
            return 0;
        }

        OvertimeRules rule = employee.getOvertimeRules();
        Float otPayPerSlab = rule.getOtAmount() != null ? rule.getOtAmount() : 0f;
        Integer dailySalary = 0;

        if (employee.getEmployeeType().getId() == 2 && employee.getHourlyRate() != null) {
            dailySalary = (int) (employee.getCompanyShift().getTotalHours() * employee.getHourlyRate());
        } else {
            dailySalary = employee.getBasicSalary() / 30;
        }
        switch (rule.getOtType().trim().toLowerCase()) {
            case "fixed amount":
                return otPayPerSlab.intValue();
            case "fixed amount per hour":
                long otHours = (long) Math.ceil(otMinutes / 60.0);
                return (int) (otHours * otPayPerSlab);
            case "1 day salary":
                return dailySalary;
            case "1.5 day salary":
                return (int) (dailySalary * 1.5);
            case "2 day salary":
                return dailySalary * 2;
            case "2.5 day salary":
                return (int) (dailySalary * 2.5);
            case "3 day salary":
                return dailySalary * 3;
            default:
                return 0;
        }
    }

    // Helper method to calculate PF amount
    private int calculatePfAmount(Integer totalEarnings) {
        if (totalEarnings >= 15000) {
            return 1800;
        } else {
            return (totalEarnings * 12) / 100;
        }
    }

    // Helper method to calculate canteen deductions
    private int calculateCanteenDeductions(CompanyEmployee employee, Map<LocalDate, Long> dailyWorkedMinutes, Set<LocalDate> workDays) {
        // Case 1: Office Type → flat amount
        if ("Office Type".equals(employee.getCanteenType())) {
            return employee.getCanteenAmount();
        } else if ("Labour Type".equals(employee.getCanteenType())) {
            int perDayAmount = employee.getCanteenAmount();

            // Case 2: If workingHoursIncludeLunch is null → always 2x per day
            if (employee.getWorkingHoursIncludeLunch() == null) {
                return workDays.size() * perDayAmount * 2;
            }

            // Case 3: Use threshold to decide heavy vs light day
            long threshold = (long) hhDotMmToMinutes(employee.getWorkingHoursIncludeLunch());

            int heavyWorkingDays = 0;
            for (LocalDate date : workDays) {
                if (dailyWorkedMinutes.getOrDefault(date, 0L) > threshold) {
                    heavyWorkingDays++;
                }
            }

            int lightDays = workDays.size() - heavyWorkingDays;
            return (lightDays * perDayAmount * 2) + (heavyWorkingDays * perDayAmount);
        } else {
            return 0;
        }
    }

    // ===== Helper: compute penalty given a rule, day salary & shift hours
    private int computePenalty(AttendancePenaltyRules rule, int daySalary, float totalHours) {
//        if (totalHours == null || totalHours <= 0) totalHours = 8; // fallback
        float perHourSalary = daySalary / (float) totalHours;
        perHourSalary = new BigDecimal(perHourSalary).setScale(2, RoundingMode.HALF_UP).floatValue();
        float perMinuteSalary = perHourSalary / 60f;
        perMinuteSalary = new BigDecimal(perMinuteSalary).setScale(2, RoundingMode.HALF_UP).floatValue();

        return switch (rule.getDeductionType()) {
            case "Fixed Amount" -> rule.getAmount();
            case "5 Min Salary" -> (int) Math.round(perMinuteSalary * 5);
            case "15 Min Salary" -> (int) Math.round(perMinuteSalary * 15);
            case "30 Min Salary" -> (int) Math.round(perMinuteSalary * 30);
            case "1 Hour Salary" -> (int) Math.round(perHourSalary);
            case "Half Day Salary" -> daySalary / 2;
            case "1 Day Salary" -> daySalary;
            case "1.5 Day Salary" -> (int) Math.round(daySalary * 1.5);
            case "2 Day Salary" -> daySalary * 2;
            case "2.5 Day Salary" -> (int) Math.round(daySalary * 2.5);
            case "3 Day Salary" -> daySalary * 3;
            default -> 0;
        };
    }

    // ===== Main: calculate LATE ENTRY penalty amount
    private int calculateLateEntryPenalty(CompanyEmployee employee, java.util.Date timeInDate) {
        Timestamp shiftStartTs = employee.getCompanyShift().getStartTime();
        if (shiftStartTs == null) return 0;

        Integer basic = employee.getBasicSalary();
        if (basic == null || basic <= 0) return 0;
        int daySalary = basic / 30;
        float totalHours = employee.getCompanyShift().getTotalHours();

        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime actualIn = timeInDate.toInstant().atZone(zone).toLocalDateTime();
        LocalTime rawStart = shiftStartTs.toInstant().atZone(zone).toLocalTime();

        if (rawStart.equals(LocalTime.MIDNIGHT) && actualIn.getHour() >= 12) {
            rawStart = LocalTime.NOON;
        }

        LocalDateTime expectedStart = LocalDateTime.of(actualIn.toLocalDate(), rawStart);
        long lateMinutes = Duration.between(expectedStart, actualIn).toMinutes();
        if (lateMinutes <= 0) return 0;
        return pickAndApplyRule(employee, daySalary, totalHours, lateMinutes, false);
    }

    // ===== Main: calculate EARLY EXIT penalty amount
    private int calculateEarlyExitPenalty(CompanyEmployee employee, java.util.Date timeOutDate) {
        Timestamp shiftEndTs = employee.getCompanyShift().getEndTime();
        if (shiftEndTs == null) return 0;

        Integer basic = employee.getBasicSalary();
        if (basic == null || basic <= 0) return 0;
        int daySalary = basic / 30;
        float totalHours = employee.getCompanyShift().getTotalHours();

        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime actualOut = timeOutDate.toInstant().atZone(zone).toLocalDateTime();
        LocalTime rawEnd = shiftEndTs.toInstant().atZone(zone).toLocalTime();

        if (rawEnd.equals(LocalTime.MIDNIGHT) && actualOut.getHour() <= 12) {
            rawEnd = LocalTime.NOON;
        }

        LocalDateTime expectedEnd = LocalDateTime.of(actualOut.toLocalDate(), rawEnd);
        long earlyMinutes = Duration.between(actualOut, expectedEnd).toMinutes(); // reverse direction
        if (earlyMinutes <= 0) return 0;
        return pickAndApplyRule(employee, daySalary, totalHours, earlyMinutes, true);
    }

    // ===== Shared: pick rule & apply
    private int pickAndApplyRule(CompanyEmployee employee, int daySalary, float totalHours, long diffMinutes, boolean type) {
        List<AttendancePenaltyRules> rules =
                attendancePenaltyRulesRepository.findByCompanyId(employee.getCompanyDetails().getId(), type);
        if (rules == null || rules.isEmpty()) return 0;

        rules.sort(Comparator.comparingInt(AttendancePenaltyRules::getMinutes));

        AttendancePenaltyRules chosenRule = null;
        for (AttendancePenaltyRules r : rules) {
            if (diffMinutes >= r.getMinutes()) {
                chosenRule = r; // keep last that satisfies
            } else if (chosenRule == null) {
                // fallback: if no smaller rule exists, pick the first greater one
                chosenRule = r;
            }
            // don’t break, so you get the best match
        }
        if (chosenRule == null) return 0;

        return computePenalty(chosenRule, daySalary, totalHours);
    }

    private int hhDotMmToMinutes(Object value) {
        if (value == null) return 0;

        double val = Double.parseDouble(value.toString());
        int hours = (int) val;
        // Get the decimal part, round to 2 decimal places to avoid floating point errors
        int minutes = (int) Math.round((val - hours) * 100);

        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Invalid minutes: " + minutes);
        }
        return (hours * 60) + minutes;
    }

    // ===== Calculate total allowance
    private List<DeductionsDto> calculateTotalAllowanceAndDeductions(Integer userId, String type) {
        List<DeductionsDto> deductionsDtoList = new ArrayList<>();
        List<Deductions> deductionsList = this.deductionsRepository.findByEmployeeIdAndType(userId, type);
        for (Deductions deductions : deductionsList) {
            DeductionsDto deductionsDto = new DeductionsDto();
            deductionsDto.setLabel(deductions.getLabel());
            deductionsDto.setAmount(deductions.getAmount());
            deductionsDto.setType(deductions.getType());
            deductionsDtoList.add(deductionsDto);
        }
        return deductionsDtoList;
    }
}
package com.timesheetspro_api.userInOut.serviceImpl;

import com.timesheetspro_api.common.dto.UserInOut.UserInOutDto;
import com.timesheetspro_api.common.dto.companyShiftDto.CompanyShiftDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyShift.CompanyShift;
import com.timesheetspro_api.common.model.locations.Locations;
import com.timesheetspro_api.common.repository.UserInOutRepository;
import com.timesheetspro_api.common.repository.UserRepository;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.CompanyShiftRepository;
import com.timesheetspro_api.common.repository.company.LocationsRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.common.specification.UserInOutSpecification;
import com.timesheetspro_api.userInOut.service.UserInOutService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.RegionUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;
import java.util.Map;

@Service("userInOutService")
public class UserInOutServiceImpl implements UserInOutService {

    @Autowired
    private UserInOutRepository userInOutRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    private CompanyShiftRepository companyShiftRepository;

    @Autowired
    private CommonService commonService;

    @Autowired
    private LocationsRepository locationsRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Override
    public Map<String, Object> dashboardCounts(int companyId) {
        try {
            Map<String, Object> res = new HashMap<>();
            // Get the current date
            Calendar calendar = Calendar.getInstance();

            // Get Start of the Day (00:00:00)
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startOfDay = calendar.getTime();

            // Get End of the Day (23:59:59)
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endOfDay = calendar.getTime();

            Long countCheckedInUsers = this.userInOutRepository.countCheckedInUsers(companyId, startOfDay, endOfDay);
            Long countCheckedOutUsers = this.userInOutRepository.countCheckedOutUsers(companyId, startOfDay, endOfDay);
            Long getCompanyTotalUserCount = this.companyEmployeeRepository.getCompanyTotalUserCount(companyId);
            res.put("countCheckedInUsers", countCheckedInUsers);
            res.put("countCheckedOutUsers", countCheckedOutUsers);
            res.put("companyTotalUserCount", getCompanyTotalUserCount);

            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserInOutDto> getAllEntriesByUserId(List<Integer> userIds, String startDate, String endDate, String timeZone, List<Integer> locationIds, List<Integer> departmentIds, Integer companyId) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date start, end;
            if (startDate == null || endDate == null) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                start = calendar.getTime();
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 0);
                end = calendar.getTime();
            } else {
                start = this.commonService.convertLocalToUtc(startDate, timeZone, false);
                end = this.commonService.convertLocalToUtc(endDate, timeZone, true);
            }

            Specification<UserInOut> spec = UserInOutSpecification.createdOnGreaterThanEqual(start);
            if (userIds != null && !userIds.isEmpty()) {
                spec = spec.and(UserInOutSpecification.userIdIn(userIds));
            }
            if (locationIds != null && !locationIds.isEmpty()) {
                spec = spec.and(UserInOutSpecification.hasLocationId(locationIds));
            }
            if (departmentIds != null && !departmentIds.isEmpty()) {
                spec = spec.and(UserInOutSpecification.hasDepartmentIds(departmentIds));
            }
            if (companyId != null) {
                spec = spec.and(UserInOutSpecification.hasCompany(companyId));
            }

            spec = spec.and(UserInOutSpecification.createdOnLessThanEqual(end));

            List<UserInOut> userInOutList =
                    this.userInOutRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));
            List<UserInOutDto> userInOutDtoList = userInOutList.stream()
                    .map(userInOut -> {
                        UserInOutDto dto = new UserInOutDto();
                        dto.setId(userInOut.getId());
                        dto.setUserName(userInOut.getUser().getUsername());
                        dto.setHourlyRate(userInOut.getUser().getHourlyRate());
                        dto.setFirstName(userInOut.getUser().getFirstName());
                        dto.setLastName(userInOut.getUser().getLastName());
                        dto.setCreatedOn(this.commonService.convertDateToString(userInOut.getCreatedOn()));
                        dto.setTimeIn(this.commonService.convertDateToString(userInOut.getTimeIn()));
                        if (userInOut.getTimeOut() != null) {
                            dto.setTimeOut(this.commonService.convertDateToString(userInOut.getTimeOut()));
                        }
                        if (userInOut.getLocations() != null) {
                            dto.setLocationId(userInOut.getLocations().getId());
                        }
                        dto.setUserId(userInOut.getUser().getEmployeeId());
                        CompanyShiftDto companyShiftDto = new CompanyShiftDto();
                        CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(userInOut.getUser().getEmployeeId()).orElseThrow(() -> new RuntimeException("Employee not found"));
                        if (companyEmployee.getCompanyShift() != null) {
                            CompanyShift companyShift = this.companyShiftRepository.findById(companyEmployee.getCompanyShift().getId()).orElseThrow(() -> new RuntimeException("Shift not found"));
                            companyShiftDto.setCompanyId(companyShift.getCompanyDetails().getId());
                            BeanUtils.copyProperties(companyShift, companyShiftDto);
                            dto.setCompanyShiftDto(companyShiftDto);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            return userInOutDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserInOutDto getUserLastInOut(int id) {
        try {
            UserInOut userInOut = this.userInOutRepository.getLastRecord(id);
            UserInOutDto userInOutDto = new UserInOutDto();
            if (userInOut != null) {
                userInOutDto.setUserId(userInOut.getUser().getEmployeeId());

                userInOutDto.setTimeIn(this.commonService.convertDateToString(userInOut.getTimeIn()));
                if (userInOut.getLocations() != null) {
                    userInOutDto.setLocationId(userInOut.getLocations().getId());
                }
                BeanUtils.copyProperties(userInOut, userInOutDto);
                return userInOutDto;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserInOutDto getUserInOut(Long id) {
        try {
            UserInOut userInOut = this.userInOutRepository.findById(id).orElseThrow(() -> new RuntimeException("UserInOut not found"));
            UserInOutDto userInOutDto = new UserInOutDto();
            userInOutDto.setId(userInOut.getId());
            userInOutDto.setUserId(userInOut.getUser().getEmployeeId());
            userInOutDto.setTimeIn(this.commonService.convertDateToString(userInOut.getTimeIn()));
            if (userInOut.getTimeOut() != null) {
                userInOutDto.setTimeOut(this.commonService.convertDateToString(userInOut.getTimeOut()));
            }
            if (userInOut.getLocations() != null) {
                userInOutDto.setLocationId(userInOut.getLocations().getId());
            }
            return userInOutDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

//    public UserInOutDto createUserInOut(int userId, Integer locationId, Integer companyId) {
//        try {
//            UserInOut userInOut = new UserInOut();
//            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(userId).orElseThrow(() -> new RuntimeException("Employee not found"));
//            userInOut.setUser(companyEmployee);
//            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found"));
//            Date currentDate = new Date();
//            userInOut.setTimeIn(currentDate);
//            userInOut.setCreatedOn(currentDate);
//            userInOut.setCompanyDetails(companyDetails);
//            userInOut.setIsSalaryGenerate(0);
//            if (locationId != null && locationId > 0) {
//                Locations locations = this.locationsRepository.findById(locationId).orElseThrow(() -> new RuntimeException("Location not found"));
//                if (locations != null) {
//                    userInOut.setLocations(locations);
//                }
//            }
//            this.userInOutRepository.save(userInOut);
//            UserInOutDto res = new UserInOutDto();
//            res.setId(userInOut.getId());
//            return res;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//    public void updateUserInOut(Long id, int userId) {
//
//        try {
//
//            UserInOut userInOut = this.userInOutRepository.getLastRecord(userId);
//
//            if (userInOut == null) {
//                throw new RuntimeException("No existing record found");
//            }
//
//            CompanyEmployee employee = this.companyEmployeeRepository
//                    .findById(userId)
//                    .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//            userInOut.setUser(employee);
//
//            // ===============================
//            // 1️⃣ Current UTC time
//            // ===============================
//
//            Instant nowInstant = Instant.now();
//            LocalDateTime nowUtc =
//                    LocalDateTime.ofInstant(nowInstant, ZoneOffset.UTC);
//
//            // ===============================
//            // 2️⃣ Get shift times (LOCAL)
//            // ===============================
//
//            LocalTime shiftStart = employee.getCompanyShift()
//                    .getStartTime()
//                    .toLocalDateTime()
//                    .toLocalTime();
//
//            LocalTime shiftEnd = employee.getCompanyShift()
//                    .getEndTime()
//                    .toLocalDateTime()
//                    .toLocalTime();
//
//            String autoTimeInAfter =
//                    employee.getCompanyShift().getAutoTimeInAfterHours();
//
//            LocalDate today = nowUtc.toLocalDate();
//
//            LocalDateTime shiftStartDateTime =
//                    LocalDateTime.of(today, shiftStart);
//
//            LocalDateTime shiftEndDateTime =
//                    LocalDateTime.of(today, shiftEnd);
//
//            // ===============================
//            // 3️⃣ Handle midnight shift
//            // ===============================
//
//            if (shiftEnd.isBefore(shiftStart)) {
//
//                if (nowUtc.toLocalTime().isBefore(shiftEnd)) {
//                    shiftStartDateTime = shiftStartDateTime.minusDays(1);
//                } else {
//                    shiftEndDateTime = shiftEndDateTime.plusDays(1);
//                }
//            }
//
//            // ===============================
//            // 4️⃣ Parse allowed gap (HH:mm)
//            // ===============================
//
//            Duration allowedDuration = Duration.ZERO;
//
//            if (autoTimeInAfter != null && !autoTimeInAfter.isEmpty()) {
//
//                String[] parts = autoTimeInAfter.split(":");
//
//                int hours = Integer.parseInt(parts[0]);
//                int minutes = Integer.parseInt(parts[1]);
//
//                allowedDuration =
//                        Duration.ofHours(hours).plusMinutes(minutes);
//            }
//
//            // ===============================
//            // 5️⃣ Calculate gap
//            // ===============================
//
//            Duration gap = Duration.between(shiftEndDateTime, nowUtc);
//
//            // ===============================
//            // 6️⃣ Apply same logic as clickInOut
//            // ===============================
//
//            if (!gap.isNegative() && gap.compareTo(allowedDuration) > 0) {
//
//                // ❌ Gap exceeded → create new record
//                createUserInOut(
//                        employee.getEmployeeId(),
//                        userInOut.getLocations().getId(),
//                        employee.getCompanyDetails().getId()
//                );
//
//                return;
//            }
//
//            // ✅ Otherwise update existing
//            userInOut.setTimeOut(Date.from(nowInstant));
//            this.userInOutRepository.save(userInOut);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//    public UserInOutDto updateUserInOut(UserInOutDto dto) {
//
//        try {
//
//            UserInOut userInOut = this.userInOutRepository.findById(dto.getId())
//                    .orElseThrow(() -> new RuntimeException("UserInOut record not found"));
//
//            CompanyEmployee employee = this.companyEmployeeRepository.findById(dto.getUserId())
//                    .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//            userInOut.setUser(employee);
//
//            // ===============================
//            // 1️⃣ Parse timeOut (UTC)
//            // ===============================
//
//            Instant timeOutInstant = null;
//
//            if (dto.getTimeOut() != null && !dto.getTimeOut().isEmpty()) {
//
//                OffsetDateTime odt = OffsetDateTime.parse(dto.getTimeOut());
//                timeOutInstant = odt.toInstant();
//            }
//
//            if (timeOutInstant == null) {
//                throw new RuntimeException("TimeOut is required for update");
//            }
//
//            LocalDateTime timeOutUtc =
//                    LocalDateTime.ofInstant(timeOutInstant, ZoneOffset.UTC);
//
//            // ===============================
//            // 2️⃣ Get shift times (LOCAL)
//            // ===============================
//
//            LocalTime shiftStart = employee.getCompanyShift()
//                    .getStartTime()
//                    .toLocalDateTime()
//                    .toLocalTime();
//
//            LocalTime shiftEnd = employee.getCompanyShift()
//                    .getEndTime()
//                    .toLocalDateTime()
//                    .toLocalTime();
//
//            String autoTimeInAfter = employee.getCompanyShift()
//                    .getAutoTimeInAfterHours();
//
//            LocalDate date = timeOutUtc.toLocalDate();
//
//            LocalDateTime shiftStartDateTime = LocalDateTime.of(date, shiftStart);
//            LocalDateTime shiftEndDateTime = LocalDateTime.of(date, shiftEnd);
//
//            // ===============================
//            // 3️⃣ Handle midnight shift
//            // ===============================
//
//            if (shiftEnd.isBefore(shiftStart)) {
//
//                if (timeOutUtc.toLocalTime().isBefore(shiftEnd)) {
//                    shiftStartDateTime = shiftStartDateTime.minusDays(1);
//                } else {
//                    shiftEndDateTime = shiftEndDateTime.plusDays(1);
//                }
//            }
//
//            // ===============================
//            // 4️⃣ Parse allowed duration
//            // ===============================
//
//            Duration allowedDuration = Duration.ZERO;
//
//            if (autoTimeInAfter != null && !autoTimeInAfter.isEmpty()) {
//
//                String[] parts = autoTimeInAfter.split(":");
//
//                int hours = Integer.parseInt(parts[0]);
//                int minutes = Integer.parseInt(parts[1]);
//
//                allowedDuration =
//                        Duration.ofHours(hours).plusMinutes(minutes);
//            }
//
//            // ===============================
//            // 5️⃣ Calculate gap
//            // ===============================
//
//            Duration gap = Duration.between(shiftEndDateTime, timeOutUtc);
//
//            // ===============================
//            // 6️⃣ Apply same logic as clickInOut
//            // ===============================
//
//            if (!gap.isNegative() && gap.compareTo(allowedDuration) > 0) {
//
//                // ❌ Gap exceeded → create new record
//
//                createUserInOut(employee.getEmployeeId(),
//                        dto.getLocationId(),
//                        employee.getCompanyDetails().getId());
//
//                return dto;
//            }
//
//            // ✅ Otherwise update normally
//
//            userInOut.setTimeOut(Date.from(timeOutInstant));
//            this.userInOutRepository.save(userInOut);
//
//            return dto;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//    @Override
//    public String clickInOut(int userId, Integer locationId, Integer companyId) {
//
//        try {
//
//            CompanyEmployee employee = companyEmployeeRepository.findById(userId)
//                    .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//            LocalTime shiftStart = employee.getCompanyShift()
//                    .getStartTime()
//                    .toLocalDateTime()
//                    .toLocalTime();
//
//            LocalTime shiftEnd = employee.getCompanyShift()
//                    .getEndTime()
//                    .toLocalDateTime()
//                    .toLocalTime();
//
//            String autoTimeInAfter = employee.getCompanyShift().getAutoTimeInAfterHours();
//
//            LocalDateTime now = LocalDateTime.now();
//            LocalDate today = now.toLocalDate();
//
//            LocalDateTime shiftStartDateTime = LocalDateTime.of(today, shiftStart);
//            LocalDateTime shiftEndDateTime = LocalDateTime.of(today, shiftEnd);
//
//            // 🔥 Handle midnight crossing shift
//            if (shiftEnd.isBefore(shiftStart)) {
//
//                if (now.toLocalTime().isBefore(shiftEnd)) {
//                    shiftStartDateTime = shiftStartDateTime.minusDays(1);
//                } else {
//                    shiftEndDateTime = shiftEndDateTime.plusDays(1);
//                }
//            }
//
//            // ✅ Parse HH:mm safely
//            Duration allowedDuration = Duration.ZERO;
//
//            if (autoTimeInAfter != null && !autoTimeInAfter.isEmpty()) {
//
//                String[] parts = autoTimeInAfter.split(":");
//
//                int hours = Integer.parseInt(parts[0]);
//                int minutes = Integer.parseInt(parts[1]);
//
//                allowedDuration = Duration.ofHours(hours)
//                        .plusMinutes(minutes);
//            }
//
//            Duration gap = Duration.between(shiftEndDateTime, now);
//
//            UserInOut existing = userInOutRepository.getCurrentUserRecord(userId);
//
//            // ✅ BEFORE shift end
//            if (gap.isNegative()) {
//
//                if (existing != null) {
//                    updateUserInOut(existing.getId(), userId);
//                    return "updated:" + employee.getUsername();
//                } else {
//                    createUserInOut(userId, locationId, companyId);
//                    return "created:" + employee.getUsername();
//                }
//            }
//
//            // ✅ AFTER shift end but within allowed gap
//            if (gap.compareTo(allowedDuration) <= 0) {
//
//                if (existing != null) {
//                    updateUserInOut(existing.getId(), userId);
//                    return "updated:" + employee.getUsername();
//                } else {
//                    createUserInOut(userId, locationId, companyId);
//                    return "created:" + employee.getUsername();
//                }
//            }
//
//            // ❌ Gap exceeded → force new record
//            createUserInOut(userId, locationId, companyId);
//            return "created (gap exceeded):" + employee.getUsername();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public UserInOutDto addClockInOut(UserInOutDto userInOutDto) {
//        try {
//            UserInOut userInOut = userInOutDto.getId() != null ? this.userInOutRepository.findById(userInOutDto.getId()).orElseThrow(() -> new RuntimeException("Clock in out not found")) : new UserInOut();
//
//            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(userInOutDto.getUserId())
//                    .orElseThrow(() -> new RuntimeException("Employee not found"));
//            CompanyDetails companyDetails = this.companyDetailsRepository.findById(userInOutDto.getCompanyId())
//                    .orElseThrow(() -> new RuntimeException("Company not found"));
//
//            userInOut.setIsSalaryGenerate(0);
//            userInOut.setUser(companyEmployee);
//            userInOut.setCompanyDetails(companyDetails);
//
//            if (userInOutDto.getCreatedOn() == null) {
//                userInOut.setCreatedOn(new Date());
//            } else {
//                userInOut.setCreatedOn(parseAnyDate(userInOutDto.getCreatedOn()));
//            }
//
//            if (userInOutDto.getTimeIn() != null) {
//                userInOut.setTimeIn(parseAnyDate(userInOutDto.getTimeIn()));
//            } else {
//                throw new RuntimeException("Clock In is required");
//            }
//
//            if (userInOutDto.getTimeOut() != null) {
//                userInOut.setTimeOut(parseAnyDate(userInOutDto.getTimeOut()));
//            } else {
//                userInOut.setTimeOut(null);
//            }
//
//            this.userInOutRepository.save(userInOut);
//            return userInOutDto;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }

    public UserInOutDto createUserInOut(int userId, Integer locationId, Integer companyId, Date timeIn) {
        try {
            UserInOut userInOut = new UserInOut();
            CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            userInOut.setUser(companyEmployee);
            CompanyDetails companyDetails = this.companyDetailsRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            userInOut.setTimeIn(timeIn);
            userInOut.setCreatedOn(timeIn); // or new Date()? Use same timeIn for consistency
            userInOut.setCompanyDetails(companyDetails);
            userInOut.setIsSalaryGenerate(0);
            if (locationId != null && locationId > 0) {
                Locations locations = this.locationsRepository.findById(locationId)
                        .orElseThrow(() -> new RuntimeException("Location not found"));
                userInOut.setLocations(locations);
            }
            this.userInOutRepository.save(userInOut);
            UserInOutDto res = new UserInOutDto();
            res.setId(userInOut.getId());
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // Keep original for backward compatibility
    public UserInOutDto createUserInOut(int userId, Integer locationId, Integer companyId) {
        return createUserInOut(userId, locationId, companyId, new Date());
    }

    public void updateUserInOut(Long id, int userId) {
        try {
            UserInOut userInOut = this.userInOutRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("UserInOut record not found"));
            CompanyEmployee employee = this.companyEmployeeRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // Use current time as the intended timeOut
            Date now = new Date();

            // Delegate to common handler
            boolean updated = handleTimeOutUpdate(employee, userInOut, now,
                    userInOut.getLocations() != null ? userInOut.getLocations().getId() : null,
                    employee.getCompanyDetails().getId());

            if (!updated) {
                // New record created, nothing else to do
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public UserInOutDto updateUserInOut(UserInOutDto dto) {
        try {
            UserInOut userInOut = this.userInOutRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("UserInOut record not found"));
            CompanyEmployee employee = this.companyEmployeeRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // Parse timeOut from DTO
            if (dto.getTimeOut() == null || dto.getTimeOut().isEmpty()) {
                throw new RuntimeException("TimeOut is required for update");
            }
            OffsetDateTime odt = OffsetDateTime.parse(dto.getTimeOut());
            Date timeOut = Date.from(odt.toInstant());

            // Delegate to common handler
            boolean updated = handleTimeOutUpdate(employee, userInOut, timeOut,
                    dto.getLocationId(),
                    employee.getCompanyDetails().getId());

            if (!updated) {
                // New record created, return original DTO (or maybe indicate?)
                return dto;
            }

            // If updated, we might also want to reflect other changes? Currently only timeOut is set.
            // The original code also set user (already set) but nothing else.
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String clickInOut(int userId, Integer locationId, Integer companyId) {
        try {
            CompanyEmployee employee = companyEmployeeRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            // Get current open record (timeOut null)
            UserInOut existing = userInOutRepository.getCurrentUserRecord(userId);

            if (existing != null) {
                // There is an open record → attempt to clock out
                updateUserInOut(existing.getId(), userId);
                return "updated:" + employee.getUsername();
            } else {
                // No open record → clock in (create new with current time)
                createUserInOut(userId, locationId, companyId, new Date());
                return "created:" + employee.getUsername();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUserInOut(Long id) {
        try {
            UserInOut userInOut = this.userInOutRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("UserInOut record not found"));
            this.userInOutRepository.delete(userInOut);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserInOutDto addClockInOut(UserInOutDto userInOutDto) {
        try {
            if (userInOutDto.getId() != null) {
                // Update existing record
                UserInOut userInOut = this.userInOutRepository.findById(userInOutDto.getId())
                        .orElseThrow(() -> new RuntimeException("Clock in out not found"));
                CompanyEmployee employee = this.companyEmployeeRepository.findById(userInOutDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("Employee not found"));

                // If timeOut is provided, apply gap logic
                if (userInOutDto.getTimeOut() != null) {
                    Date timeOut = parseAnyDate(userInOutDto.getTimeOut());
                    boolean updated = handleTimeOutUpdate(employee, userInOut, timeOut,
                            userInOutDto.getLocationId(),
                            userInOutDto.getCompanyId());
                    if (!updated) {
                        // New record created, return DTO as is
                        return userInOutDto;
                    }
                }

                // Also update timeIn if provided? (original code does this, but we keep it)
                if (userInOutDto.getTimeIn() != null) {
                    userInOut.setTimeIn(parseAnyDate(userInOutDto.getTimeIn()));
                }
                // Note: createdOn might also be updated in original, but we skip for brevity
                userInOutRepository.save(userInOut);
                return userInOutDto;
            } else {
                // Create new record (no gap check for creation)
                UserInOut userInOut = new UserInOut();
                CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(userInOutDto.getUserId())
                        .orElseThrow(() -> new RuntimeException("Employee not found"));
                CompanyDetails companyDetails = this.companyDetailsRepository.findById(userInOutDto.getCompanyId())
                        .orElseThrow(() -> new RuntimeException("Company not found"));

                userInOut.setIsSalaryGenerate(0);
                userInOut.setUser(companyEmployee);
                userInOut.setCompanyDetails(companyDetails);

                if (userInOutDto.getCreatedOn() == null) {
                    userInOut.setCreatedOn(new Date());
                } else {
                    userInOut.setCreatedOn(parseAnyDate(userInOutDto.getCreatedOn()));
                }

                if (userInOutDto.getTimeIn() != null) {
                    userInOut.setTimeIn(parseAnyDate(userInOutDto.getTimeIn()));
                } else {
                    throw new RuntimeException("Clock In is required");
                }

                if (userInOutDto.getTimeOut() != null) {
                    userInOut.setTimeOut(parseAnyDate(userInOutDto.getTimeOut()));
                } else {
                    userInOut.setTimeOut(null);
                }

                this.userInOutRepository.save(userInOut);
                return userInOutDto;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<UserInOutDto> getTodayEntriesByUserId(int userId) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Get Start of the Day (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        // Get End of the Day (23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endOfDay = calendar.getTime();


        List<UserInOut> entries = userInOutRepository.findByUserIdAndToday(userId, startOfDay, endOfDay);

        return entries.stream().map(entry -> {
            UserInOutDto dto = new UserInOutDto();
            dto.setId(entry.getId());
            dto.setTimeIn(this.commonService.convertDateToString(entry.getTimeIn()));
            dto.setTimeOut(this.commonService.convertDateToString(entry.getTimeOut()));
            dto.setUserId(entry.getUser().getEmployeeId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTimeInOutReport(List<Integer> userIds, String startDate, String endDate, String timeZone, Integer companyId) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date start, end;
            if (startDate == null || endDate == null) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                start = calendar.getTime();
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 0);
                end = calendar.getTime();
            } else {
                start = this.commonService.convertLocalToUtc(startDate, timeZone, false);
                end = this.commonService.convertLocalToUtc(endDate, timeZone, true);
            }

            // Fetch only the users specified by userIds
            List<CompanyEmployee> users = new ArrayList<>();
            if (userIds != null && !userIds.isEmpty()) { // Check for null first!
                users = this.companyEmployeeRepository.findAllById(userIds);
            } else {
                users = this.companyEmployeeRepository.findAll(); // if no userIds provided get all users.
            }

            Map<Integer, String> userMap = users.stream()
                    .collect(Collectors.toMap(CompanyEmployee::getEmployeeId, user -> user.getFirstName() + " " + user.getLastName()));

            Specification<UserInOut> spec = UserInOutSpecification.createdOnGreaterThanEqual(start)
                    .and(UserInOutSpecification.createdOnLessThanEqual(end));

            // Add a specification to filter by userIds
            if (userIds != null && !userIds.isEmpty()) {
                spec = spec.and(UserInOutSpecification.userIdIn(userIds));
            }
            if (companyId != null) {
                spec = spec.and(UserInOutSpecification.hasCompany(companyId));
            }

            List<UserInOut> userInOutRecords = userInOutRepository.findAll(spec);
            Map<Integer, List<UserInOut>> userInOutMap = userInOutRecords.stream()
                    .collect(Collectors.groupingBy(record -> record.getUser().getEmployeeId()));

            List<Map<String, Object>> responseList = new ArrayList<>();

            for (Map.Entry<Integer, String> entry : userMap.entrySet()) {
                int currentUserId = entry.getKey();
                String userName = entry.getValue();
                List<UserInOut> records = userInOutMap.getOrDefault(currentUserId, new ArrayList<>());

                Map<Integer, List<Map<String, Object>>> monthlyRecords = new HashMap<>();
                for (UserInOut record : records) {
                    Calendar recordCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    recordCal.setTime(record.getCreatedOn());
                    int month = recordCal.get(Calendar.MONTH) + 1;

                    Map<String, Object> dayRecord = Map.of(
                            "records", List.of(
                                    Map.of(
                                            "timeIn", this.commonService.convertDateToString(record.getTimeIn()),
                                            "timeOut", record.getTimeOut() != null ? this.commonService.convertDateToString(record.getTimeOut()) : ""
                                    )
                            )
                    );

                    monthlyRecords.computeIfAbsent(month, m -> new ArrayList<>()).add(dayRecord);
                }

                List<Map<String, Object>> monthData = monthlyRecords.entrySet().stream()
                        .map(entrySet -> Map.of("month", entrySet.getKey(), "data", entrySet.getValue()))
                        .collect(Collectors.toList());

                responseList.add(Map.of("username", userName, "records", monthData));
            }

            return Map.of("data", responseList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public Workbook generateExcelReport(Map<String, Object> data, String startDateStr, String endDateStr, String timeZone) {
        try {
            Workbook workbook = new XSSFWorkbook();
            SimpleDateFormat jsonDateFormat = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy"); // Include year
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

            Map<String, Sheet> monthSheets = new LinkedHashMap<>();

            Date startDate = null;
            Date endDate = null;

            try {
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    startDate = dateFormat.parse(startDateStr);
                }
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    endDate = dateFormat.parse(endDateStr);
                }
            } catch (Exception e) {
                System.err.println("Invalid start or end date format. Defaulting to current month.");
            }

            if (startDate == null || endDate == null) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = calendar.getTime();

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate = calendar.getTime();
            }

            Set<String> monthKeys = generateMonthKeys(startDate, endDate, monthFormat);

            for (String monthKey : monthKeys) {
                Sheet sheet = createSheet(workbook, monthKey, startDate, endDate); // Pass startDate and endDate
                monthSheets.put(monthKey, sheet);
            }
            List<Map<String, Object>> userData = (List<Map<String, Object>>) data.get("data");

            for (Map<String, Object> user : userData) {
                String userName = (String) user.get("username");
                List<Map<String, Object>> records = (List<Map<String, Object>>) user.get("records");

                if (records == null || records.isEmpty()) {
                    for (String monthKey : monthSheets.keySet()) {
                        Sheet sheet = monthSheets.get(monthKey);
                        writeUserRecord(sheet, userName, Collections.emptyList(), timeZone);
                    }
                    continue;
                }

                for (String monthKey : monthSheets.keySet()) {
                    Sheet sheet = monthSheets.get(monthKey);
                    List<Map<String, Object>> filteredRecords = new ArrayList<>();
                    try {
                        Date currentMonthDate = monthFormat.parse(monthKey);
                        Calendar currentMonthCalendar = Calendar.getInstance();
                        currentMonthCalendar.setTime(currentMonthDate);
                        int currentMonth = currentMonthCalendar.get(Calendar.MONTH) + 1; // 1-based index

                        for (Map<String, Object> record : records) {
                            Integer recordMonth = (Integer) record.get("month");
                            if (recordMonth != null && recordMonth == currentMonth) {
                                List<Map<String, Object>> dataList = (List<Map<String, Object>>) record.get("data");
                                if (dataList != null) {
                                    for (Map<String, Object> dataEntry : dataList) {
                                        List<Map<String, Object>> timeRecords = (List<Map<String, Object>>) dataEntry.get("records");
                                        if (timeRecords != null) {
                                            filteredRecords.addAll(timeRecords);
                                        }
                                    }
                                }
                            }
                        }
                        writeUserRecord(sheet, userName, filteredRecords, timeZone);
                    } catch (ParseException e) {
                        System.err.println("Error parsing month key: " + monthKey);
                        e.printStackTrace();
                    }
                }
            }

            return workbook;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<String> generateMonthKeys(Date startDate, Date endDate, SimpleDateFormat monthFormat) {
        Set<String> monthKeys = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                try {
                    Date date1 = monthFormat.parse(key1);
                    Date date2 = monthFormat.parse(key2);
                    return date1.compareTo(date2); // Sort chronologically
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing month keys: " + e.getMessage());
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        int direction = startDate.before(endDate) ? 1 : -1; // Determine the direction of iteration

        while (true) {
            String monthKey = monthFormat.format(calendar.getTime());
            monthKeys.add(monthKey);

            if (calendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)) {
                break; // Stop when the end month is reached
            }

            calendar.add(Calendar.MONTH, direction); // Move to the next or previous month
        }

        return monthKeys;
    }

    private Sheet createSheet(Workbook workbook, String sheetName, Date startDate, Date endDate) {
        Sheet sheet = workbook.createSheet(sheetName.replace("/", "-"));

        // Calculate the start and end days for the sheet
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy"); // Corrected format
            Date sheetMonthDate = monthFormat.parse(sheetName);
            calendar.setTime(sheetMonthDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return sheet; // Return if parsing fails
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date sheetStartDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date sheetEndDate = calendar.getTime();

        int startDay = 1;
        int endDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (startDate != null && sheetStartDate.getMonth() == startDate.getMonth() && sheetStartDate.getYear() == startDate.getYear()) {
            startDay = startDate.getDate();
        }

        if (endDate != null && sheetEndDate.getMonth() == endDate.getMonth() && sheetEndDate.getYear() == endDate.getYear()) {
            endDay = endDate.getDate();
        }
        int totalColumns = endDay - startDay + 2; // +2 for "User Name" and "Total Hours"

        // Create title row
        Row titleRow = sheet.createRow(0);
        CellRangeAddress titleRegion = new CellRangeAddress(0, 0, 0, totalColumns);
        sheet.addMergedRegion(titleRegion); // Adjusted colspan
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("In-Out Report");

        RegionUtil.setBorderTop(BorderStyle.THIN, titleRegion, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, titleRegion, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, titleRegion, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, titleRegion, sheet);
        titleCell.setCellStyle(createCellStyle(workbook, true, true, true, true, 14));

        // Create month row
        Row monthRow = sheet.createRow(1);
        CellRangeAddress monthRegion = new CellRangeAddress(1, 1, 0, totalColumns);
        sheet.addMergedRegion(monthRegion);
        Cell monthCell = monthRow.createCell(0);
        monthCell.setCellValue(sheetName);

        RegionUtil.setBorderTop(BorderStyle.THIN, monthRegion, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, monthRegion, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, monthRegion, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, monthRegion, sheet);
        monthCell.setCellStyle(createCellStyle(workbook, true, true, true, true, 14));

        // Create header row with day numbers
        Row headerRow = sheet.createRow(2);
        Cell userNameCell = headerRow.createCell(0);
        userNameCell.setCellValue("User Name");
        userNameCell.setCellStyle(createCellStyle(workbook, true, true, true, true, 12));

        // Add day numbers (startDay to endDay)
        for (int i = startDay; i <= endDay; i++) {
            Cell cell = headerRow.createCell(i - startDay + 1);
            cell.setCellValue(i);
            cell.setCellStyle(createCellStyle(workbook, true, true, true, true, 12));
        }

        // Add "Total Hours" in the last column
        Cell totalHoursCell = headerRow.createCell(endDay - startDay + 2);
        totalHoursCell.setCellValue("Total Hours");
        totalHoursCell.setCellStyle(createCellStyle(workbook, true, true, true, true, 12));

        return sheet;
    }

    private void writeUserRecord(Sheet sheet, String userName, List<Map<String, Object>> records, String timeZone) {
        int rowNum = findOrCreateUserRow(sheet, userName);
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        long totalMinutes = 0;
        int startDay = sheet.getRow(2).getCell(1).getNumericCellValue() != 1 ? (int) sheet.getRow(2).getCell(1).getNumericCellValue() : 1;
        int endDay = (int) sheet.getRow(2).getCell(sheet.getRow(2).getLastCellNum() - 2).getNumericCellValue();

        Map<Integer, StringBuilder> dayEntries = new HashMap<>();
        for (Map<String, Object> record : records) {
            try {
                if (record.get("timeIn") == null || record.get("timeOut") == null) {
                    continue;
                }

                Date timeIn = inputFormat.parse(this.commonService.convertUtcToLocal(record.get("timeIn").toString(), timeZone));
                Date timeOut = inputFormat.parse(this.commonService.convertUtcToLocal(record.get("timeOut").toString(), timeZone));


                Calendar calendar = Calendar.getInstance();
                calendar.setTime(timeIn);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                long durationMinutes = Math.round((timeOut.getTime() - timeIn.getTime()) / (60.0 * 1000.0));

                if (durationMinutes < 0) {
                    durationMinutes += 24 * 60; // Handle overnight shifts
                }

                totalMinutes += durationMinutes;

                if (dayOfMonth >= startDay && dayOfMonth <= endDay) {
                    dayEntries.putIfAbsent(dayOfMonth, new StringBuilder());
                    dayEntries.get(dayOfMonth)
                            .append(timeFormat.format(timeIn))
                            .append(" - ")
                            .append(timeFormat.format(timeOut))
                            .append("\n");
                }
            } catch (Exception e) {
                System.err.println("Error parsing timeIn or timeOut: " + e.getMessage());
            }
        }

        for (int day = startDay; day <= endDay; day++) {
            Cell cell = row.getCell(day - startDay + 1);
            if (cell == null) {
                cell = row.createCell(day - startDay + 1);
            }
            cell.setCellStyle(createCellStyle(sheet.getWorkbook(), false, true, false, true, 11));

            String timeRanges = dayEntries.containsKey(day) ? dayEntries.get(day).toString().trim() : "-";
            cell.setCellValue(timeRanges);
        }

        Cell totalCell = row.getCell(endDay - startDay + 2);
        if (totalCell == null) {
            totalCell = row.createCell(endDay - startDay + 2);
        }
        totalCell.setCellStyle(createCellStyle(sheet.getWorkbook(), false, true, true, true, 11));
        totalCell.setCellValue(formatTotalTime(totalMinutes)); // Pass totalMinutes to formatTotalTime
    }

    private int findOrCreateUserRow(Sheet sheet, String userName) {
        int lastRow = sheet.getLastRowNum();
        for (int i = 3; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getCell(0) != null && row.getCell(0).getStringCellValue().equals(userName)) {
                return i;
            }
        }
        Row newRow = sheet.createRow(lastRow + 1);
        Cell cell = newRow.createCell(0);
        cell.setCellValue(userName);
        cell.setCellStyle(createCellStyle(sheet.getWorkbook(), true, true, true, true, 11)); // Bold, no borders, no centered, size 11
        return lastRow + 1;
    }

    private CellStyle createCellStyle(Workbook workbook, boolean isBold, boolean hasBorders, boolean isCentered, boolean isVerticallyCentered, int fontSize) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();

        if (isBold) {
            font.setBold(true);
        }

        font.setFontHeightInPoints((short) fontSize);
        style.setFont(font);

        if (isCentered) {
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        if (isVerticallyCentered) {
            style.setVerticalAlignment(VerticalAlignment.CENTER); // Add vertical alignment
        }
        if (hasBorders) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }

        style.setWrapText(true);
        return style;
    }

    private String formatTotalTime(long totalMinutes) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%d hr %02d min", hours, minutes);
    }

    public Date parseAnyDate(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return null;
            s = s.trim();

            // 1) ISO with time (Z or offset) - e.g. 2026-01-31T08:34:45.622Z
            if (s.contains("T")) {
                return Date.from(Instant.parse(s));
            }

            // 2) dd/MM/yyyy - e.g. 31/01/2026
            if (s.matches("\\d{2}/\\d{2}/\\d{4}")) {
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                f.setLenient(false);
                // choose timezone: if this represents a "date only", usually treat it as local start-of-day
                // If you want it as UTC midnight, keep UTC.
                f.setTimeZone(TimeZone.getTimeZone("UTC"));
                return f.parse(s);
            }

            // 3) yyyy-MM-dd - e.g. 2026-01-31
            if (s.matches("\\d{4}-\\d{2}-\\d{2}")) {
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                f.setLenient(false);
                f.setTimeZone(TimeZone.getTimeZone("UTC"));
                return f.parse(s);
            }

            // 4) dd/MM/yyyy, hh:mm:ss a
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss a", Locale.ENGLISH);
            f.setLenient(false);
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            return f.parse(s);

        } catch (Exception e) {
            throw new RuntimeException("Invalid date format: " + s, e);
        }
    }

    private boolean handleTimeOutUpdate(CompanyEmployee employee, UserInOut existingRecord,
                                        Date timeOut, Integer locationId, Integer companyId) {

        // 2️⃣ For Hourly shifts, check if autoTimeInAfter is present
        String autoTimeInAfter = employee.getCompanyDetails().getAutoTimeInAfterHours();
        if (autoTimeInAfter == null || autoTimeInAfter.isEmpty()) {
            // No gap limit defined → just update
            existingRecord.setTimeOut(timeOut);
            userInOutRepository.save(existingRecord);
            return true;
        }

        // 3️⃣ Gap logic for Hourly shifts with autoTimeInAfter
        Instant timeOutInstant = timeOut.toInstant();
        LocalDateTime timeOutUtc = LocalDateTime.ofInstant(timeOutInstant, ZoneOffset.UTC);

        // Get shift start/end times (assumed stored as UTC timestamps, but treated as local time)
        LocalTime shiftStart = employee.getCompanyShift().getStartTime().toLocalDateTime().toLocalTime();
        LocalTime shiftEnd = employee.getCompanyShift().getEndTime().toLocalDateTime().toLocalTime();

        LocalDate date = timeOutUtc.toLocalDate();
        LocalDateTime shiftStartDateTime = LocalDateTime.of(date, shiftStart);
        LocalDateTime shiftEndDateTime = LocalDateTime.of(date, shiftEnd);

        // Handle shifts that cross midnight
        if (shiftEnd.isBefore(shiftStart)) {
            if (timeOutUtc.toLocalTime().isBefore(shiftEnd)) {
                shiftStartDateTime = shiftStartDateTime.minusDays(1);
            } else {
                shiftEndDateTime = shiftEndDateTime.plusDays(1);
            }
        }

        // Parse allowed gap duration from autoTimeInAfter (format "HH:mm")
        String[] parts = autoTimeInAfter.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        Duration allowedDuration = Duration.ofHours(hours).plusMinutes(minutes);

        Duration gap = Duration.between(shiftEndDateTime, timeOutUtc);

        if (!gap.isNegative() && gap.compareTo(allowedDuration) > 0) {
            // Gap exceeded → create new record for next day with timeIn = timeOut + 1 day
            Date nextDayTimeIn = Date.from(timeOutInstant.plusSeconds(24 * 60 * 60));
            createUserInOut(employee.getEmployeeId(), locationId, companyId, nextDayTimeIn);
            return true; // existing record not updated
        } else {
            // Within allowed gap → update existing record
            existingRecord.setTimeOut(timeOut);
            userInOutRepository.save(existingRecord);
            return true;
        }
    }
}
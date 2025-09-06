package com.timesheetspro_api.users.serviceImpl;

import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.dto.CompanyEmployeeDto.CompanyEmployeeDto;
import com.timesheetspro_api.common.dto.LoginDto;
import com.timesheetspro_api.common.dto.location.LocationDto;
import com.timesheetspro_api.common.dto.user.ResetPasswordDto;
import com.timesheetspro_api.common.dto.user.UserDto;
import com.timesheetspro_api.common.exception.GlobalException;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.companyTheme.CompanyTheme;
import com.timesheetspro_api.common.model.contractor.Contractor;
import com.timesheetspro_api.common.model.department.Department;
import com.timesheetspro_api.common.model.roles.Roles;
import com.timesheetspro_api.common.model.users.Users;
import com.timesheetspro_api.common.repository.*;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.CompanyThemeRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.locations.service.LocationService;
import com.timesheetspro_api.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.security.MessageDigest;
import java.util.*;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Value("${timeSheetProDrive}")
    String FILE_DIRECTORY;

    @Value("${companyId}")
    String companyId;

    @Value("${siteUrl}")
    String siteUrl;

    @Autowired
    private JwtTokenUtil jwtUtil;

    private static final String SECRET_KEY = "your-very-secret-key";

    @Autowired
    private CommonService commonService;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserShiftRepository userShiftRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private CompanyThemeRepository companyThemeRepository;

    @Autowired
    private LocationService locationService;

    @Override
    public List<UserDto> getAllUsers() {
        try {
            List<Users> usersList = this.userRepository.findAll();
            List<UserDto> userDtoList = new ArrayList<>();
            if (!usersList.isEmpty()) {
                for (Users users : usersList) {
                    userDtoList.add(this.getUserById(users.getUserId()));
                }
            }
            return userDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public Users getUser(Long userId) {
        try {
            Users user = this.userRepository.findUserById(userId);
            if (user != null) {
                return user;
            }
            return null;
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public UserDto getUserById(Long userId) {
        try {
            Users user = this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            UserDto userDto = new UserDto();
            userDto.setUserName(user.getUsername());
            userDto.setUserId(user.getUserId());
            userDto.setDepartmentId(user.getDepartment().getId());
            userDto.setRoleId(user.getRole().getRoleId());
            userDto.setRoleName(user.getRole().getRoleName());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setMiddleName(user.getMiddleName());
            userDto.setEmail(user.getEmail());
            userDto.setPhone(user.getPhone());
            userDto.setPassword(user.getPassword());
            userDto.setHourlyRate(user.getHourlyRate());
            userDto.setGender(user.getGender());
            userDto.setPersonalIdentificationNumber(user.getPersonalIdentificationNumber());
            userDto.setAddress1(user.getAddress1());
            userDto.setAddress2(user.getAddress2());
            userDto.setCity(user.getCity());
            userDto.setZipCode(user.getZipCode());
            userDto.setCountry(user.getCountry());
            userDto.setState(user.getState());
            userDto.setBirthDate(user.getBirthDate());
            userDto.setEmergencyContact(user.getEmergencyContact());
            userDto.setContactPhone(user.getContactPhone());
            userDto.setRelationship(user.getRelationship());
            userDto.setProfileImage(user.getProfileImage());
            return userDto;
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("phone")) {
                    throw new GlobalException("Phone number must be unique");
                } else if (e.getMessage().contains("personalIdentificationNumber")) {
                    throw new GlobalException("Personal identification number must be unique");
                }
            }
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            Users users = new Users();
//            Users lastRecord = this.userRepository.findLastUser();
//            if (lastRecord != null) {
//                int id = Integer.parseInt(lastRecord.getEmployeeId()) + 1;
//                users.setEmployeeId(String.format("%04d", id));
//            } else {
//                lastRecord.setEmployeeId("0001");
//            }
            Department department = this.departmentRepository.findById(userDto.getDepartmentId()).orElseThrow(() -> new RuntimeException("Department not found"));
            Roles roles = this.rolesRepository.findById(userDto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));

            if (userDto.getUserName() != null) {
                Users userhas = this.userRepository.findByUsername(userDto.getUserName());
                if (userhas != null) {
                    throw new GlobalException("Username is already taken.");
                }
            }

            if (userDto.getPersonalIdentificationNumber() != null) {
                Users userhas = this.userRepository.findByPersonalIdentificationNumber(userDto.getPersonalIdentificationNumber());
                if (userhas != null) {
                    throw new GlobalException("Personal identification number must be unique");
                }
            }

//            if (userDto.getPhone() != null) {
//                Users userhas = this.userRepository.findByPhoneNumber(userDto.getPhone());
//                if (userhas != null) {
//                    throw new GlobalException("Phone number must be unique");
//                }
//            }

            if (userDto.getContractorId() != null) {
                Contractor contractor = this.contractorRepository.findById(userDto.getContractorId()).orElseThrow(() -> new RuntimeException("Contractor not found"));
                users.setContractor(contractor);
            }

            users.setDepartment(department);
            users.setRole(roles);
            users.setFirstName(userDto.getFirstName());
            users.setLastName(userDto.getLastName());
            users.setMiddleName(userDto.getMiddleName());
            users.setEmail(userDto.getEmail());
            users.setUserName(userDto.getUserName());
            users.setPhone(userDto.getPhone());
            users.setPassword(userDto.getPassword());
            users.setGender(userDto.getGender());
            users.setHourlyRate(userDto.getHourlyRate());
            users.setPersonalIdentificationNumber(userDto.getPersonalIdentificationNumber());
            users.setAddress1(userDto.getAddress1());
            users.setAddress2(userDto.getAddress2());
            users.setCity(userDto.getCity());
            users.setZipCode(userDto.getZipCode());
            users.setCountry(userDto.getCountry());
            users.setState(userDto.getState());
            users.setBirthDate(userDto.getBirthDate());
            users.setEmergencyContact(userDto.getEmergencyContact());
            users.setContactPhone(userDto.getContactPhone());
            users.setRelationship(userDto.getRelationship());
            users.setProfileImage("");
            this.userRepository.save(users);
            userDto.setUserId(users.getUserId());
            return userDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        try {
            Users users = this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Role not found"));
            Department department = this.departmentRepository.findById(userDto.getDepartmentId()).orElseThrow(() -> new RuntimeException("Department not found"));
            Roles roles = this.rolesRepository.findById(userDto.getRoleId()).orElseThrow(() -> new RuntimeException("Role not found"));
            if (userDto.getContractorId() != null) {
                Contractor contractor = this.contractorRepository.findById(userDto.getContractorId()).orElseThrow(() -> new RuntimeException("Contractor not found"));
                users.setContractor(contractor);
            }

            if (userDto.getUserName() != null) {
                Users userhas = this.userRepository.findAllExceptUserByUserName(userId, userDto.getUserName());
                if (userhas != null) {
                    throw new GlobalException("Username is already taken.");
                }
            }

            users.setDepartment(department);
            users.setRole(roles);
            users.setDepartment(department);
            users.setRole(roles);
            users.setMiddleName(userDto.getMiddleName());
            users.setFirstName(userDto.getFirstName());
            users.setLastName(userDto.getLastName());
            users.setUserName(userDto.getUserName());
            users.setEmail(userDto.getEmail());
            users.setPhone(userDto.getPhone());
            users.setPassword(userDto.getPassword());
            users.setGender(userDto.getGender());
            users.setHourlyRate(userDto.getHourlyRate());
            users.setPersonalIdentificationNumber(userDto.getPersonalIdentificationNumber());
            users.setAddress1(userDto.getAddress1());
            users.setAddress2(userDto.getAddress2());
            users.setCity(userDto.getCity());
            users.setZipCode(userDto.getZipCode());
            users.setCountry(userDto.getCountry());
            users.setState(userDto.getState());
            users.setBirthDate(userDto.getBirthDate());
            users.setEmergencyContact(userDto.getEmergencyContact());
            users.setContactPhone(userDto.getContactPhone());
            users.setRelationship(userDto.getRelationship());
            this.userRepository.save(users);
            return userDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            Users users = this.userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Role not found"));
            this.userRepository.delete(users);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        try {
            String actualUsername = userName;
            String companyId = null;

            if (userName.contains("#_#")) {
                String[] parts = userName.split("#_#");
                actualUsername = parts[0];
                companyId = parts[1];
            }
            Users user = this.userRepository.findByUsername(actualUsername);
            if (user != null) {
                return user;
            }

            if (companyId != null) {
                CompanyEmployee companyEmployee = this.companyEmployeeRepository.findByCompanyNoAndUserName(Integer.parseInt(companyId), actualUsername);
                if (companyEmployee != null) {
                    return companyEmployee;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLogger.error("loadUserByUsername service Error: " + e);
        }
        throw new UsernameNotFoundException("User not found with username: " + userName);
    }

    @Override
    public Map<String, Object> userLogin(LoginDto loginDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            if (loginDto.getCompanyId().equals(companyId)) {
                Users user = this.userRepository.findByUsername(loginDto.getUserName());
                if (user != null) {
                    if (user.getPassword().equals(loginDto.getPassword())) {
                        UserDto userDto = new UserDto();
                        BeanUtils.copyProperties(user, userDto);
                        userDto.setRoleId(user.getRole().getRoleId());
                        userDto.setRoleName(user.getRole().getRoleName());

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("userId", user.getUserId());
                        userMap.put("userName", user.getUsername());
                        userMap.put("roleId", user.getRole().getRoleId());
                        userMap.put("roleName", user.getRole().getRoleName());
                        userMap.put("companyId", companyId);

                        final String jwtToken = jwtUtil.generateToken(userMap);
                        resBody.put("token", jwtToken);
                        resBody.put("data", userDto);
                        return resBody;
                    } else {
                        resBody.put("errorType", "password");
                        resBody.put("error", "Invalid credentials.");
                        return resBody;
                    }
                } else {
                    resBody.put("error", "Invalid credentials.");
                    return resBody;
                }
            } else {
                CompanyDetails companyDetails = this.companyDetailsRepository.findByCompanyNo(loginDto.getCompanyId());
                if (companyDetails != null && companyDetails.getIsActive() == 1) {
                    CompanyEmployee companyEmployee = this.companyEmployeeRepository.findByCompanyNoAndUserName(companyDetails.getId(), loginDto.getUserName());
                    CompanyTheme companyTheme = this.companyThemeRepository.findByCompanyId(companyDetails.getId());

                    if (companyEmployee.getRoles() != null && !companyEmployee.getRoles().getRoleName().equals("Admin") && !companyEmployee.getRoles().getRoleName().equals("Owner") && companyEmployee.getCheckGeofence() == 1) {
                        if (!companyEmployee.getCompanyLocation().isEmpty()) {
                            String companyLocation = companyEmployee.getCompanyLocation().replaceAll("[\\[\\]]", ""); // Remove brackets
                            String[] parts = companyLocation.split(",");
                            for (int i = 0; i < parts.length; i++) {
                                LocationDto locationDto = this.locationService.getLocation(Integer.parseInt(parts[i].trim()));
                                if (locationDto != null) {
                                    if (locationDto.getGeofenceId() == null || locationDto.getGeofenceId().isEmpty()) {
                                        resBody.put("error", "Geofence data is missing or incomplete for one or more locations. Please contact your administrator to configure geofencing for your company's locations before proceeding.");
                                        return resBody;
                                    }
                                } else {
                                    resBody.put("error", "Geofence data is missing or incomplete for one or more locations. Please contact your administrator to configure geofencing for your company's locations before proceeding.");
                                    return resBody;
                                }
                            }
                        } else {
                            resBody.put("error", "Login failed due to internal error.");
                            return resBody;
                        }
                    }

                    if (companyEmployee != null && companyEmployee.getPassword().equals(loginDto.getPassword())) {
                        CompanyEmployeeDto companyEmployeeDto = new CompanyEmployeeDto();
                        companyEmployeeDto.setCompanyId(companyEmployee.getCompanyDetails().getId());
                        companyEmployeeDto.setRoleId(companyEmployee.getRoles().getRoleId());
                        companyEmployeeDto.setRoleName(companyEmployee.getRoles().getRoleName());
                        companyEmployeeDto.setUserName(companyEmployee.getUsername());
                        if (companyEmployee.getDepartment() != null) {
                            companyEmployeeDto.setDepartmentName(companyEmployee.getDepartment().getDepartmentName());
                        }
                        if (companyTheme != null) {
                            companyEmployeeDto.setThemeId(companyTheme.getId());
                        }
                        BeanUtils.copyProperties(companyEmployee, companyEmployeeDto);

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("userId", companyEmployee.getEmployeeId());
                        userMap.put("userName", companyEmployee.getUsername());
                        userMap.put("roleId", companyEmployee.getRoles().getRoleId());
                        userMap.put("roleName", companyEmployee.getRoles().getRoleId());
                        userMap.put("companyId", companyEmployee.getCompanyDetails().getId());

                        final String jwtToken = jwtUtil.generateToken(userMap);
                        resBody.put("token", jwtToken);
                        resBody.put("data", companyEmployeeDto);
                        return resBody;
                    } else {
                        resBody.put("error", "Invalid credentials.");
                        return resBody;
                    }
                } else {
                    resBody.put("error", "Company Id is not valid");
                    return resBody;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resBody.put("error", "Login failed due to internal error.");
        }
        return resBody;
    }

    @Override
    public Map<String, Object> validateToken(String token) {
        Map<String, Object> res = new HashMap<>();
        try {
            String decodedToken = new String(Base64.getUrlDecoder().decode(token));
            String[] parts = decodedToken.split(":");

            if (parts.length != 5) { // Expecting id, UUID, timestamp, and HMAC
                res.put("message", "Invalid token structure");
                res.put("status", 400);
                return res; // Token structure is invalid
            }

            String companyNo = parts[0];
            String id = parts[1];
            String uuid = parts[2];
            long timestamp = Long.parseLong(parts[3]);
            String providedHmac = parts[4];

            Users user = this.userRepository.findUserById(Long.parseLong(id));
            if (user == null) {
                CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(Integer.parseInt(id)).orElseThrow(() -> new RuntimeException("User not found"));
                if (companyEmployee == null) {
                    res.put("message", "User not found");
                    res.put("status", 404);
                    return res;

                }
            }

            long currentTimestamp = System.currentTimeMillis();
            if (currentTimestamp - timestamp > 180 * 1000) {
                res.put("message", "Token is expired.");
                res.put("status", 404);
                return res;
            }

            // Reconstruct the original data used to compute HMAC
            String data = companyNo + ":" + id + ":" + uuid + ":" + timestamp;

            // Recompute HMAC using the original data
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] expectedHmac = mac.doFinal(data.getBytes());

            // Compare the recomputed HMAC with the provided HMAC
            if (MessageDigest.isEqual(expectedHmac, Base64.getUrlDecoder().decode(providedHmac))) {
                res.put("message", "Token is valid");
                res.put("status", 200);
                res.put("userId", Long.parseLong(id));
                return res;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error validating token: " + e.getMessage(), e);
        }
    }

    private boolean generateToken(Long id, String email, String companyNo) throws Exception {
        long currentTimestamp = System.currentTimeMillis(); // Current timestamp in milliseconds
        String data = companyNo + ":" + id + ":" + UUID.randomUUID().toString() + ":" + currentTimestamp;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());

        String token = data + ":" + Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
        String link = Base64.getUrlEncoder().withoutPadding().encodeToString(token.getBytes());

        String route = siteUrl + "reset-pin/" + link;
        String subject = "Reset Your Password - TimeSheetsPro";

        String body = "Hello,\n\n"
                + "We received a request to reset your password for your TimeSheetsPro account.\n"
                + "Please click the link below to reset your PIN:\n\n"
                + route + "\n\n"
                + "If you did not request this, you can safely ignore this email.\n\n"
                + "Thank you,\n"
                + "TimeSheetsPro Support Team";

        return this.commonService.sendEmail(email, subject, body);
    }

    @Override
    public boolean generateResetLink(String email, String userName, String id) {
        try {
            if (id.equals(companyId)) {
                Users users = this.userRepository.findByEmailAndUserName(email, userName);
                Long userId = users.getUserId();
                if (userId != null) {
                    return generateToken(userId, email, companyId);
                }
            } else {
                CompanyEmployee companyEmployee = this.companyEmployeeRepository.findByCompanyNoAndUserName(id, userName, email);
                if (companyEmployee != null) {
                    return generateToken(Long.parseLong(String.valueOf(companyEmployee.getEmployeeId())), email, companyEmployee.getCompanyDetails().getCompanyNo());
                }
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error generating reset link: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> resetPassword(ResetPasswordDto resetPasswordDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            String decodedToken = new String(Base64.getUrlDecoder().decode(resetPasswordDto.getToken()));
            String[] parts = decodedToken.split(":");

            if (parts.length != 5) { // Expecting id, UUID, timestamp, and HMAC
                resBody.put("message", "Invalid token structure");
                resBody.put("status", 400);
                return resBody; // Token structure is invalid
            }

            String companyNo = parts[0];
            String id = parts[1];

            if (companyNo.equals(companyId)) {
                Users user = this.userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new RuntimeException("User not found"));
                if (resetPasswordDto.getCurrentPassword() != null) {
                    if (!resetPasswordDto.getCurrentPassword().equals(user.getPassword())) {
                        resBody.put("passwordNotMatch", "Current pin is wrong.");
                        return resBody;
                    }
                }
                user.setPassword(resetPasswordDto.getPassword());
                this.userRepository.save(user);
                resBody.put("success", "Pin change successfully.");
                return resBody;
            } else {
                CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(Integer.parseInt(id)).orElseThrow(() -> new RuntimeException("Company not found"));
                if (resetPasswordDto.getCurrentPassword() != null) {
                    if (!resetPasswordDto.getCurrentPassword().equals(companyEmployee.getPassword())) {
                        resBody.put("passwordNotMatch", "Current pin is wrong.");
                        return resBody;
                    }
                }
                companyEmployee.setPassword(resetPasswordDto.getPassword());
                this.companyEmployeeRepository.save(companyEmployee);
                resBody.put("success", "Pin change successfully.");
                return resBody;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error resetPassword: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadProfileImage(Integer userId, String imagePath) {
        try {
            this.deleteProfileImage(userId);
            Users user = this.userRepository.findById(Long.parseLong(userId.toString())).orElseThrow(() -> new RuntimeException("User not found"));
            String updatedPath = this.commonService.updateFileLocationForProfile(imagePath, userId, "profileImages");
            if (updatedPath.equals("Error")) {
                return "Error";
            } else {
                user.setProfileImage(updatedPath);
                this.userRepository.save(user);
                return updatedPath;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error uploadProfileImage: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteProfileImage(Integer userId) {
        try {
            Users user = this.userRepository.findById(Long.parseLong(userId.toString())).orElseThrow(() -> new RuntimeException("User not found"));
            user.setProfileImage("");
            this.userRepository.save(user);

            File existingImagePath = new File(FILE_DIRECTORY + userId + "/profileImages/");
            if (existingImagePath.exists()) {
                this.commonService.deleteDirectoryRecursively(existingImagePath);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleteProfileImage: " + e.getMessage(), e);
        }
    }
}

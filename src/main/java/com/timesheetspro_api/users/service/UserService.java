package com.timesheetspro_api.users.service;

import com.timesheetspro_api.common.dto.LoginDto;
import com.timesheetspro_api.common.dto.user.ResetPasswordDto;
import com.timesheetspro_api.common.dto.user.UserDto;
import com.timesheetspro_api.common.model.users.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {

    Map<String, Object> userLogin(LoginDto loginDto);

    List<UserDto> getAllUsers();

    Users getUser(Long userId);

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);

    boolean generateResetLink(String email, String userName, String companyId);

    Map<String, Object> validateToken(String token);

    Map<String, Object> resetPassword(ResetPasswordDto resetPasswordDto);

    String uploadProfileImage(Integer userId, String imagePath);

    boolean deleteProfileImage(Integer userId);
}

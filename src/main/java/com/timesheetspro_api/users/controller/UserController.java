package com.timesheetspro_api.users.controller;

import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.dto.LoginDto;
import com.timesheetspro_api.common.dto.user.ResetPasswordDto;
import com.timesheetspro_api.common.dto.user.UserDto;
import com.timesheetspro_api.common.exception.GlobalException;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private UserService userService;

    @GetMapping("/getAllUsers")
    public ApiResponse<?> getAllUsers() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully", this.userService.getAllUsers());
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getUser(@PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "User fetched successfully", this.userService.getUserById(Long.parseLong(id)));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createUser(@Valid @RequestBody UserDto userDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.CREATED.value(), "User added successfully", this.userService.createUser(userDto));
        } catch (GlobalException e) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            UserDto res = this.userService.updateUser(Long.parseLong(id), userDto);
            return new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteUser(@PathVariable String id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.userService.deleteUser(Long.parseLong(id));
            return new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PostMapping("/login")
    public ApiResponse<?> userLogin(@Valid @RequestBody LoginDto loginDto) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            resBody = this.userService.userLogin(loginDto);
            if (resBody.containsKey("error")) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), resBody.get("error").toString(), resBody);
            }
            if (resBody.containsKey("passwordError")) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), resBody.get("passwordError").toString(), resBody);
            }

            if (resBody.isEmpty()) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid credentials", resBody);
            }

            return new ApiResponse<>(HttpStatus.OK.value(), "Login successful", resBody);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to login", resBody);
        }
    }

    @GetMapping("/generateResetLink")
    public ApiResponse<?> generateResetLink(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam String email,
            @RequestParam String userName,
            @RequestParam String companyId
    ) {

        Map<String, Object> resBody = new HashMap<>();
        try {
            if (this.userService.generateResetLink(email, userName, companyId)) {
                return new ApiResponse<>(HttpStatus.OK.value(), "A password reset link has been sent to " + email, "");
            } else {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "This email or username is not registered", "");
            }

        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to generate link", resBody);
        }
    }

    @GetMapping("/validateToken")
    public ApiResponse<?> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam String token) {

        Map<String, Object> resBody = new HashMap<>();
        try {
            Map<String, Object> validateToken = this.userService.validateToken(token);
            if (validateToken != null) {
                return new ApiResponse<>(HttpStatus.OK.value(), validateToken.get("message").toString(), validateToken);
            }
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid token", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch account details", resBody);
        }
    }

    @PostMapping("/resetPassword")
    public ApiResponse<?> resetPassword(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody ResetPasswordDto resetPasswordDto) {

        try {
            Map<String, Object> response = this.userService.resetPassword(resetPasswordDto);

            if (response.containsKey("success")) {
                return new ApiResponse<>(HttpStatus.OK.value(), "Pin reset successfully", response);
            }
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Failed to reset pin", response);

        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error resetting pin", Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/uploadProfileImage")
    public ApiResponse<?> uploadProfileImage(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody Map<String, Object> req) {

        Map<String, Object> resBody = new HashMap<>();
        try {
            Long userId = req.get("userId") != null ? Long.parseLong(req.get("userId").toString()) : jwtUtil.extractUserId(authorizationHeader.substring(7));
            String path = this.userService.uploadProfileImage(userId, req.get("profileImage").toString());
            if (path.equals("Error")) {
                return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Image does not exist in the directory", "");
            }
            return new ApiResponse<>(HttpStatus.OK.value(), "Profile image update successfully", path);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update profile image", resBody);
        }
    }

    @GetMapping("/deleteProfileImage")
    public ApiResponse<?> deleteProfileImage(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            Long userId = jwtUtil.extractUserId(authorizationHeader.substring(7));
            if (this.userService.deleteProfileImage(userId)) {
                return new ApiResponse<>(HttpStatus.OK.value(), "Profile image deleted successfully", "");
            }
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Profile image not found", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete profile image", resBody);
        }
    }
}

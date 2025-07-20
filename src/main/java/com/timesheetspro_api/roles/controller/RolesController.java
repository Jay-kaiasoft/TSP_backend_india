package com.timesheetspro_api.roles.controller;

import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.dto.roles.RoleDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.roles.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/roles")
public class RolesController {
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");
    private static final Logger debugLogger = LoggerFactory.getLogger("debugLogger");

    @Autowired
    private JwtTokenUtil jwtUtil;
    @Autowired
    private RoleService rolesService;

    @GetMapping("/getAllRoleList")
    public ApiResponse<?> getAllRolesList(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Roles list fetched successfully", this.rolesService.getAllRolesList());
        } catch (Exception e) {
            errorLogger.error("[ loginUserId : " + loginUserId + " ] getAllRoles Error: " + e);
            debugLogger.debug("[ loginUserId : " + loginUserId + " ] getAllRoles Error: " + e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch roles list", resBody);
        }
    }

    @PostMapping("/create")
    public ApiResponse<?> createRole(
            @RequestBody RoleDto rolesDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody.put("role", rolesService.createRole(rolesDto));
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Role added successfully", resBody);
        } catch (Exception e) {
            errorLogger.error("[ loginUserId : " + loginUserId + " ] createRoles Error: " + e);
            debugLogger.debug("[ loginUserId : " + loginUserId + " ] createRoles Error: " + e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to add new role", resBody);
        }
    }

    @GetMapping("/rolesListPage")
    public ApiResponse<?> rolesList(
            @RequestParam(value = "searchKey", required = false) String searchKey,
            Pageable pageable,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody = rolesService.rolesList(searchKey, pageable);
            return new ApiResponse<>(HttpStatus.OK.value(), "Roles list fetched successfully", resBody);
        } catch (Exception e) {
            errorLogger.error("[ loginUserId : " + loginUserId + " ] rolesList Error: " + e);
            debugLogger.debug("[ loginUserId : " + loginUserId + " ] rolesList Error: " + e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch roles list", resBody);
        }
    }

    @GetMapping("/getAllRoles")
    public ApiResponse<?> getAllRoles(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody = rolesService.getAllRoles();
            return new ApiResponse<>(HttpStatus.OK.value(), "Roles list fetched successfully", resBody);
        } catch (Exception e) {
            errorLogger.error("[ loginUserId : " + loginUserId + " ] getAllRoles Error: " + e);
            debugLogger.debug("[ loginUserId : " + loginUserId + " ] getAllRoles Error: " + e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch roles list", resBody);
        }
    }

    @GetMapping("/get/{roleId}")
    public ApiResponse<?> getRoleById(
            @PathVariable Long roleId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody.put("role", rolesService.getRoleById(roleId));
            return new ApiResponse<>(HttpStatus.OK.value(), "Role fetched successfully", resBody);
        } catch (Exception e) {
            errorLogger.error("[ loginUserId : " + loginUserId + " ] getRoleById Error: " + e);
            debugLogger.debug("[ loginUserId : " + loginUserId + " ] getRoleById Error: " + e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch role", resBody);
        }
    }

    @PutMapping("/update/{roleId}")
    public ApiResponse<?> updateRoleById(
            @PathVariable Long roleId,
            @RequestBody RoleDto rolesDto,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody.put("role", rolesService.updateById(roleId, rolesDto));
            return new ApiResponse<>(HttpStatus.OK.value(), "Role updated successfully", resBody);
        } catch (Exception e) {
            errorLogger.error("[ loginUserId : " + loginUserId + " ] updateRoleById Error: " + e);
            debugLogger.debug("[ loginUserId : " + loginUserId + " ] updateRoleById Error: " + e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update role", resBody);
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public ApiResponse<?> deleteRoleById(
            @PathVariable Long roleId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            rolesService.deleteRoleById(roleId);
            return new ApiResponse<>(HttpStatus.OK.value(), "Role deleted successfully", resBody);
        } catch (Exception e) {
            errorLogger.error("[ loginUserId : " + loginUserId + " ] deleteRoleById Error: " + e);
            debugLogger.debug("[ loginUserId : " + loginUserId + " ] deleteRoleById Error: " + e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete role", resBody);
        }
    }

    @GetMapping("/getActions/{roleId}")
    public ApiResponse<?> getActions(
            @PathVariable Long roleId,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            return new ApiResponse<>(HttpStatus.OK.value(), "Role's policies fetched successfully", rolesService.getPolicy(roleId));
        } catch (Exception e) {
            errorLogger.error("[ loginUserId : " + loginUserId + " ] getPolicies Error: " + e);
            debugLogger.debug("[ loginUserId : " + loginUserId + " ] getPolicies Error: " + e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch", resBody);
        }
    }
}

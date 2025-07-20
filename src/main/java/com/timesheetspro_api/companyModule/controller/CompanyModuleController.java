package com.timesheetspro_api.companyModule.controller;

import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.dto.CompanyModuleDto.CompanyModuleDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyModule.service.CompanyModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/companyModule")
public class CompanyModuleController {
    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private CompanyModuleService moduleService;

    @PostMapping("/create")
    public ApiResponse<?> createModule(
            @RequestBody CompanyModuleDto moduleDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody.put("module", moduleService.createModule(moduleDto));
            return new ApiResponse<>(HttpStatus.OK.value(), "Module added successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "failed to add new module", resBody);
        }
    }

    @GetMapping("/allModuleListPage")
    public ApiResponse<?> allModuleListPage(
            @RequestParam(value = "searchKey", required = false) String searchKey,
            Pageable pageable,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody = moduleService.allModuleListPage(searchKey, pageable);
            return new ApiResponse<>(HttpStatus.OK.value(), "Modules list fetched successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch modules list", resBody);
        }
    }

    @GetMapping("/moduleByFunctionalityListPage")
    public ApiResponse<?> moduleByFunctionalityListPage(
            @RequestParam(value = "functionalityId") int functionalityId,
            @RequestParam(value = "searchKey", required = false) String searchKey,
            Pageable pageable,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody = moduleService.moduleByFunctionalityListPage(functionalityId, searchKey, pageable);
            return new ApiResponse<>(HttpStatus.OK.value(), "Modules list fetched successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch modules list", resBody);
        }
    }

    @GetMapping("/getAllModules")
    public ApiResponse<?> getAllModules(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody = moduleService.getAllModules();
            return new ApiResponse<>(HttpStatus.OK.value(), "Modules list fetched successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch modules list", resBody);
        }
    }

    @GetMapping("/get/{moduleId}")
    public ApiResponse<?> getModuleById(
            @PathVariable int moduleId,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody.put("module", moduleService.getModuleById(moduleId));
            return new ApiResponse<>(HttpStatus.OK.value(), "Module fetched successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch functionality", resBody);
        }
    }

    @PutMapping("/update/{moduleId}")
    public ApiResponse<?> updateModuleById(
            @PathVariable int moduleId,
            @RequestBody CompanyModuleDto moduleDto,
            @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            resBody.put("module", moduleService.updateModuleById(moduleId, moduleDto));
            return new ApiResponse<>(HttpStatus.OK.value(), "Module updated successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to update Module", resBody);
        }
    }

    @DeleteMapping("/delete/{moduleId}")
    public ApiResponse<?> deleteModuleById(
            @PathVariable int moduleId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Long loginUserId = jwtUtil.extractUserId(authorizationHeader.substring(7));
        try {
            moduleService.deleteModuleById(moduleId);
            return new ApiResponse<>(HttpStatus.OK.value(), "Module deleted successfully", resBody);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to delete module", resBody);
        }
    }
}

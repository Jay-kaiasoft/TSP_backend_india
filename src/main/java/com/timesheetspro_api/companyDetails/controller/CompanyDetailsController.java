package com.timesheetspro_api.companyDetails.controller;

import com.timesheetspro_api.common.dto.companyDetails.CompanyDetailsDto;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.companyDetails.service.CompanyDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/companyDetails")
public class CompanyDetailsController {

    @Autowired
    private CompanyDetailsService companyDetailsService;

    @GetMapping("/search")
    public ApiResponse<?> search(@RequestParam("name") String name, @RequestParam("active") int active) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<Map<String, Object>> companyDetailsDtoList = this.companyDetailsService.searchCompanies(name, active);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch company details successfully", companyDetailsDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch company details", resBody);
        }
    }

    @GetMapping("/get/all")
    public ApiResponse<?> getAllCompanyDetails(@RequestParam("active") int active) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            List<Map<String, Object>> companyDetailsDtoList = this.companyDetailsService.getAllCompanyDetails(active);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch company details successfully", companyDetailsDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch company details", resBody);
        }
    }

    @GetMapping("/get/{id}")
    public ApiResponse<?> getCompanyDetails(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyDetailsDto companyDetailsDtoList = this.companyDetailsService.getCompanyDetails(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Fetch company details successfully", companyDetailsDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch company details", resBody);
        }
    }

    @PostMapping("/create/{step}")
    public ApiResponse<?> createCompanyDetails(@Valid @RequestBody CompanyDetailsDto companyDetailsDto, @PathVariable String step) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            return new ApiResponse<>(HttpStatus.CREATED.value(), "Company details added successfully", this.companyDetailsService.createCompanyDetails(companyDetailsDto, step));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @PutMapping("/update/{id}/{step}")
    public ApiResponse<?> updateCompanyDetails(@PathVariable Integer id, @Valid @RequestBody CompanyDetailsDto companyDetailsDto, @PathVariable String step) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            CompanyDetailsDto companyDetailsDtoList = this.companyDetailsService.updateCompanyDetails(id, companyDetailsDto, step);
            return new ApiResponse<>(HttpStatus.OK.value(), "Company details updated successfully", companyDetailsDtoList);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteCompanyDetails(@PathVariable Integer id) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            this.companyDetailsService.deleteCompanyDetails(id);
            return new ApiResponse<>(HttpStatus.OK.value(), "Company deactivate successfully", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch delet company details", resBody);
        }
    }

    @PostMapping("/uploadCompanyLogo")
    public ApiResponse<?> uploadCompanyLogo(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @RequestBody Map<String, Object> req) {

        Map<String, Object> resBody = new HashMap<>();
        try {
            String path = this.companyDetailsService.uploadCompanyLogo(Integer.parseInt(req.get("companyId").toString()), req.get("companyLogo").toString());
            if (path.equals("Error")) {
                return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Image does not exist in the directory", "");
            }
            return new ApiResponse<>(HttpStatus.OK.value(), "Logo update successfully", path);
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to update profile image", resBody);
        }
    }

    @DeleteMapping("/deleteCompanyLogo/{companyId}")
    public ApiResponse<?> deleteCompanyLogo(@RequestHeader(value = "Authorization", required = false) String authorizationHeader, @PathVariable Integer companyId) {
        Map<String, Object> resBody = new HashMap<>();
        try {
            if (this.companyDetailsService.deleteCompanyLogo(companyId)) {
                return new ApiResponse<>(HttpStatus.OK.value(), "Logo deleted successfully", "");
            }
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Logo not found", "");
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to delete profile image", resBody);
        }
    }

    @GetMapping("/getLastCompany")
    public ApiResponse<?> getLastCompany() {
        Map<String, Object> resBody = new HashMap<>();
        try {
            String company = this.companyDetailsService.getLastCompany();
            if (company != null) {
                return new ApiResponse<>(HttpStatus.OK.value(), "Fetch company details successfully", company);
            } else {
                return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "No company details found", "");
            }
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Fail to fetch company details", resBody);
        }
    }
}

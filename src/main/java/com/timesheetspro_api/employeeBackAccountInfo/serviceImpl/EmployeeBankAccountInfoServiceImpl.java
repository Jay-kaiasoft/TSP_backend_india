package com.timesheetspro_api.employeeBackAccountInfo.serviceImpl;

import com.timesheetspro_api.common.dto.employeeBackAccountInfo.EmployeeBackAccountInfoDTO;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.employeeBackAccountInfo.EmployeeBackAccountInfo;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.EmployeeBackAccountInfoRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.employeeBackAccountInfo.service.EmployeeBankAccountInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service("employeeBankAccountInfoService")
public class EmployeeBankAccountInfoServiceImpl implements EmployeeBankAccountInfoService {

    @Value("${timeSheetProDrive}")
    String FILE_DIRECTORY;

    @Autowired
    private EmployeeBackAccountInfoRepository bankAccountInfoRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    private CommonService commonService;

    @Override
    public EmployeeBackAccountInfoDTO createBankAccountInfo(EmployeeBackAccountInfoDTO dto) {
        try {
            EmployeeBackAccountInfo entity = new EmployeeBackAccountInfo();
            CompanyEmployee emp = this.companyEmployeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            entity.setCompanyEmployee(emp);
            BeanUtils.copyProperties(dto, entity);
            this.bankAccountInfoRepository.save(entity);
            dto.setId(entity.getId());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmployeeBackAccountInfoDTO getBankAccountInfoById(int id) {
        try {
            EmployeeBackAccountInfo entity = this.bankAccountInfoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bank account info not found"));
            EmployeeBackAccountInfoDTO dto = new EmployeeBackAccountInfoDTO();
            BeanUtils.copyProperties(entity, dto);
            dto.setEmployeeId(entity.getCompanyEmployee().getEmployeeId());
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<EmployeeBackAccountInfoDTO> getAllBankAccountInfo() {
        try {
            List<EmployeeBackAccountInfoDTO> dtoList = new ArrayList<>();
            List<EmployeeBackAccountInfo> entityList = this.bankAccountInfoRepository.findAll();
            for (EmployeeBackAccountInfo entity : entityList) {
                dtoList.add(this.getBankAccountInfoById(entity.getId()));
            }
            return dtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmployeeBackAccountInfoDTO updateBankAccountInfo(int id, EmployeeBackAccountInfoDTO dto) {
        try {
            EmployeeBackAccountInfo existing = this.bankAccountInfoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bank account info not found"));
            CompanyEmployee emp = this.companyEmployeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            existing.setCompanyEmployee(emp);
            BeanUtils.copyProperties(dto, existing, "id");
            this.bankAccountInfoRepository.save(existing);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteBankAccountInfo(int id) {
        try {
            this.bankAccountInfoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String uploadPassbookImage(Integer companyId, Integer id, String imagePath) {
        try {
            this.deletePassbookImage(companyId, id);
            EmployeeBackAccountInfo existing = this.bankAccountInfoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bank account info not found"));
            String updatedPath = this.commonService.updateFileLocationForProfile(imagePath, Integer.parseInt(companyId.toString()), "employeeProfile/bank/" + id);
            if (updatedPath.equals("Error")) {
                return "Error";
            } else {
                existing.setPassbookImage(updatedPath);
                this.bankAccountInfoRepository.save(existing);
                return updatedPath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error uploadEmployee: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deletePassbookImage(Integer companyId, Integer id) {
        try {
            EmployeeBackAccountInfo existing = this.bankAccountInfoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bank account info not found"));
            File existingImagePath = new File(FILE_DIRECTORY + companyId + "/employeeProfile/bank/" + id);
            if (existingImagePath.exists()) {
                this.commonService.deleteDirectoryRecursively(existingImagePath);
                existing.setPassbookImage("");
                this.bankAccountInfoRepository.save(existing);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error deleteCompanyLogo: " + e.getMessage(), e);
        }
    }
}

package com.timesheetspro_api.employmentInfo.serviceImpl;

import com.timesheetspro_api.common.dto.employmentInfo.EmploymentInfoDTO;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.employmentInfo.EmploymentInfo;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.common.repository.company.EmploymentInfoRepository;
import com.timesheetspro_api.common.service.CommonService;
import com.timesheetspro_api.employmentInfo.service.EmploymentInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "employmentInfoService")
public class EmploymentInfoServiceImpl implements EmploymentInfoService {

    @Autowired
    private EmploymentInfoRepository employmentInfoRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Autowired
    private CommonService commonService;

    @Override
    public EmploymentInfoDTO createEmploymentInfo(EmploymentInfoDTO dto) {
        try {
            EmploymentInfo entity = new EmploymentInfo();
            CompanyEmployee emp = this.companyEmployeeRepository.findById(dto.getEmployeeId()).orElseThrow(() -> new RuntimeException("Employee not found"));
            entity.setCompanyEmployee(emp);
            entity.setHireDate(this.commonService.convertStringToDate(dto.getHireDate()));
            BeanUtils.copyProperties(dto, entity);
            this.employmentInfoRepository.save(entity);
            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmploymentInfoDTO getEmploymentInfoById(int id) {
        try {
            EmploymentInfo entity = this.employmentInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("EmploymentInfo not found"));
            EmploymentInfoDTO employmentInfoDTO = new EmploymentInfoDTO();
            employmentInfoDTO.setEmployeeId(entity.getCompanyEmployee().getEmployeeId());
            employmentInfoDTO.setHireDate(this.commonService.convertDateToString(entity.getHireDate()));
            BeanUtils.copyProperties(entity,employmentInfoDTO);
            return employmentInfoDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<EmploymentInfoDTO> getAllEmploymentInfo() {
        try {
            List<EmploymentInfoDTO> employmentInfoDTOList = new ArrayList<>();
            List<EmploymentInfo> employmentInfoList = this.employmentInfoRepository.findAll();
            if (!employmentInfoList.isEmpty()) {
                for (EmploymentInfo employmentInfo : employmentInfoList) {
                    employmentInfoDTOList.add(this.getEmploymentInfoById(employmentInfo.getId()));
                }
            }
            return employmentInfoDTOList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EmploymentInfoDTO updateEmploymentInfo(int id, EmploymentInfoDTO dto) {
        try {
            EmploymentInfo existing = this.employmentInfoRepository.findById(id).orElseThrow(() -> new RuntimeException("EmploymentInfo not found"));

            CompanyEmployee emp = this.companyEmployeeRepository.findById(dto.getEmployeeId()).orElseThrow(() -> new RuntimeException("Employee not found"));
            existing.setCompanyEmployee(emp);
            existing.setHireDate(this.commonService.convertStringToDate(dto.getHireDate()));
            BeanUtils.copyProperties(dto, existing, "id");
            this.employmentInfoRepository.save(existing);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteEmploymentInfo(int id) {
        try {
            this.employmentInfoRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

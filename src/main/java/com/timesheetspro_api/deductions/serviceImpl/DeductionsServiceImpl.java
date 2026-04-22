package com.timesheetspro_api.deductions.serviceImpl;

import com.timesheetspro_api.common.dto.deductions.DeductionsDto;
import com.timesheetspro_api.common.model.CompanyEmployee.CompanyEmployee;
import com.timesheetspro_api.common.model.deductions.Deductions;
import com.timesheetspro_api.common.repository.DeductionsRepository;
import com.timesheetspro_api.common.repository.company.CompanyEmployeeRepository;
import com.timesheetspro_api.deductions.service.DeductionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("DeductionsService")
public class DeductionsServiceImpl implements DeductionsService {

    @Autowired
    private DeductionsRepository deductionsRepository;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @Override
    public List<DeductionsDto> findByEmployeeId(Integer id) {
        try {
            List<Deductions> deductions = deductionsRepository.findByEmployeeId(id);
            List<DeductionsDto> deductionsDtoList = new ArrayList<>();
            if (deductions != null) {
                for (Deductions deduction : deductions) {
                    deductionsDtoList.add(this.findById(deduction.getId()));
                }
            }
            return deductionsDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DeductionsDto findById(Integer id) {
        try {
            Deductions deductions = this.deductionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Deductions not found!"));
            DeductionsDto deductionsDto = new DeductionsDto();
            deductionsDto.setId(deductions.getId());
            deductionsDto.setType(deductions.getType());
            deductionsDto.setLabel(deductions.getLabel());
            deductionsDto.setAmount(deductions.getAmount());
            deductionsDto.setEmployeeId(deductions.getCompanyEmployee().getEmployeeId());
            return deductionsDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveDeductions(List<DeductionsDto> deductionsDtoList) {
        try {
            if (deductionsDtoList != null && !deductionsDtoList.isEmpty()) {
                for (DeductionsDto deductionsDto : deductionsDtoList) {
                    Integer id = deductionsDto.getId();
                    Deductions deductions = new Deductions();
                    if (id != null) {
                        deductions = this.deductionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Deductions not found!"));
                    }
                    CompanyEmployee companyEmployee = this.companyEmployeeRepository.findById(deductionsDto.getEmployeeId()).orElseThrow(() -> new RuntimeException("Employee not found!"));
                    deductions.setCompanyEmployee(companyEmployee);
                    deductions.setType(deductionsDto.getType());
                    deductions.setLabel(deductionsDto.getLabel());
                    deductions.setAmount(deductionsDto.getAmount());
                    this.deductionsRepository.save(deductions);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        try {
            Deductions deductions = this.deductionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Deductions not found!"));
            this.deductionsRepository.delete(deductions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

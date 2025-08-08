package com.timesheetspro_api.salaryStatementMaster.serviceImpl;

import com.timesheetspro_api.common.dto.SalaryStatementMaster.SalaryStatementMasterDto;
import com.timesheetspro_api.common.model.companyDetails.CompanyDetails;
import com.timesheetspro_api.common.model.salaryStatementMaster.SalaryStatementMaster;
import com.timesheetspro_api.common.repository.company.CompanyDetailsRepository;
import com.timesheetspro_api.common.repository.company.SalaryStatementMasterRepository;
import com.timesheetspro_api.salaryStatementMaster.service.SalaryStatementMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "salaryStatementMasterService")
public class SalaryStatementMasterServiceImpl implements SalaryStatementMasterService {

    @Autowired
    private SalaryStatementMasterRepository salaryStatementMasterRepository;

    @Autowired
    private CompanyDetailsRepository companyDetailsRepository;

    @Override
    public List<SalaryStatementMasterDto> getAllSalaryStatementMasters(int companyId) {
        try {
            List<SalaryStatementMaster> salaryStatementMastersList = this.salaryStatementMasterRepository.findByCompanyId(companyId);
            List<SalaryStatementMasterDto> salaryStatementMasterDtoList = new ArrayList<>();
            if (!salaryStatementMastersList.isEmpty()) {
                for (SalaryStatementMaster salaryStatementMaster : salaryStatementMastersList) {
                    salaryStatementMasterDtoList.add(this.getSalaryStatementMasterById(salaryStatementMaster.getId()));
                }
            }
            return salaryStatementMasterDtoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalaryStatementMasterDto getSalaryStatementMastersByMonthAndYear(int companyId, int month, int year) {
        try {
            SalaryStatementMaster salaryStatementMastersList = this.salaryStatementMasterRepository.findByMonthAndYear(companyId, month, year);
            if (salaryStatementMastersList != null) {
                return this.getSalaryStatementMasterById(salaryStatementMastersList.getId());
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalaryStatementMasterDto getSalaryStatementMasterById(int id) {
        try {
            SalaryStatementMaster salaryStatementMaster = this.salaryStatementMasterRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Statement Master not found"));
            SalaryStatementMasterDto salaryStatementMasterDto = new SalaryStatementMasterDto();
            salaryStatementMasterDto.setId(salaryStatementMaster.getId());
            salaryStatementMasterDto.setCompanyId(salaryStatementMaster.getCompanyDetails().getId());
            salaryStatementMasterDto.setMonth(salaryStatementMaster.getMonth());
            salaryStatementMasterDto.setYear(salaryStatementMaster.getYear());
            salaryStatementMasterDto.setTotalSalary(salaryStatementMaster.getTotalSalary());
            salaryStatementMasterDto.setTotalPf(salaryStatementMaster.getTotalPf());
            salaryStatementMasterDto.setTotalPt(salaryStatementMaster.getTotalPt());
            salaryStatementMasterDto.setNote(salaryStatementMaster.getNote());
            return salaryStatementMasterDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalaryStatementMasterDto createSalaryStatementMaster(SalaryStatementMasterDto salaryStatementMasterDto) {
        try {
            SalaryStatementMaster salaryStatementMaster = new SalaryStatementMaster();
            CompanyDetails companyDetails = companyDetailsRepository.findById(salaryStatementMasterDto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            salaryStatementMaster.setCompanyDetails(companyDetails);
            salaryStatementMaster.setMonth(salaryStatementMasterDto.getMonth());
            salaryStatementMaster.setYear(salaryStatementMasterDto.getYear());
            salaryStatementMaster.setTotalSalary(salaryStatementMasterDto.getTotalSalary());
            salaryStatementMaster.setTotalPf(salaryStatementMasterDto.getTotalPf());
            salaryStatementMaster.setTotalPt(salaryStatementMasterDto.getTotalPt());
            salaryStatementMaster.setNote(salaryStatementMasterDto.getNote());
            this.salaryStatementMasterRepository.save(salaryStatementMaster);
            return salaryStatementMasterDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SalaryStatementMasterDto updateSalaryStatementMaster(int id, SalaryStatementMasterDto salaryStatementMasterDto) {
        try {
            SalaryStatementMaster salaryStatementMaster = this.salaryStatementMasterRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Statement Master not found"));
            CompanyDetails companyDetails = companyDetailsRepository.findById(salaryStatementMasterDto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            salaryStatementMaster.setCompanyDetails(companyDetails);
            salaryStatementMaster.setMonth(salaryStatementMasterDto.getMonth());
            salaryStatementMaster.setYear(salaryStatementMasterDto.getYear());
            salaryStatementMaster.setTotalSalary(salaryStatementMasterDto.getTotalSalary());
            salaryStatementMaster.setTotalPf(salaryStatementMasterDto.getTotalPf());
            salaryStatementMaster.setTotalPt(salaryStatementMasterDto.getTotalPt());
            salaryStatementMaster.setNote(salaryStatementMasterDto.getNote());
            this.salaryStatementMasterRepository.save(salaryStatementMaster);
            return salaryStatementMasterDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSalaryStatementMaster(int id) {
        try {
            SalaryStatementMaster salaryStatementMaster = this.salaryStatementMasterRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary Statement Master not found"));
            this.salaryStatementMasterRepository.delete(salaryStatementMaster);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

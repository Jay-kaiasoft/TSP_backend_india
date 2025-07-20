package com.timesheetspro_api.companyRoleActions.serviceImpl;

import com.timesheetspro_api.common.dto.companyActionsDto.CompanyActionsDto;
import com.timesheetspro_api.common.model.companyActions.CompanyActions;
import com.timesheetspro_api.common.repository.company.CompanyActionsRepository;
import com.timesheetspro_api.companyRoleActions.service.CompanyRoleActionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "companyRoleActionService")
public class CompanyRoleActionServiceImpl implements CompanyRoleActionService {

    @Autowired
    private CompanyActionsRepository companyActionsRepository;

    @Override
    public List<CompanyActionsDto> getCompanyActions() {
        try {
            List<CompanyActions> companyActions = this.companyActionsRepository.findAll();
            List<CompanyActionsDto> companyActionsDtos = new ArrayList<>();
            if (!companyActions.isEmpty()){
                for (CompanyActions companyActions1:companyActions){
                    companyActionsDtos.add(this.getActions(companyActions1.getActionId()));
                }
            }
            return companyActionsDtos;
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyActionsDto getActions(int id) {
        try {
            CompanyActions companyActions = this.companyActionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Action not found"));
            CompanyActionsDto companyActionsDto = new CompanyActionsDto();
            BeanUtils.copyProperties(companyActions,companyActionsDto);
            return  companyActionsDto;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyActionsDto createActions(CompanyActionsDto companyActionsDto) {
        try {
            CompanyActions companyActions = new CompanyActions();
            BeanUtils.copyProperties(companyActionsDto, companyActions);
            this.companyActionsRepository.save(companyActions);
            return companyActionsDto;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CompanyActionsDto updateActions(int id, CompanyActionsDto companyActionsDto) {
        try {
            CompanyActions companyActions = this.companyActionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Action not found"));
            BeanUtils.copyProperties(companyActionsDto, companyActions);
            this.companyActionsRepository.save(companyActions);
            return companyActionsDto;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteActions(int id) {
        try {
            CompanyActions companyActions = this.companyActionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Action not found"));
            this.companyActionsRepository.delete(companyActions);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

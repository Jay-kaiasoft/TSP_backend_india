package com.timesheetspro_api.companyAction.serviceImpl;

import com.timesheetspro_api.common.dto.companyActionsDto.CompanyActionsDto;
import com.timesheetspro_api.common.model.companyActions.CompanyActions;
import com.timesheetspro_api.common.repository.company.CompanyActionsRepository;
import com.timesheetspro_api.companyAction.service.CompanyActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "companyActionService")
public class CompanyActionServiceImpl implements CompanyActionService {

    @Autowired
    private CompanyActionsRepository actionsRepository;

    @Override
    public List<CompanyActionsDto> getAllActions() {
        try {
            List<CompanyActions> actions = this.actionsRepository.findAll();
            List<CompanyActionsDto> actionDtos = new ArrayList<>();
            if (!actions.isEmpty()) {
                for (CompanyActions actions1 : actions) {
                    CompanyActionsDto actionDto = new CompanyActionsDto();
                    actionDto.setActionId(actions1.getActionId());
                    actionDto.setActionName(actions1.getActionName());
                    actionDtos.add(actionDto);
                }
            }
            return actionDtos;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}

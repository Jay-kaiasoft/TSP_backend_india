package com.timesheetspro_api.actions.serviceImpl;

import com.timesheetspro_api.actions.service.ActionService;
import com.timesheetspro_api.common.dto.action.ActionDto;
import com.timesheetspro_api.common.model.actions.Actions;
import com.timesheetspro_api.common.repository.ActionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "actionService")
public class ActionServiceImpl implements ActionService {

    @Autowired
    private ActionsRepository actionsRepository;

    @Override
    public List<ActionDto> getAllActions() {
        try {
            List<Actions> actions = this.actionsRepository.findAll();
            List<ActionDto> actionDtos = new ArrayList<>();
            if (!actions.isEmpty()) {
                for (Actions actions1 : actions) {
                    ActionDto actionDto = new ActionDto();
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

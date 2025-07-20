package com.timesheetspro_api.actions.service;

import com.timesheetspro_api.common.dto.action.ActionDto;

import java.util.List;

public interface ActionService {
    List<ActionDto> getAllActions();
}

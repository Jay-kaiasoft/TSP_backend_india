package com.timesheetspro_api.functionality.service;


import com.timesheetspro_api.common.dto.functionality.FunctionalityDto;
import com.timesheetspro_api.common.model.functionality.Functionality;

import java.util.List;

public interface FunctionalityService {

    List<Functionality> getAllFunctionality();

    Functionality getFunctionality(Long functionalityId);

    FunctionalityDto createFunctionality(FunctionalityDto functionalityDto);

    FunctionalityDto updateFunctionality(Long functionalityId, FunctionalityDto functionalityDto);

    void deleteFunctionality(Long functionalityId);
}

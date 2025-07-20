package com.timesheetspro_api.userShift.service;

import com.timesheetspro_api.common.dto.userShift.UserShiftDto;

import java.util.List;

public interface UserShiftService {
    List<UserShiftDto> getAllUserShift();

    UserShiftDto getUserShift(Long id);

    UserShiftDto createUserShift(UserShiftDto userShiftDto);

    UserShiftDto updateUserShift(Long id, UserShiftDto userShiftDto);

    void deleteUserShift(Long id);
}

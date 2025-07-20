package com.timesheetspro_api.userShift.serviceImpl;

import com.timesheetspro_api.common.dto.userShift.UserShiftDto;
import com.timesheetspro_api.common.model.userShift.UserShift;
import com.timesheetspro_api.common.repository.UserShiftRepository;
import com.timesheetspro_api.userShift.service.UserShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value = "userShiftService")
public class UserShiftServiceImpl implements UserShiftService {
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Autowired
    private UserShiftRepository userShiftRepository;

    @Override
    public List<UserShiftDto> getAllUserShift() {
        try {
            List<UserShift> userShifts = this.userShiftRepository.findAll();
            List<UserShiftDto> userShiftDtoList = new ArrayList<>();

            if (!userShifts.isEmpty()) {
                for (UserShift userShift : userShifts) {
                    userShiftDtoList.add(this.getUserShift(userShift.getId()));
                }
            }
            return userShiftDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public UserShiftDto getUserShift(Long id) {
        try {
            UserShift userShift = this.userShiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
            UserShiftDto userShiftDto = new UserShiftDto();
            BeanUtils.copyProperties(userShift, userShiftDto);
            return userShiftDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public UserShiftDto createUserShift(UserShiftDto userShiftDto) {
        try {
            UserShift userShift = new UserShift();
            BeanUtils.copyProperties(userShiftDto, userShift);
            this.userShiftRepository.save(userShift);
            return userShiftDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public UserShiftDto updateUserShift(Long id, UserShiftDto userShiftDto) {
        try {
            UserShift userShift = this.userShiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
            BeanUtils.copyProperties(userShiftDto, userShift);
            this.userShiftRepository.save(userShift);
            return userShiftDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    @Override
    public void deleteUserShift(Long id) {
        try {
            UserShift userShift = this.userShiftRepository.findById(id).orElseThrow(() -> new RuntimeException("Shift not found"));
            this.userShiftRepository.delete(userShift);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}

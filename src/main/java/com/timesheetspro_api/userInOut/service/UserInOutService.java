package com.timesheetspro_api.userInOut.service;

import com.timesheetspro_api.common.dto.UserInOut.UserInOutDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserInOutService {

    Map<String, Object> dashboardCounts(int companyId);

    List<UserInOutDto> getAllEntriesByUserId(List<Integer> userIds, String startDate, String endDate, String timeZone, List<Integer> locationIds, List<Integer> departmentIds,Integer companyId);

    UserInOutDto getUserLastInOut(int id);

    UserInOutDto getUserInOut(Long id);

    UserInOutDto createUserInOut(int userId, Integer locationId, Integer parsedCompanyId);

    void updateUserInOut(Long id, int userId);

    UserInOutDto updateUserInOut(UserInOutDto dto);

    List<UserInOutDto> getTodayEntriesByUserId(int userId);

    Map<String, Object> getTimeInOutReport(List<Integer> userIds, String startDate, String endDate, String timeZone,Integer companyId);

    Workbook generateExcelReport(Map<String, Object> data, String startDate, String endDate, String timeZone);

    String clickInOut(int userId, Integer locationId,Integer companyId);

}

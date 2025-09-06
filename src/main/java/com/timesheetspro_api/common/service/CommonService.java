package com.timesheetspro_api.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.Map;

public interface CommonService {
    Date convertStringToDate(String dateStr);

    String convertUtcToLocal(String utcTime, String timeZone);

    Date convertLocalToUtc(String localDate, String timeZone, boolean hasTime);

    String convertDateToString(Date date);

    Map<String, Object> uploadFiles(MultipartFile[] files, Integer LoginUserId, String folderName);

    String updateFileLocationForProfile(String image, Integer loginUserId, String folderName);

    void deleteDirectoryRecursively(File directory);

    boolean sendEmail(String toEmail, String subject, String body);
}

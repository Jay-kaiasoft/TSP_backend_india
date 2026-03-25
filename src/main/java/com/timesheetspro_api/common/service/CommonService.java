package com.timesheetspro_api.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.Map;

public interface CommonService {
    Map<String, Object> startUpload(String folderName, Integer userId, String fileName);

    Map<String, Object> uploadChunk(
            String folderName,
            Integer userId,
            String uploadId,
            int chunkIndex,
            int totalChunks,
            String originalFileName,
            MultipartFile chunk
    );

    Map<String, Object> completeUpload(
            String folderName,
            Integer userId,
            String uploadId,
            int totalChunks,
            String originalFileName
    );

    Map<String, Object> uploadFiles(MultipartFile[] files, Integer LoginUserId, String folderName);

    String updateFileLocationForProfile(String image, Integer loginUserId, String folderName);

    void deleteDirectoryRecursively(File directory);

    Date convertStringToDate(String dateStr);

    String convertUtcToLocal(String utcTime, String timeZone);

    Date convertLocalToUtc(String localDate, String timeZone, boolean hasTime);

    String convertDateToString(Date date);
    String convertDateToString(Date date, String timeZone);

    boolean sendEmail(String toEmail, String subject, String body);
}

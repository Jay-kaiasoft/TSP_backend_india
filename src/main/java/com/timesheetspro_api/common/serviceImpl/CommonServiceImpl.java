package com.timesheetspro_api.common.serviceImpl;

import com.timesheetspro_api.common.service.CommonService;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import net.coobird.thumbnailator.Thumbnails;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import java.nio.file.Files;
import java.util.*;

import org.springframework.mail.javamail.JavaMailSender;

import javax.imageio.ImageIO;

@Service(value = "commonService")
public class CommonServiceImpl implements CommonService {
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Value("${timeSheetProDrive}")
    String FILE_DIRECTORY;

    @Value("${imageContextPath}")
    String imageContextPath;

    @Value("${spring.mail.properties.mail.from}")
    String mailFrom;

    @Value("${spring.mail.properties.mail.fromName}")
    String mailFromName;

    private static final int MAX_DIM = 1920;

    @Autowired
    private JavaMailSender sender;

    @Override
    public Map<String, Object> startUpload(String folderName, Integer userId, String fileName) {

        int lastDot = fileName.lastIndexOf(".");
        if (lastDot == -1) {
            throw new RuntimeException("File has no extension");
        }

        String extension = fileName.substring(lastDot + 1).toLowerCase();
        if (!extension.matches("jpg|jpeg|png")) {
            throw new RuntimeException("Only JPG, JPEG, PNG images are allowed");
        }

        String safeFileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-]+", "_");

        String uploadId = UUID.randomUUID().toString();

        String chunkDirPath =
                FILE_DIRECTORY + userId + "/tempImage/" + folderName + "/chunks/" + uploadId + "/";

        File dir = new File(chunkDirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory");
        }

        Map<String, Object> res = new HashMap<>();
        res.put("uploadId", uploadId);
        res.put("fileName", safeFileName);

        return res;
    }


    @Override
    public Map<String, Object> uploadChunk(
            String folderName,
            Integer userId,
            String uploadId,
            int chunkIndex,
            int totalChunks,
            String originalFileName,
            MultipartFile chunk
    ) {
        String safeName = sanitizeFileName(originalFileName);
        validateExtension(safeName);

        String chunkDirPath = FILE_DIRECTORY + userId + "/tempImage/" + folderName + "/chunks/" + uploadId + "/";
        File chunkDir = new File(chunkDirPath);
        if (!chunkDir.exists()) chunkDir.mkdirs();

        // store chunk
        File chunkFile = new File(chunkDirPath + chunkIndex + ".part");
        try {
            chunk.transferTo(chunkFile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save chunk " + chunkIndex + ": " + e.getMessage(), e);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("chunkIndex", chunkIndex);
        res.put("totalChunks", totalChunks);
        return res;
    }

    @Override
    public Map<String, Object> completeUpload(
            String folderName,
            Integer userId,
            String uploadId,
            int totalChunks,
            String originalFileName
    ) {
        String safeName = sanitizeFileName(originalFileName);
        validateExtension(safeName);

        String baseDir = FILE_DIRECTORY + userId + "/tempImage/" + folderName + "/";
        File targetDirectory = new File(baseDir);
        if (!targetDirectory.exists()) targetDirectory.mkdirs();

        String chunkDirPath = baseDir + "chunks/" + uploadId + "/";
        File chunkDir = new File(chunkDirPath);
        if (!chunkDir.exists()) {
            throw new RuntimeException("Chunk directory not found");
        }

        File finalFile = new File(baseDir + safeName);

        mergeChunks(chunkDirPath, finalFile, totalChunks);

        File optimized = this.optimizeImage(finalFile);

        if (!optimized.equals(finalFile)) {
            finalFile.delete();
            optimized.renameTo(finalFile);
        }

        setFilePermissions(finalFile);

        // cleanup
        cleanupChunkDir(chunkDir);

        String fileUrl = imageContextPath + (userId + "/tempImage/" + folderName + "/" + safeName).replace("\\", "/");

        Map<String, String> fileInfo = new HashMap<>();
        fileInfo.put("imageName", safeName);
        fileInfo.put("imageURL", fileUrl);

        Map<String, Object> resBody = new HashMap<>();
        resBody.put("uploadedFiles", List.of(fileInfo));
        return resBody;
    }

//    @Override
//    public Date convertStringToDate(String dateStr) {
//        try {
//            SimpleDateFormat formatter;
//
//            // Determine format dynamically (DD/MM/YYYY)
//            if (dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
//                // dd/MM/yyyy
//                formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//
//            } else if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
//                // yyyy-MM-dd (ISO format)
//                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//
//            } else {
//                // dd/MM/yyyy, hh:mm:ss a
//                formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss a", Locale.ENGLISH);
//            }
//
//            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//            formatter.setLenient(false);
//            return formatter.parse(dateStr);
//
//        } catch (ParseException e) {
//            throw new RuntimeException(
//                    "Error converting date: " + dateStr + " - " + e.getMessage()
//            );
//        }
//    }

    @Override
    public Date convertStringToDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) return null;

            // ✅ ISO 8601: "2026-01-31T08:34:45.622Z" or "2026-01-30T18:30:00.000Z"
            // Also covers strings that include 'T' (common for ISO datetime)
            if (dateStr.contains("T")) {
                return Date.from(Instant.parse(dateStr));
            }

            SimpleDateFormat formatter;

            // dd/MM/yyyy
            if (dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

                // yyyy-MM-dd
            } else if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

                // dd/MM/yyyy, hh:mm:ss a
            } else {
                formatter = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss a", Locale.ENGLISH);
            }

            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            formatter.setLenient(false);
            return formatter.parse(dateStr);

        } catch (Exception e) {
            throw new RuntimeException("Error converting date: " + dateStr + " - " + e.getMessage(), e);
        }
    }


    @Override
    public String convertUtcToLocal(String utcTime, String timeZone) {
        try {
            // Input format: dd/MM/yyyy, hh:mm:ss a
            DateTimeFormatter inputFormatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a", Locale.ENGLISH);

            LocalDateTime localDateTime = LocalDateTime.parse(utcTime, inputFormatter);

            ZonedDateTime utcZonedDateTime =
                    localDateTime.atZone(ZoneId.of("UTC"));

            ZonedDateTime localZonedDateTime =
                    utcZonedDateTime.withZoneSameInstant(ZoneId.of(timeZone));

            DateTimeFormatter outputFormatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a", Locale.ENGLISH);

            return localZonedDateTime.format(outputFormatter);

        } catch (Exception e) {
            System.err.println("Error parsing time: " + e.getMessage());
            return null;
        }
    }

//    @Override
//    public Date convertLocalToUtc(String localDateTime, String timeZone, boolean hasTime) {
//        try {
//            SimpleDateFormat inputFormat;
//
//            if (hasTime && localDateTime.contains(":")) {
//                // dd/MM/yyyy, HH:mm:ss (24-hour for safety)
//                inputFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", Locale.ENGLISH);
//            } else {
//                // dd/MM/yyyy
//                inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//            }
//
//            inputFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
//            return inputFormat.parse(localDateTime);
//
//        } catch (ParseException e) {
//            throw new RuntimeException(
//                    "Error converting local date time to UTC: " +
//                            localDateTime +
//                            " | hasTime=" + hasTime +
//                            " | Error: " + e.getMessage()
//            );
//        }
//    }

    @Override
    public Date convertLocalToUtc(String localDateTime, String timeZone, boolean hasTime) {
        try {
            DateTimeFormatter formatter;

            if (hasTime && localDateTime.contains(":")) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss");
            } else {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            }

            ZoneId zoneId = ZoneId.of(timeZone);

            if (hasTime) {
                LocalDateTime ldt = LocalDateTime.parse(localDateTime, formatter);
                ZonedDateTime zdt = ldt.atZone(zoneId);
                return Date.from(zdt.toInstant());
            } else {
                LocalDate ld = LocalDate.parse(localDateTime, formatter);

                ZonedDateTime zdt = ld
                        .atStartOfDay(ZoneId.of(timeZone))
                        .withZoneSameInstant(ZoneOffset.UTC);

                return Date.from(zdt.toInstant());

            }

        } catch (Exception e) {
            throw new RuntimeException("Error converting local date time to UTC: " + e.getMessage());
        }
    }

    @Override
    public String convertDateToString(Date date) {
        return convertDateToString(date, "UTC");
    }

    @Override
    public String convertDateToString(Date date, String timeZone) {
        if (date == null) return null;
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss a", Locale.ENGLISH);
        if (timeZone != null && !timeZone.isEmpty()) {
            dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return dateFormat.format(date);
    }

    @Override
    public Map<String, Object> uploadFiles(MultipartFile[] files, Integer loginUserId, String folderName) {
        Map<String, Object> resBody = new HashMap<>();
        List<Map<String, String>> uploadedFiles = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                // Sanitize the filename
                String originalFilename = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]+", "_");

                // Determine the file type (video or image) based on extension
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
                boolean isVideo = fileExtension.matches("mp4|mkv|avi|mov");
                boolean isImage = fileExtension.matches("jpg|jpeg|png");

                if (!(isImage || isVideo)) {
                    resBody.put("status", "400");
                    resBody.put("message", "." + fileExtension + " File type not supported");
                    return resBody;
                }

                // Generate dynamic directory path
                String dynamicPath = loginUserId + "/" + "tempImage/" + folderName + "/";
                // Create directory if it doesn't exist
                File targetDirectory = new File(FILE_DIRECTORY + dynamicPath);
                if (!targetDirectory.exists()) {
                    targetDirectory.mkdirs();
                }

                String fullPath = FILE_DIRECTORY + dynamicPath + originalFilename;
                File targetFile = new File(fullPath);

                // Save the file
                file.transferTo(targetFile);

                //✅ Set file permissions to 755 (rwxr - xr - x)
                setFilePermissions(targetFile);

                // Add file info to response list
                String fileUrl = imageContextPath + dynamicPath.replace("\\", "/") + originalFilename; // Convert path to URL
                Map<String, String> fileInfo = new HashMap<>();
                fileInfo.put("imageName", originalFilename);
                fileInfo.put("imageURL", fileUrl);
                fileInfo.put("fileType", isVideo ? "video" : "image");
                uploadedFiles.add(fileInfo);
            }

            resBody.put("uploadedFiles", uploadedFiles);
            resBody.put("status", 200);
        } catch (Exception e) {
            e.printStackTrace();
            resBody.put("status", "error");
            resBody.put("message", e.getMessage());
        }

        return resBody;
    }

    @Override
    public String updateFileLocationForProfile(String image, Integer loginUserId, String folderName) {
        String[] arr = image.split("/");
        String originalFileName = arr[arr.length - 1];

        File tempImageDirectory = new File(FILE_DIRECTORY + loginUserId + "/tempImage/" + folderName);
        File destinationDirectory = new File(FILE_DIRECTORY + loginUserId + "/" + folderName);
        System.out.println("================= tempImageDirectory ================" + tempImageDirectory);
        System.out.println("================= destinationDirectory ================" + destinationDirectory);

        // Ensure the destination directory exists
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }

        File sourceFile = new File(tempImageDirectory, originalFileName);
        File destinationFile = new File(destinationDirectory, originalFileName);
        // Move file to the new location with overwrite if exists
        if (sourceFile.exists()) {
            try {
                FileUtil.copyFile(sourceFile, destinationFile);
                setFilePermissions(destinationFile);
                String imageDynamicPath = loginUserId + "/" + folderName + "/" + originalFileName;
                this.deleteDirectoryRecursively(tempImageDirectory);
                return imageContextPath + imageDynamicPath;
            } catch (IOException e) {
                throw new RuntimeException("File move error: " + e.getMessage(), e);
            }
        } else {
            return "Error";
        }
    }

    @Override
    public void deleteDirectoryRecursively(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectoryRecursively(file); // Recursively delete sub-files/sub-directories
                }
            }
        }
        if (directory.delete()) {
            System.out.println("Deleted: " + directory.getAbsolutePath());
        } else {
            System.out.println("Failed to delete: " + directory.getAbsolutePath());
        }
    }

    @Override
    public boolean sendEmail(String toEmail, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailFrom, "rxkz jnbt gyvt gckp");
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            return true;


//            MimeMessage message = sender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
//                    StandardCharsets.UTF_8.name());
//
//            helper.setFrom(mailFrom, mailFromName);
//            helper.setTo(toEmail);
//            helper.setSubject(subject);
//            helper.setText(body);
//
//            sender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void setFilePermissions(File file) {
        try {
            if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
                Files.setPosixFilePermissions(file.toPath(), perms);
            }
        } catch (Exception e) {
            // Don't fail the upload just because chmod failed
            e.printStackTrace();
        }
    }

    private String sanitizeFileName(String originalFileName) {
        return originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-]+", "_");
    }

    private void validateExtension(String safeName) {
        int dot = safeName.lastIndexOf(".");
        if (dot == -1) throw new RuntimeException("File has no extension");

        String ext = safeName.substring(dot + 1).toLowerCase();

        boolean isVideo = ext.matches("mp4|mkv|avi|mov");
        boolean isImage = ext.matches("jpg|jpeg|png");

        if (!(isImage || isVideo)) {
            throw new RuntimeException("." + ext + " File type not supported");
        }
    }

    private void mergeChunks(String chunkDirPath, File finalFile, int totalChunks) {
        // If complete is called again, avoid appending to old file
        if (finalFile.exists()) finalFile.delete();

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(finalFile, true))) {
            byte[] buffer = new byte[1024 * 1024]; // 1MB buffer

            for (int i = 0; i < totalChunks; i++) {
                File part = new File(chunkDirPath + i + ".part");
                if (!part.exists()) {
                    throw new RuntimeException("Missing chunk: " + i);
                }

                try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(part))) {
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to merge chunks: " + e.getMessage(), e);
        }
    }

    private void cleanupChunkDir(File chunkDir) {
        File[] files = chunkDir.listFiles();
        if (files != null) {
            for (File f : files) f.delete();
        }
        chunkDir.delete();
    }

    public static File optimizeImage(File inputFile) {
        try {
            BufferedImage original = ImageIO.read(inputFile);
            if (original == null) {
                // Not an image
                return inputFile;
            }

            int width = original.getWidth();
            int height = original.getHeight();

            int maxSide = Math.max(width, height);

            // 🔴 IMPORTANT: DO NOT UPSCALE
            if (maxSide <= MAX_DIM) {
                // Image already small enough → do nothing
                return inputFile;
            }

            // Resize ratio
            double scale = (double) MAX_DIM / maxSide;
            int newWidth = (int) Math.round(width * scale);
            int newHeight = (int) Math.round(height * scale);

            String name = inputFile.getName().toLowerCase();
            File outFile = new File(inputFile.getParentFile(), "opt_" + inputFile.getName());

            if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                Thumbnails.of(inputFile)
                        .size(newWidth, newHeight)
                        .outputQuality(0.6)  // visually lossless
                        .outputFormat("jpg")
                        .toFile(outFile);
            } else if (name.endsWith(".png")) {
                // PNG = lossless recompress + resize
                Thumbnails.of(inputFile)
                        .size(newWidth, newHeight)
                        .outputFormat("png")
                        .toFile(outFile);
            } else {
                return inputFile;
            }

            return outFile;

        } catch (Exception e) {
            e.printStackTrace();
            return inputFile;
        }
    }
}
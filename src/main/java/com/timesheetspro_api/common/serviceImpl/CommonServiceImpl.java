package com.timesheetspro_api.common.serviceImpl;

import com.timesheetspro_api.common.service.CommonService;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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

    @Autowired
    private JavaMailSender sender;

    @Override
    public Date convertStringToDate(String dateStr) {
        try {
            SimpleDateFormat formatter;

            // Determine format dynamically
            if (dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) { // Matches "MM/dd/yyyy"
                formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            } else if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) { // Matches "yyyy-MM-dd"
                formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            } else {
                formatter = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a", Locale.ENGLISH);
            }

            formatter.setTimeZone(TimeZone.getTimeZone("UTC")); // Convert input to UTC
            formatter.setLenient(false);
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Error converting date: " + dateStr + " - " + e.getMessage());
        }
    }

    @Override
    public String convertUtcToLocal(String utcTime, String timeZone) {
        try {
            // Ensure input format matches exactly
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm:ss a", Locale.ENGLISH);

            // Parse the UTC time correctly
            LocalDateTime localDateTime = LocalDateTime.parse(utcTime, inputFormatter);

            // Convert to UTC ZonedDateTime
            ZonedDateTime utcZonedDateTime = localDateTime.atZone(ZoneId.of("UTC"));

            // Convert to target time zone
            ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of(timeZone));

            // ✅ Corrected output format to match inputFormat in writeUserRecord
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm:ss a", Locale.ENGLISH);

            return localZonedDateTime.format(outputFormatter);
        } catch (Exception e) {
            System.err.println("Error parsing time: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Date convertLocalToUtc(String localDateTime, String timeZone, boolean hasTime) {
        try {
            SimpleDateFormat inputFormat;
            if (hasTime) {
                inputFormat = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss");
            } else {
                inputFormat = new SimpleDateFormat("MM/dd/yyyy");
            }
            inputFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date parsedDate = inputFormat.parse(localDateTime);
            return parsedDate; // parsedDate is now in UTC
        } catch (ParseException e) {
            throw new RuntimeException("Error converting local date time to UTC: " + e.getMessage());
        }
    }

    @Override
    public String convertDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy, hh:mm:ss a", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    @Override
    public Map<String, Object> uploadFiles(MultipartFile[] files, Long loginUserId, String folderName) {
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
                    resBody.put("status", 400);
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
            resBody.put("status", "error");
            resBody.put("message", e.getMessage());
        }

        return resBody;
    }

    @Override
    public String updateFileLocationForProfile(String image, Long loginUserId, String folderName) {
        String[] arr = image.split("/");
        String originalFileName = arr[arr.length - 1];

        File tempImageDirectory = new File(FILE_DIRECTORY + loginUserId + "/tempImage/" + folderName);
        File destinationDirectory = new File(FILE_DIRECTORY + loginUserId + "/" + folderName);
        System.out.println("================= tempImageDirectory ================"+tempImageDirectory);
        System.out.println("================= destinationDirectory ================"+destinationDirectory);

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

    private static void setFilePermissions(File file) throws IOException {
        // Use PosixFilePermissions for Unix-based systems
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            Files.setPosixFilePermissions(file.toPath(), perms);
        }
    }
}
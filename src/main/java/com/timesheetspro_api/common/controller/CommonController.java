package com.timesheetspro_api.common.controller;

import com.timesheetspro_api.auth.config.JwtTokenUtil;
import com.timesheetspro_api.common.response.ApiResponse;
import com.timesheetspro_api.common.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
public class CommonController {
    private static final Logger errorLogger = LoggerFactory.getLogger("errorLogger");

    @Autowired
    private CommonService commonService;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @PostMapping("/uploadFile")
    public ApiResponse<?> uploadFiles(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam String folderName,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) MultipartFile[] files, // For multiple files
            @RequestParam(required = false) MultipartFile file // For a single file
    ) {
        Map<String, Object> resBody = new HashMap<>();
        Integer loginUserId = null;
        try {

            if (authorizationHeader != null) {
                if (userId != null) {
                    loginUserId = userId;
                } else {
                    loginUserId = Integer.parseInt(jwtUtil.extractUserId(authorizationHeader.substring(7)).toString());
                }
            }
            // Handle both single and multiple files
            if (file != null) {
                files = new MultipartFile[]{file}; // Convert single file to array for consistency
            }

            if (files == null || files.length == 0) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "No files provided", "");
            }
            Map<String, Object> resBodyObjectMap = commonService.uploadFiles(files, loginUserId, folderName);

            if (resBodyObjectMap.get("status").equals("400")) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), resBodyObjectMap.get("message").toString(), "");
            }
            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Files uploaded successfully",
                    resBodyObjectMap.get("uploadedFiles")
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), resBody);
        }
    }


    @GetMapping("/getTimezones")
    public ApiResponse<?> getTimezones() {
        try {
            // Static JSON-like data
            List<String> timezoneList = Arrays.asList(
                    "Asia/Kolkata",
                    "Asia/Dubai",
                    "Europe/London",
                    "Australia/Sydney",
                    "America/Adak",
                    "America/Anchorage",
                    "America/Anguilla",
                    "America/Antigua",
                    "America/Araguaina",
                    "America/Argentina/Buenos_Aires",
                    "America/Argentina/Catamarca",
                    "America/Argentina/ComodRivadavia",
                    "America/Argentina/Cordoba",
                    "America/Argentina/Jujuy",
                    "America/Argentina/La_Rioja",
                    "America/Argentina/Mendoza",
                    "America/Argentina/Rio_Gallegos",
                    "America/Argentina/Salta",
                    "America/Argentina/San_Juan",
                    "America/Argentina/San_Luis",
                    "America/Argentina/Tucuman",
                    "America/Argentina/Ushuaia",
                    "America/Aruba",
                    "America/Asuncion",
                    "America/Atikokan",
                    "America/Atka",
                    "America/Bahia",
                    "America/Bahia_Banderas",
                    "America/Barbados",
                    "America/Belem",
                    "America/Belize",
                    "America/Blanc-Sablon",
                    "America/Boa_Vista",
                    "America/Bogota",
                    "America/Boise",
                    "America/Buenos_Aires",
                    "America/Cambridge_Bay",
                    "America/Campo_Grande",
                    "America/Cancun",
                    "America/Caracas",
                    "America/Catamarca",
                    "America/Cayenne",
                    "America/Cayman",
                    "America/Chicago",
                    "America/Chihuahua",
                    "America/Ciudad_Juarez",
                    "America/Coral_Harbour",
                    "America/Cordoba",
                    "America/Costa_Rica",
                    "America/Creston",
                    "America/Cuiaba",
                    "America/Curacao",
                    "America/Danmarkshavn",
                    "America/Dawson",
                    "America/Dawson_Creek",
                    "America/Denver",
                    "America/Detroit",
                    "America/Dominica",
                    "America/Edmonton",
                    "America/Eirunepe",
                    "America/El_Salvador",
                    "America/Ensenada",
                    "America/Fort_Nelson",
                    "America/Fort_Wayne",
                    "America/Fortaleza",
                    "America/Glace_Bay",
                    "America/Godthab",
                    "America/Goose_Bay",
                    "America/Grand_Turk",
                    "America/Grenada",
                    "America/Guadeloupe",
                    "America/Guatemala",
                    "America/Guayaquil",
                    "America/Guyana",
                    "America/Halifax",
                    "America/Havana",
                    "America/Hermosillo",
                    "America/Indiana/Indianapolis",
                    "America/Indiana/Knox",
                    "America/Indiana/Marengo",
                    "America/Indiana/Petersburg",
                    "America/Indiana/Tell_City",
                    "America/Indiana/Vevay",
                    "America/Indiana/Vincennes",
                    "America/Indiana/Winamac",
                    "America/Indianapolis",
                    "America/Inuvik",
                    "America/Iqaluit",
                    "America/Jamaica",
                    "America/Jujuy",
                    "America/Juneau",
                    "America/Kentucky/Louisville",
                    "America/Kentucky/Monticello",
                    "America/Knox_IN",
                    "America/Kralendijk",
                    "America/La_Paz",
                    "America/Lima",
                    "America/Los_Angeles",
                    "America/Louisville",
                    "America/Lower_Princes",
                    "America/Maceio",
                    "America/Managua",
                    "America/Manaus",
                    "America/Marigot",
                    "America/Martinique",
                    "America/Matamoros",
                    "America/Mazatlan",
                    "America/Mendoza",
                    "America/Menominee",
                    "America/Merida",
                    "America/Metlakatla",
                    "America/Mexico_City",
                    "America/Miquelon",
                    "America/Moncton",
                    "America/Monterrey",
                    "America/Montevideo",
                    "America/Montreal",
                    "America/Montserrat",
                    "America/Nassau",
                    "America/New_York",
                    "America/Nipigon",
                    "America/Nome",
                    "America/Noronha",
                    "America/North_Dakota/Beulah",
                    "America/North_Dakota/Center",
                    "America/North_Dakota/New_Salem",
                    "America/Nuuk",
                    "America/Ojinaga",
                    "America/Panama",
                    "America/Pangnirtung",
                    "America/Paramaribo",
                    "America/Phoenix",
                    "America/Port_of_Spain",
                    "America/Port-au-Prince",
                    "America/Porto_Acre",
                    "America/Porto_Velho",
                    "America/Puerto_Rico",
                    "America/Punta_Arenas",
                    "America/Rainy_River",
                    "America/Rankin_Inlet",
                    "America/Recife",
                    "America/Regina",
                    "America/Resolute",
                    "America/Rio_Branco",
                    "America/Rosario",
                    "America/Santa_Isabel",
                    "America/Santarem",
                    "America/Santiago",
                    "America/Santo_Domingo",
                    "America/Sao_Paulo",
                    "America/Scoresbysund",
                    "America/Shiprock",
                    "America/Sitka",
                    "America/St_Barthelemy",
                    "America/St_Johns",
                    "America/St_Kitts",
                    "America/St_Lucia",
                    "America/St_Thomas",
                    "America/St_Vincent",
                    "America/Swift_Current",
                    "America/Tegucigalpa",
                    "America/Thule",
                    "America/Thunder_Bay",
                    "America/Tijuana",
                    "America/Toronto",
                    "America/Tortola",
                    "America/Vancouver",
                    "America/Virgin",
                    "America/Whitehorse",
                    "America/Winnipeg",
                    "America/Yakutat"
            );

            return new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Time Zones fetched successfully",
                    timezoneList
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), Collections.emptyList());
        }
    }
}


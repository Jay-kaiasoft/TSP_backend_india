package com.timesheetspro_api.common.constants;

import java.util.List;
import java.util.Map;

public interface Constants {
    // common constant
    final String MSG = "msg";
    final String CODE = "code";
    final String STATUS = "status";
    final String INVALID_TOKEN = "Access Denied";
    final String STATUS_FAILURE = "failure";
    final String CHARACTER_ENCODING_UTF_8 = "UTF-8";
    final String REQUEST_HEADER_AUTHORIZATION = "Authorization";
    final String AUTHORIZATION_BEARER = "Bearer ";


    final List<Map<String, Object>> PT_RULES = List.of(
            Map.of(
                    "state", "Andhra Pradesh",
                    "sort_name", "AP",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 15000
            ),
            Map.of(
                    "state", "Arunachal Pradesh",
                    "sort_name", "AR",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Assam",
                    "sort_name", "AS",
                    "ptPerMonth", 208,
                    "apply_on_greter_then", 10000
            ),
            Map.of(
                    "state", "Bihar",
                    "sort_name", "BR",
                    "ptPerMonth", 208,
                    "apply_on_greter_then", 8334
            ),
            Map.of(
                    "state", "Chhattisgarh",
                    "sort_name", "CG",
                    "ptPerMonth", 208,
                    "apply_on_greter_then", 15000
            ),
            Map.of(
                    "state", "Goa",
                    "sort_name", "GA",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Gujarat",
                    "sort_name", "GJ",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 12000
            ),
            Map.of(
                    "state", "Haryana",
                    "sort_name", "HR",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Himachal Pradesh",
                    "sort_name", "HP",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Jharkhand",
                    "sort_name", "JH",
                    "ptPerMonth", 208,
                    "apply_on_greter_then", 10000
            ),
            Map.of(
                    "state", "Karnataka",
                    "sort_name", "KA",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 15000
            ),
            Map.of(
                    "state", "Kerala",
                    "sort_name", "KL",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 12500
            ),
            Map.of(
                    "state", "Madhya Pradesh",
                    "sort_name", "MP",
                    "ptPerMonth", 208,
                    "apply_on_greter_then", 15000
            ),
            Map.of(
                    "state", "Maharashtra",
                    "sort_name", "MH",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 10000,
                    "extra", "₹300 in February"
            ),
            Map.of(
                    "state", "Manipur",
                    "sort_name", "MN",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Meghalaya",
                    "sort_name", "ML",
                    "ptPerMonth", 208,
                    "apply_on_greter_then", 10000
            ),
            Map.of(
                    "state", "Mizoram",
                    "sort_name", "MZ",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Nagaland",
                    "sort_name", "NL",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Odisha",
                    "sort_name", "OR",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 10000
            ),
            Map.of(
                    "state", "Punjab",
                    "sort_name", "PB",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Rajasthan",
                    "sort_name", "RJ",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Sikkim",
                    "sort_name", "SK",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 10000
            ),
            Map.of(
                    "state", "Tamil Nadu",
                    "sort_name", "TN",
                    "ptPerMonth", 208,
                    "apply_on_greter_then", 12500
            ),
            Map.of(
                    "state", "Telangana",
                    "sort_name", "TG",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 15000
            ),
            Map.of(
                    "state", "Tripura",
                    "sort_name", "TR",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 10000
            ),
            Map.of(
                    "state", "Uttar Pradesh",
                    "sort_name", "UP",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "Uttarakhand",
                    "sort_name", "UK",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            ),
            Map.of(
                    "state", "West Bengal",
                    "sort_name", "WB",
                    "ptPerMonth", 200,
                    "apply_on_greter_then", 10001,
                    "extra", "Slab-based up to ₹2500/year"
            ),
            Map.of(
                    "state", "Delhi",
                    "sort_name", "DL",
                    "ptPerMonth", 0,
                    "apply_on_greter_then", 0,
                    "extra", "PT not applicable"
            )
    );
}


package com.timesheetspro_api.common.dto.country;

import lombok.Data;

@Data
public class CountryDto {
    private int id;
    private String iso2;
    private String cntName;
    private String longName;
    private int oid;
    private String cntCode;
    private int phoneMinLength;
    private int phoneMaxLength;
}

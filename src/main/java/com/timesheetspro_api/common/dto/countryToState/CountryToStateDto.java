package com.timesheetspro_api.common.dto.countryToState;

import lombok.Data;

@Data
public class CountryToStateDto {
    private int id;
    private int countryId;
    private String stateLong;
    private String stateShort;
    private Character stateCapital;
}

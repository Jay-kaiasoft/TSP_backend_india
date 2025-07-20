package com.timesheetspro_api.contractor.service;

import com.timesheetspro_api.common.dto.contractor.ContractorDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ContractorService {

    List<ContractorDto> getAllContractors();

    ContractorDto getContractor(Long id);

    ContractorDto createContractor(ContractorDto contractorDto);

    ContractorDto updateContractor(Long id, ContractorDto contractorDto);

    void deleteContractor(Long id);
}

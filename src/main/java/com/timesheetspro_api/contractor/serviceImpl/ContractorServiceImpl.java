package com.timesheetspro_api.contractor.serviceImpl;

import com.timesheetspro_api.common.dto.contractor.ContractorDto;
import com.timesheetspro_api.common.model.contractor.Contractor;
import com.timesheetspro_api.common.repository.ContractorRepository;
import com.timesheetspro_api.contractor.service.ContractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContractorServiceImpl implements ContractorService {
    @Autowired
    private ContractorRepository contractorRepository;

    public List<ContractorDto> getAllContractors() {
        try {
            List<Contractor> contractor = this.contractorRepository.findAll();
            List<ContractorDto> contractorDtoList = new ArrayList<>();
            if (!contractor.isEmpty()){
                for (Contractor contractor1:contractor){
                    contractorDtoList.add(this.getContractor(contractor1.getId()));
                }
            }
            return contractorDtoList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public ContractorDto getContractor(Long id) {
        try {
            Contractor contractor = this.contractorRepository.findById(id).orElseThrow(() -> new RuntimeException("Contractor not found"));
            ContractorDto contractorDto = new ContractorDto();
            contractorDto.setId(contractor.getId());
            contractorDto.setContractorName(contractor.getContractorName());
            return contractorDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public ContractorDto createContractor(ContractorDto contractorDto) {
        try {
            Contractor contractor = new Contractor();
            contractor.setContractorName(contractorDto.getContractorName());
            this.contractorRepository.save(contractor);
            return contractorDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public ContractorDto updateContractor(Long id, ContractorDto contractorDto) {
        try {
            Contractor contractor = this.contractorRepository.findById(id).orElseThrow(() -> new RuntimeException("Contractor not found"));
            contractor.setContractorName(contractorDto.getContractorName());
            this.contractorRepository.save(contractor);
            return contractorDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteContractor(Long id) {
        try {
            Contractor contractor = this.contractorRepository.findById(id).orElseThrow(() -> new RuntimeException("Contractor not found"));
            this.contractorRepository.delete(contractor);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}

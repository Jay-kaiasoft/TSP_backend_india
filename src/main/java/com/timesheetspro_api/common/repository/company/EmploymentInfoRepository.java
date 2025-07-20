package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.employmentInfo.EmploymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmploymentInfoRepository extends JpaRepository<EmploymentInfo, Integer> {
}

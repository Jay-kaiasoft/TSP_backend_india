package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.holidayTemplates.HolidayTemplates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayTemplatesRepository extends JpaRepository<HolidayTemplates, Integer> {
    @Query("SELECT h FROM HolidayTemplates h WHERE h.companyDetails.id=:id")
    List<HolidayTemplates> findByCompanyId(Integer id);
}

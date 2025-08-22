package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.holidayTemplateDetails.HolidayTemplateDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayTemplateDetailsRepository extends JpaRepository<HolidayTemplateDetails, Integer> {
    @Query("SELECT h FROM HolidayTemplateDetails h WHERE h.holidayTemplates.id=:id")
    List<HolidayTemplateDetails> findByTemplatesId(Integer id);
}

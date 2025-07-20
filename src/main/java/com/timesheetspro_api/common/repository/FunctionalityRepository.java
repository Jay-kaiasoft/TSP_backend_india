package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.functionality.Functionality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionalityRepository extends JpaRepository<Functionality, Long> {
}

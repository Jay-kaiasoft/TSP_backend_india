package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.userShift.UserShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserShiftRepository extends JpaRepository<UserShift, Long> {
}

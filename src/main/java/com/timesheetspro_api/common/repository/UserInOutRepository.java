package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.UserInOut.UserInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserInOutRepository extends JpaRepository<UserInOut, Long>, JpaSpecificationExecutor<UserInOut> {
    @Query("SELECT u FROM UserInOut u WHERE (:userId IS NULL OR u.user.id = :userId) AND u.timeOut BETWEEN :startOfDay AND :endOfDay")
    List<UserInOut> findByUserIdAndToday(@Param("userId") int userId,
                                         @Param("startOfDay") Date startOfDay,
                                         @Param("endOfDay") Date endOfDay);

    @Query("SELECT COUNT(DISTINCT u.user.id) FROM UserInOut u WHERE u.timeIn IS NOT NULL AND u.timeIn BETWEEN :startOfDay AND :endOfDay")
    long countCheckedInUsers(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);

    @Query("SELECT COUNT(DISTINCT u.user.id) FROM UserInOut u WHERE u.timeOut BETWEEN :startOfDay AND :endOfDay")
    long countCheckedOutUsers(@Param("startOfDay") Date startOfDay, @Param("endOfDay") Date endOfDay);

    @Query("SELECT u FROM UserInOut u WHERE u.timeOut IS NULL AND u.user.id=:userId")
    UserInOut getLastRecord(@Param("userId") int userId);

    @Query("SELECT u FROM UserInOut u WHERE u.timeOut IS NOT NULL AND u.user.id=:userId")
    List<UserInOut> getAllRecordsByUsers(@Param("userId") int userId);

    @Query("SELECT u FROM UserInOut u WHERE u.timeOut IS NULL AND u.user.id=:userId")
    UserInOut getCurrentUserRecord(@Param("userId") int userId);
}

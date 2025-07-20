package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    @Query(value = "SELECT u FROM Users u WHERE u.employeeId = :employeeId")
    Users findByEmployeeId(@Param("employeeId") String employeeId);

    @Query(value = "SELECT u FROM Users u WHERE u.email = :email AND u.userName = :userName")
    Users findByEmailAndUserName(@Param("email") String email, @Param("userName") String userName);

//    @Query(value = "SELECT u FROM Users u WHERE u.userName = :userName")
//    Users findByUserName(@Param("userName") String userName);

    @Query(value = "SELECT u FROM Users u WHERE u.personalIdentificationNumber = :personalIdentificationNumber")
    Users findByPersonalIdentificationNumber(@Param("personalIdentificationNumber") String personalIdentificationNumber);

    @Query(value = "SELECT u FROM Users u WHERE u.phone = :phone")
    Users findByPhoneNumber(@Param("phone") String phone);

    @Query(value = "SELECT u FROM Users u WHERE u.userName = :userName")
    Users findByUsername(@Param("userName") String userName);

    @Query(value = "SELECT * FROM Users ORDER BY user_Id DESC LIMIT 1", nativeQuery = true)
    Users findLastUser();

    @Query(value = "SELECT u FROM Users u WHERE u.id = :id")
    Users findUserById(@Param("id") Long id);

    @Query("SELECT u FROM Users u WHERE u.id !=:userId AND u.userName=:userName")
    Users findAllExceptUserByUserName(@Param("userId") Long userId, @Param("userName") String userName);

}

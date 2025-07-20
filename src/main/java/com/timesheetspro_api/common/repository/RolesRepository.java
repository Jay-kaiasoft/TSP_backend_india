package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.roles.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
    @Query("SELECT r FROM Roles r WHERE r.roleId != 1")
    List<Roles> findRolesExceptOwner();

    @Query("SELECT r FROM Roles r WHERE LOWER(r.roleName) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<Roles> getRolesByName(@Param("searchKey")String searchKey, Pageable pageable);

    @Query("SELECT r FROM Roles r WHERE r.roleId=:roleId")
    Roles findRoleById(Long roleId);
}

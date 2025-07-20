package com.timesheetspro_api.common.repository;

import com.timesheetspro_api.common.model.module.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface ModuleRepository extends JpaRepository<Module, Long> {
    @Query("SELECT m FROM Module m WHERE LOWER(m.moduleName) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<Module> getModuleByName(@Param("searchKey")String searchKey, Pageable pageable);

    @Query("SELECT r FROM Module r WHERE r.moduleId=:moduleId")
    Module findModuleById(Long moduleId);

    @Query("SELECT r FROM Module r WHERE r.functionality.id=:id")
    Page<Module> findModulesByFunctionalityId(Long id, Pageable pageable);

    @Query("SELECT r FROM Module r WHERE r.functionality.id=:id")
    List<Module> findModulesByFunctionalityId(Long id);

    @Query("SELECT r FROM Module r WHERE r.functionality.id=:id AND LOWER(r.moduleName) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<Module> findModulesByFunctionalityIdAndName(@Param("id")Long id, @Param("searchKey")String searchKey, Pageable pageable);
}

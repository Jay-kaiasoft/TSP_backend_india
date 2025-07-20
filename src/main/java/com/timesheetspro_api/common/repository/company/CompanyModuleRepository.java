package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.companyModules.CompanyModules;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface CompanyModuleRepository extends JpaRepository<CompanyModules, Integer> {
    @Query("SELECT m FROM CompanyModules m WHERE LOWER(m.moduleName) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<CompanyModules> getModuleByName(@Param("searchKey")String searchKey, Pageable pageable);

    @Query("SELECT r FROM CompanyModules r WHERE r.moduleId=:moduleId")
    CompanyModules findModuleById(int moduleId);

    @Query("SELECT r FROM CompanyModules r WHERE r.functionality.id=:id")
    Page<CompanyModules> findModulesByFunctionalityId(int id, Pageable pageable);

    @Query("SELECT r FROM CompanyModules r WHERE r.functionality.id=:id")
    List<CompanyModules> findModulesByFunctionalityId(int id);

    @Query("SELECT r FROM CompanyModules r WHERE r.functionality.id=:id AND LOWER(r.moduleName) LIKE LOWER(CONCAT('%', :searchKey, '%'))")
    Page<CompanyModules> findModulesByFunctionalityIdAndName(@Param("id")int id, @Param("searchKey")String searchKey, Pageable pageable);
}

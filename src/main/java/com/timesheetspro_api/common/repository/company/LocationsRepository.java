package com.timesheetspro_api.common.repository.company;

import com.timesheetspro_api.common.model.locations.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface LocationsRepository extends JpaRepository<Locations, Integer> {
    @Query("SELECT l FROM Locations l WHERE l.companyDetails.id=:companyId")
    List<Locations> findByCompanyId(int companyId);

    @Query("SELECT l FROM Locations l WHERE l.companyDetails.id=:companyId AND l.isActive=1")
    List<Locations> findByCompanyActiveLocations(int companyId);

    @Query("SELECT l FROM Locations l WHERE l.id=:id")
    Locations findByLocationId(int id);

    @Query("SELECT l FROM Locations l WHERE l.locationName=:locationName AND l.companyDetails.id=:companyId")
    Locations findByLocationName(String locationName, int companyId);

    @Query("SELECT l FROM Locations l WHERE l.locationName = :locationName AND l.id <> :id")
    Locations findByLocationNameAndIdNot(String locationName, int id);

}

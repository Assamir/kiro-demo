package com.insurance.backoffice.infrastructure.repository;

import com.insurance.backoffice.domain.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Vehicle entity operations.
 * Provides data access methods for vehicle management functionality.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    /**
     * Finds a vehicle by its registration number.
     * Registration number is unique identifier for vehicles.
     * 
     * @param registrationNumber the registration number to search for
     * @return Optional containing the vehicle if found, empty otherwise
     */
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    
    /**
     * Finds a vehicle by its VIN number.
     * VIN is unique identifier for vehicles.
     * 
     * @param vin the VIN number to search for
     * @return Optional containing the vehicle if found, empty otherwise
     */
    Optional<Vehicle> findByVin(String vin);
    
    /**
     * Checks if a vehicle exists with the given registration number.
     * Used for validation during vehicle creation.
     * 
     * @param registrationNumber the registration number to check
     * @return true if a vehicle exists with this registration number, false otherwise
     */
    boolean existsByRegistrationNumber(String registrationNumber);
    
    /**
     * Checks if a vehicle exists with the given VIN number.
     * Used for validation during vehicle creation.
     * 
     * @param vin the VIN number to check
     * @return true if a vehicle exists with this VIN, false otherwise
     */
    boolean existsByVin(String vin);
    
    /**
     * Finds vehicles by make and model (case-insensitive).
     * Used for vehicle search functionality.
     * 
     * @param make the vehicle make to search for
     * @param model the vehicle model to search for
     * @return list of vehicles matching the make and model
     */
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.make) = LOWER(:make) AND LOWER(v.model) = LOWER(:model)")
    List<Vehicle> findByMakeAndModelIgnoreCase(@Param("make") String make, @Param("model") String model);
    
    /**
     * Finds vehicles by make (case-insensitive).
     * Used for vehicle filtering by manufacturer.
     * 
     * @param make the vehicle make to search for
     * @return list of vehicles with the specified make
     */
    @Query("SELECT v FROM Vehicle v WHERE LOWER(v.make) = LOWER(:make)")
    List<Vehicle> findByMakeIgnoreCase(@Param("make") String make);
    
    /**
     * Finds vehicles manufactured in a specific year.
     * Used for vehicle filtering by age.
     * 
     * @param year the year of manufacture
     * @return list of vehicles manufactured in the specified year
     */
    List<Vehicle> findByYearOfManufacture(Integer year);
    
    /**
     * Finds vehicles manufactured between two years (inclusive).
     * Used for vehicle filtering by age range.
     * 
     * @param startYear the start year (inclusive)
     * @param endYear the end year (inclusive)
     * @return list of vehicles manufactured within the specified range
     */
    List<Vehicle> findByYearOfManufactureBetween(Integer startYear, Integer endYear);
    
    /**
     * Finds vehicles first registered after a specific date.
     * Used for filtering newer vehicles.
     * 
     * @param date the date to compare against
     * @return list of vehicles first registered after the specified date
     */
    List<Vehicle> findByFirstRegistrationDateAfter(LocalDate date);
    
    /**
     * Finds vehicles that have active policies.
     * Used for reporting and vehicle management.
     * 
     * @return list of vehicles with at least one active policy
     */
    @Query("SELECT DISTINCT v FROM Vehicle v JOIN v.policies p WHERE p.status = 'ACTIVE'")
    List<Vehicle> findVehiclesWithActivePolicies();
}
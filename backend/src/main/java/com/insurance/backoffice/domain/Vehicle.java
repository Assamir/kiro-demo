package com.insurance.backoffice.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a vehicle in the insurance system.
 * Contains technical and registration information required for policy issuance.
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String make;
    
    @Column(nullable = false, length = 50)
    private String model;
    
    @Column(name = "year_of_manufacture", nullable = false)
    private Integer yearOfManufacture;
    
    @Column(name = "registration_number", unique = true, nullable = false, length = 20)
    private String registrationNumber;
    
    @Column(unique = true, nullable = false, length = 17)
    private String vin;
    
    @Column(name = "engine_capacity", nullable = false)
    private Integer engineCapacity;
    
    @Column(nullable = false)
    private Integer power;
    
    @Column(name = "first_registration_date", nullable = false)
    private LocalDate firstRegistrationDate;
    
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>();
    
    // Default constructor for JPA and testing
    public Vehicle() {}
    
    // Private constructor for Builder pattern
    private Vehicle(Builder builder) {
        this.id = builder.id;
        this.make = builder.make;
        this.model = builder.model;
        this.yearOfManufacture = builder.yearOfManufacture;
        this.registrationNumber = builder.registrationNumber;
        this.vin = builder.vin;
        this.engineCapacity = builder.engineCapacity;
        this.power = builder.power;
        this.firstRegistrationDate = builder.firstRegistrationDate;
    }
    
    /**
     * Returns the full vehicle description (make and model).
     * Clean Code: Intention-revealing method name.
     */
    public String getFullDescription() {
        return make + " " + model;
    }
    
    /**
     * Calculates the age of the vehicle in years from first registration.
     * Clean Code: Business logic encapsulated in domain object.
     */
    public int getAgeInYears() {
        return Period.between(firstRegistrationDate, LocalDate.now()).getYears();
    }
    
    /**
     * Determines if the vehicle is considered new (less than 1 year old).
     * Clean Code: Tell, don't ask principle.
     */
    public boolean isNew() {
        return getAgeInYears() < 1;
    }
    
    /**
     * Adds a policy to this vehicle's policy list.
     * Clean Code: Encapsulates relationship management.
     */
    public void addPolicy(Policy policy) {
        policies.add(policy);
        policy.setVehicle(this);
    }
    
    /**
     * Removes a policy from this vehicle's policy list.
     * Clean Code: Maintains bidirectional relationship integrity.
     */
    public void removePolicy(Policy policy) {
        policies.remove(policy);
        policy.setVehicle(null);
    }
    
    /**
     * Checks if the vehicle has any active policies.
     * Clean Code: Business rule encapsulated in domain object.
     */
    public boolean hasActivePolicies() {
        return policies.stream()
                .anyMatch(policy -> PolicyStatus.ACTIVE.equals(policy.getStatus()));
    }
    
    // Getters
    public Long getId() { return id; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getVin() { return vin; }
    public Integer getEngineCapacity() { return engineCapacity; }
    public Integer getPower() { return power; }
    public LocalDate getFirstRegistrationDate() { return firstRegistrationDate; }
    public List<Policy> getPolicies() { return new ArrayList<>(policies); }
    
    // Setters for mutable fields
    public void setMake(String make) { this.make = make; }
    public void setModel(String model) { this.model = model; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public void setVin(String vin) { this.vin = vin; }
    public void setEngineCapacity(Integer engineCapacity) { this.engineCapacity = engineCapacity; }
    public void setPower(Integer power) { this.power = power; }
    public void setFirstRegistrationDate(LocalDate firstRegistrationDate) { this.firstRegistrationDate = firstRegistrationDate; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id) && 
               Objects.equals(registrationNumber, vehicle.registrationNumber) &&
               Objects.equals(vin, vehicle.vin);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, registrationNumber, vin);
    }
    
    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", vin='" + vin + '\'' +
                '}';
    }
    
    /**
     * Builder pattern implementation for clean object creation.
     */
    public static class Builder {
        private Long id;
        private String make;
        private String model;
        private Integer yearOfManufacture;
        private String registrationNumber;
        private String vin;
        private Integer engineCapacity;
        private Integer power;
        private LocalDate firstRegistrationDate;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder make(String make) {
            this.make = make;
            return this;
        }
        
        public Builder model(String model) {
            this.model = model;
            return this;
        }
        
        public Builder yearOfManufacture(Integer yearOfManufacture) {
            this.yearOfManufacture = yearOfManufacture;
            return this;
        }
        
        public Builder registrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
            return this;
        }
        
        public Builder vin(String vin) {
            this.vin = vin;
            return this;
        }
        
        public Builder engineCapacity(Integer engineCapacity) {
            this.engineCapacity = engineCapacity;
            return this;
        }
        
        public Builder power(Integer power) {
            this.power = power;
            return this;
        }
        
        public Builder firstRegistrationDate(LocalDate firstRegistrationDate) {
            this.firstRegistrationDate = firstRegistrationDate;
            return this;
        }
        
        public Vehicle build() {
            validateRequiredFields();
            return new Vehicle(this);
        }
        
        private void validateRequiredFields() {
            if (make == null || make.trim().isEmpty()) {
                throw new IllegalArgumentException("Make is required");
            }
            if (model == null || model.trim().isEmpty()) {
                throw new IllegalArgumentException("Model is required");
            }
            if (yearOfManufacture == null) {
                throw new IllegalArgumentException("Year of manufacture is required");
            }
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number is required");
            }
            if (vin == null || vin.trim().isEmpty()) {
                throw new IllegalArgumentException("VIN is required");
            }
            if (engineCapacity == null) {
                throw new IllegalArgumentException("Engine capacity is required");
            }
            if (power == null) {
                throw new IllegalArgumentException("Power is required");
            }
            if (firstRegistrationDate == null) {
                throw new IllegalArgumentException("First registration date is required");
            }
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
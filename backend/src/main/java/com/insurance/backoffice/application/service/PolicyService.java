package com.insurance.backoffice.application.service;

import com.insurance.backoffice.domain.*;
import com.insurance.backoffice.infrastructure.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service class for policy management operations.
 * Implements business logic for policy creation, update, and cancellation.
 * Clean Code: Single Responsibility - handles only policy-related business logic.
 */
@Service
@Transactional
public class PolicyService {
    
    private final PolicyRepository policyRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final RatingService ratingService;
    
    @Autowired
    public PolicyService(PolicyRepository policyRepository, 
                        ClientRepository clientRepository,
                        VehicleRepository vehicleRepository,
                        RatingService ratingService) {
        this.policyRepository = policyRepository;
        this.clientRepository = clientRepository;
        this.vehicleRepository = vehicleRepository;
        this.ratingService = ratingService;
    }
    
    /**
     * Creates a new insurance policy.
     * Clean Code: Intention-revealing method name with clear business purpose.
     * 
     * @param clientId the ID of the client
     * @param vehicleId the ID of the vehicle
     * @param insuranceType the type of insurance
     * @param startDate the policy start date
     * @param endDate the policy end date
     * @param discountSurcharge optional discount or surcharge amount
     * @return the created policy
     * @throws EntityNotFoundException if client or vehicle not found
     * @throws IllegalArgumentException if policy data is invalid
     */
    public Policy createPolicy(Long clientId, Long vehicleId, InsuranceType insuranceType,
                              LocalDate startDate, LocalDate endDate, BigDecimal discountSurcharge,
                              BigDecimal amountGuaranteed, String coverageArea) {
        
        // Validate input parameters
        validatePolicyCreationParameters(clientId, vehicleId, insuranceType, startDate, endDate);
        
        // Fetch client and vehicle
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + clientId));
        
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with ID: " + vehicleId));
        
        // Calculate premium using rating service
        BigDecimal premium = ratingService.calculatePremium(insuranceType, vehicle, startDate);
        
        // Generate unique policy number
        String policyNumber = generatePolicyNumber(insuranceType);
        
        // Create policy using builder pattern
        Policy policy = Policy.builder()
                .policyNumber(policyNumber)
                .issueDate(LocalDate.now())
                .startDate(startDate)
                .endDate(endDate)
                .status(PolicyStatus.ACTIVE)
                .insuranceType(insuranceType)
                .premium(premium)
                .discountSurcharge(discountSurcharge)
                .amountGuaranteed(amountGuaranteed)
                .coverageArea(coverageArea)
                .client(client)
                .vehicle(vehicle)
                .build();
        
        return policyRepository.save(policy);
    }
    
    /**
     * Updates an existing policy.
     * Clean Code: Business logic encapsulated with proper validation.
     * 
     * @param policyId the ID of the policy to update
     * @param startDate the new start date
     * @param endDate the new end date
     * @param discountSurcharge the new discount/surcharge amount
     * @param amountGuaranteed the guaranteed amount for coverage
     * @param coverageArea the coverage area
     * @return the updated policy
     * @throws EntityNotFoundException if policy not found
     * @throws IllegalArgumentException if update data is invalid
     */
    public Policy updatePolicy(Long policyId, LocalDate startDate, LocalDate endDate, 
                              BigDecimal discountSurcharge, BigDecimal amountGuaranteed, String coverageArea) {
        
        Policy existingPolicy = findPolicyById(policyId);
        
        // Validate that policy can be updated
        if (existingPolicy.isCanceled()) {
            throw new IllegalStateException("Cannot update a canceled policy");
        }
        
        // Validate dates using update-specific validation
        validatePolicyDatesForUpdate(startDate, endDate, existingPolicy.getIssueDate());
        
        // Update policy fields
        existingPolicy.setStartDate(startDate);
        existingPolicy.setEndDate(endDate);
        existingPolicy.setDiscountSurcharge(discountSurcharge != null ? discountSurcharge : BigDecimal.ZERO);
        
        // Update coverage details if provided
        if (amountGuaranteed != null) {
            existingPolicy.setAmountGuaranteed(amountGuaranteed);
        }
        if (coverageArea != null && !coverageArea.trim().isEmpty()) {
            existingPolicy.setCoverageArea(coverageArea.trim());
        }
        
        // Recalculate premium if dates changed
        BigDecimal newPremium = ratingService.calculatePremium(
                existingPolicy.getInsuranceType(), 
                existingPolicy.getVehicle(), 
                startDate);
        existingPolicy.setPremium(newPremium);
        
        return policyRepository.save(existingPolicy);
    }
    
    /**
     * Cancels a policy by setting its status to CANCELED.
     * Clean Code: Clear business operation with proper validation.
     * 
     * @param policyId the ID of the policy to cancel
     * @return the canceled policy
     * @throws EntityNotFoundException if policy not found
     * @throws IllegalStateException if policy cannot be canceled
     */
    public Policy cancelPolicy(Long policyId) {
        Policy policy = findPolicyById(policyId);
        
        // Use domain method for cancellation logic
        policy.cancel();
        
        return policyRepository.save(policy);
    }
    
    /**
     * Finds a policy by ID.
     * Clean Code: Simple, focused method with clear purpose.
     * 
     * @param policyId the policy ID
     * @return the policy
     * @throws EntityNotFoundException if policy not found
     */
    @Transactional(readOnly = true)
    public Policy findPolicyById(Long policyId) {
        return policyRepository.findById(policyId)
                .orElseThrow(() -> new EntityNotFoundException("Policy not found with ID: " + policyId));
    }
    
    /**
     * Finds a policy by policy number.
     * Clean Code: Intention-revealing method name.
     * 
     * @param policyNumber the policy number
     * @return the policy
     * @throws EntityNotFoundException if policy not found
     */
    @Transactional(readOnly = true)
    public Policy findPolicyByNumber(String policyNumber) {
        return policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new EntityNotFoundException("Policy not found with number: " + policyNumber));
    }
    
    /**
     * Finds all policies for a specific client.
     * Clean Code: Key requirement implementation for policy search by client.
     * 
     * @param clientId the client ID
     * @return list of policies for the client
     */
    @Transactional(readOnly = true)
    public List<Policy> findPoliciesByClient(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        return policyRepository.findByClientIdOrderByIssueDateDesc(clientId);
    }
    
    /**
     * Finds all policies for a specific vehicle.
     * Clean Code: Focused method for vehicle-based queries.
     * 
     * @param vehicleId the vehicle ID
     * @return list of policies for the vehicle
     */
    @Transactional(readOnly = true)
    public List<Policy> findPoliciesByVehicle(Long vehicleId) {
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID cannot be null");
        }
        return policyRepository.findByVehicleId(vehicleId);
    }
    
    /**
     * Finds policies by status.
     * Clean Code: Simple filtering method.
     * 
     * @param status the policy status
     * @return list of policies with the specified status
     */
    @Transactional(readOnly = true)
    public List<Policy> findPoliciesByStatus(PolicyStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return policyRepository.findByStatus(status);
    }
    
    /**
     * Finds policies by insurance type.
     * Clean Code: Simple filtering method.
     * 
     * @param insuranceType the insurance type
     * @return list of policies with the specified insurance type
     */
    @Transactional(readOnly = true)
    public List<Policy> findPoliciesByInsuranceType(InsuranceType insuranceType) {
        if (insuranceType == null) {
            throw new IllegalArgumentException("Insurance type cannot be null");
        }
        return policyRepository.findByInsuranceType(insuranceType);
    }
    
    /**
     * Searches policies by client name.
     * Clean Code: User-friendly search functionality.
     * 
     * @param clientName the client name to search for
     * @return list of policies for clients whose names contain the search term
     */
    @Transactional(readOnly = true)
    public List<Policy> searchPoliciesByClientName(String clientName) {
        if (clientName == null || clientName.trim().isEmpty()) {
            return List.of();
        }
        return policyRepository.findByClientNameContainingIgnoreCase(clientName.trim());
    }
    
    /**
     * Finds currently active policies.
     * Clean Code: Business-focused method for active policy management.
     * 
     * @return list of currently active policies
     */
    @Transactional(readOnly = true)
    public List<Policy> findCurrentlyActivePolicies() {
        return policyRepository.findCurrentlyActivePolicies(LocalDate.now());
    }
    
    /**
     * Finds policies expiring within specified days.
     * Clean Code: Business method for policy renewal management.
     * 
     * @param days the number of days to look ahead
     * @return list of policies expiring within the specified timeframe
     */
    @Transactional(readOnly = true)
    public List<Policy> findPoliciesExpiringWithinDays(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days must be non-negative");
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate expirationDate = currentDate.plusDays(days);
        return policyRepository.findPoliciesExpiringWithinDays(currentDate, expirationDate);
    }
    
    /**
     * Finds all clients for policy form dropdowns.
     * Clean Code: Simple method for form data population.
     * 
     * @return list of all clients
     */
    @Transactional(readOnly = true)
    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }
    
    /**
     * Finds all vehicles for policy form dropdowns.
     * Clean Code: Simple method for form data population.
     * 
     * @return list of all vehicles
     */
    @Transactional(readOnly = true)
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }
    
    /**
     * Generates a unique policy number.
     * Clean Code: Extracted utility method with clear purpose.
     * 
     * @param insuranceType the insurance type for prefix
     * @return unique policy number
     */
    private String generatePolicyNumber(InsuranceType insuranceType) {
        String prefix = insuranceType.name();
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String policyNumber = prefix + "-" + uniqueId;
        
        // Ensure uniqueness (very unlikely collision, but safety check)
        while (policyRepository.existsByPolicyNumber(policyNumber)) {
            uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            policyNumber = prefix + "-" + uniqueId;
        }
        
        return policyNumber;
    }
    
    /**
     * Validates policy creation parameters.
     * Clean Code: Extracted validation logic for reusability.
     */
    private void validatePolicyCreationParameters(Long clientId, Long vehicleId, 
                                                 InsuranceType insuranceType, 
                                                 LocalDate startDate, LocalDate endDate) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID is required");
        }
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        if (insuranceType == null) {
            throw new IllegalArgumentException("Insurance type is required");
        }
        
        validatePolicyDates(startDate, endDate);
    }
    
    /**
     * Validates policy dates.
     * Clean Code: Extracted validation logic for reusability.
     */
    private void validatePolicyDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }
    }
    
    /**
     * Validates policy dates for updates (more flexible than creation).
     * Clean Code: Separate validation for updates to allow existing policies.
     */
    private void validatePolicyDatesForUpdate(LocalDate startDate, LocalDate endDate, LocalDate issueDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        // Business rule: Policy cannot start before it was issued
        if (startDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Policy start date cannot be before the issue date (" + issueDate + ")");
        }
        // For updates, we only require that end date is not in the past
        if (endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }
    }
}
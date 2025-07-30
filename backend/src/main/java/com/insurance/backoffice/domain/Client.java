package com.insurance.backoffice.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a client in the insurance system.
 * Contains personal information required for policy issuance.
 */
@Entity
@Table(name = "clients")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;
    
    @Column(unique = true, nullable = false, length = 11)
    private String pesel;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;
    
    @Column(nullable = false, length = 255)
    private String email;
    
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;
    
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Policy> policies = new ArrayList<>();
    
    // Default constructor for JPA
    protected Client() {}
    
    // Private constructor for Builder pattern
    private Client(Builder builder) {
        this.id = builder.id;
        this.fullName = builder.fullName;
        this.pesel = builder.pesel;
        this.address = builder.address;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
    }
    
    /**
     * Adds a policy to this client's policy list.
     * Clean Code: Encapsulates the relationship management.
     */
    public void addPolicy(Policy policy) {
        policies.add(policy);
        policy.setClient(this);
    }
    
    /**
     * Removes a policy from this client's policy list.
     * Clean Code: Maintains bidirectional relationship integrity.
     */
    public void removePolicy(Policy policy) {
        policies.remove(policy);
        policy.setClient(null);
    }
    
    /**
     * Returns the number of active policies for this client.
     * Clean Code: Business logic encapsulated in domain object.
     */
    public long getActivePolicyCount() {
        return policies.stream()
                .filter(policy -> PolicyStatus.ACTIVE.equals(policy.getStatus()))
                .count();
    }
    
    /**
     * Checks if the client has any active policies.
     * Clean Code: Intention-revealing method name.
     */
    public boolean hasActivePolicies() {
        return getActivePolicyCount() > 0;
    }
    
    // Getters
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPesel() { return pesel; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public List<Policy> getPolicies() { return new ArrayList<>(policies); }
    
    // Setters for mutable fields
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPesel(String pesel) { this.pesel = pesel; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id) && Objects.equals(pesel, client.pesel);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, pesel);
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", pesel='" + pesel + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    
    /**
     * Builder pattern implementation for clean object creation.
     */
    public static class Builder {
        private Long id;
        private String fullName;
        private String pesel;
        private String address;
        private String email;
        private String phoneNumber;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }
        
        public Builder pesel(String pesel) {
            this.pesel = pesel;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }
        
        public Client build() {
            validateRequiredFields();
            return new Client(this);
        }
        
        private void validateRequiredFields() {
            if (fullName == null || fullName.trim().isEmpty()) {
                throw new IllegalArgumentException("Full name is required");
            }
            if (pesel == null || pesel.trim().isEmpty()) {
                throw new IllegalArgumentException("PESEL is required");
            }
            if (address == null || address.trim().isEmpty()) {
                throw new IllegalArgumentException("Address is required");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Phone number is required");
            }
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
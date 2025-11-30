package com.example.stockeasy.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User entity representing a user in the stock trading simulator.
 * Implements UserDetails for Spring Security integration.
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "username"),
           @UniqueConstraint(columnNames = "email")
       })
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @NotBlank
    @Size(max = 120)
    @Column(name = "password_hash", nullable = false)
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "cash_balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal cashBalance = new BigDecimal("100000.00");
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;
    
    @Column(name = "tour_completed", nullable = false)
    private Boolean tourCompleted = false;
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Portfolio> portfolios = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Watchlist> watchlists = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    
    // Constructors
    public User() {}
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles.add("USER");
    }
    
    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles.add("USER");
    }
    
    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        if (accountLockedUntil == null) {
            return true;
        }
        return accountLockedUntil.isBefore(LocalDateTime.now());
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isActive && isAccountNonLocked();
    }
    
    // Business methods
    public void addRole(String role) {
        this.roles.add(role.toUpperCase());
    }
    
    public void removeRole(String role) {
        this.roles.remove(role.toUpperCase());
    }
    
    public boolean hasRole(String role) {
        return this.roles.contains(role.toUpperCase());
    }
    
    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
        this.failedLoginAttempts = 0;
        this.accountLockedUntil = null;
    }
    
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountLockedUntil = LocalDateTime.now().plusMinutes(15);
        }
    }
    
    public void depositCash(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.cashBalance = this.cashBalance.add(amount);
        }
    }
    
    public boolean withdrawCash(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && 
            this.cashBalance.compareTo(amount) >= 0) {
            this.cashBalance = this.cashBalance.subtract(amount);
            return true;
        }
        return false;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public BigDecimal getCashBalance() { return cashBalance; }
    public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }
    
    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    
    public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }
    
    public Boolean isTourCompleted() { return tourCompleted != null ? tourCompleted : false; }
    public void setTourCompleted(Boolean tourCompleted) { this.tourCompleted = tourCompleted; }
    
    public Set<Portfolio> getPortfolios() { return portfolios; }
    public void setPortfolios(Set<Portfolio> portfolios) { this.portfolios = portfolios; }
    
    public Set<Watchlist> getWatchlists() { return watchlists; }
    public void setWatchlists(Set<Watchlist> watchlists) { this.watchlists = watchlists; }
    
    public Set<Transaction> getTransactions() { return transactions; }
    public void setTransactions(Set<Transaction> transactions) { this.transactions = transactions; }
    
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    
    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(getId(), user.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", cashBalance=" + cashBalance +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}

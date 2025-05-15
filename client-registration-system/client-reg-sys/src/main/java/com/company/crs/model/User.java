package com.company.crs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User model class to store authenticated user information
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String email;
    private String jwtToken;
    private List<String> roles;
    private String firstName;
    private String lastName;
    
    public User() {
        this.roles = new ArrayList<>();
    }
    
    public User(String username, String jwtToken) {
        this();
        this.username = username;
        this.jwtToken = jwtToken;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    
    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
    }
    
    public boolean hasRole(String role) {
        return this.roles != null && this.roles.contains(role);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }
    
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", roles=" + roles + '}';
    }
}

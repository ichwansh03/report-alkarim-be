package org.ichwan.dto;

import org.ichwan.util.UserRole;

import java.time.LocalDateTime;

public class UserResponse {
    private String name;
    private String clsroom;
    private String gender;
    private UserRole roles;
    private String regnumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getClsroom() { return clsroom; }
    public void setClsroom(String clsroom) { this.clsroom = clsroom; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public UserRole getRoles() { return roles; }
    public void setRoles(UserRole roles) { this.roles = roles; }

    public String getRegnumber() { return regnumber; }
    public void setRegnumber(String regnumber) { this.regnumber = regnumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
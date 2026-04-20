package org.ichwan.dto.response;

import org.ichwan.util.UserRole;

import java.time.LocalDateTime;

public class UserResponse {
    private String name;
    private ClassRoomResponse classRoom;
    private String gender;
    private UserRole roles;
    private String regnumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegnumber() { return regnumber; }
    public void setRegnumber(String regnumber) { this.regnumber = regnumber; }
}
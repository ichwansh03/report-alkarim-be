package org.ichwan.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String clsroom;
    private String gender;
    private String roles;
    private String password;

    public User() {
    }

    public User(Integer id, String name, String email, String clsroom, String gender, String roles, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.clsroom = clsroom;
        this.gender = gender;
        this.roles = roles;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClsroom() {
        return clsroom;
    }

    public void setClsroom(String clsroom) {
        this.clsroom = clsroom;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

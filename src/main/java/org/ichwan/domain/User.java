package org.ichwan.domain;

import jakarta.persistence.*;
import org.ichwan.util.UserRole;

@Entity
@Table(name = "users")
public class User extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String regnumber;
    private String clsroom;
    private String gender;
    @Enumerated(EnumType.STRING)
    private UserRole roles;
    private String password;

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegnumber() {
        return regnumber;
    }

    public void setRegnumber(String regnumber) {
        this.regnumber = regnumber;
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

    public UserRole getRoles() {
        return roles;
    }

    public void setRoles(UserRole roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", regnumber='" + regnumber + '\'' +
                ", clsroom='" + clsroom + '\'' +
                ", gender='" + gender + '\'' +
                ", roles='" + roles + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

package com.canteenx.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JavaBean representing an Administrator in the CanteenX system.
 * Complies with standard JavaBean specifications (serializable, no-arg constructor, getters/setters).
 */
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    private int adminId;
    private String name;
    private String email;
    private String password;
    private Timestamp createdAt;

    public Admin() {
    }

    public Admin(int adminId, String name, String email, String password) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Admin(int adminId, String name, String email, String password, Timestamp createdAt) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

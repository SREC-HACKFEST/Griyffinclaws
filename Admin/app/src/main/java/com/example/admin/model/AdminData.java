package com.example.admin.model;

public class AdminData {
    String id;
    String name;
    String belongs_to;
    String department;
    String email;
    String password;

    public AdminData() {
    }

    public AdminData(String id, String name, String belongs_to, String department, String email, String password) {
        this.id = id;
        this.name = name;
        this.belongs_to = belongs_to;
        this.department = department;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBelongs_to() {
        return belongs_to;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBelongs_to(String belongs_to) {
        this.belongs_to = belongs_to;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

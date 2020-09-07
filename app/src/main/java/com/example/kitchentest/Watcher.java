package com.example.kitchentest;

public class Watcher {
    String name, role, id, phone;



    public Watcher() {
    }
    public Watcher(String name, String id, String phone, String role) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.role = role;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}

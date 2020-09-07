package com.example.kitchentest;

public class PersonOnDuty {
    String name, role, id, phone, idGroup, status,statusAcceptWork, replaceDay;



    public PersonOnDuty() {
    }

    public PersonOnDuty(String name, String role, String id, String phone, String idGroup, String status, String statusAcceptWork, String replaceDay) {
        this.name = name;
        this.role = role;
        this.id = id;
        this.phone = phone;
        this.idGroup = idGroup;
        this.status = status;
        this.statusAcceptWork = statusAcceptWork;
        this.replaceDay = replaceDay;
    }

    public PersonOnDuty(String name, String role, String idGroup, String status) {
        this.name = name;
        this.role = role;
        this.idGroup = idGroup;
        this.status = status;
    }

    public String getStatusAcceptWork() {
        return statusAcceptWork;
    }
    public void setStatusAcceptWork(String statusAcceptWork) {
        this.statusAcceptWork = statusAcceptWork;
    }
    public String getReplaceDay() {
        return replaceDay;
    }
    public void setReplaceDay(String replaceDay) {
        this.replaceDay = replaceDay;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getIdGroup() {
        return idGroup;
    }
    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
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

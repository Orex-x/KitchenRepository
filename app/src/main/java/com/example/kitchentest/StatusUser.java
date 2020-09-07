package com.example.kitchentest;

public class StatusUser {

    String idUser ,status;

    public StatusUser() {
    }
    public StatusUser(String status, String idUser) {
        this.status = status;
        this.idUser = idUser;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getIdUser() {
        return idUser;
    }
    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}

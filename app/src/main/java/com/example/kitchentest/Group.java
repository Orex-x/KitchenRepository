package com.example.kitchentest;

public class Group {
    String idAdmin, idGroup, schedule, replaceDayGroup;

    public Group() {
    }


    public Group(String idAdmin, String idGroup) {
        this.idAdmin = idAdmin;
        this.idGroup = idGroup;
    }

    public Group(String idAdmin, String idGroup, String replaceDayGroup) {
        this.idAdmin = idAdmin;
        this.idGroup = idGroup;
        this.schedule = schedule;
        this.replaceDayGroup = replaceDayGroup;
    }



    public String getReplaceDayGroup() {
        return replaceDayGroup;
    }

    public void setReplaceDayGroup(String replaceDayGroup) {
        this.replaceDayGroup = replaceDayGroup;
    }

    public String getSchedule() {
        return schedule;
    }
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    public String getIdAdmin() {
        return idAdmin;
    }
    public void setIdAdmin(String idAdmin) {
        this.idAdmin = idAdmin;
    }
    public String getIdGroup() {
        return idGroup;
    }
    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }
}

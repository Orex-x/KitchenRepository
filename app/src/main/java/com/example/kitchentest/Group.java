package com.example.kitchentest;

public class Group {
    String idAdmin, idGroup, schedule, replaceDayGroup, duties, SDeadline,
            SquantityDayInSchedule;

    public Group() {
    }


    public Group(String idAdmin, String idGroup) {
        this.idAdmin = idAdmin;
        this.idGroup = idGroup;
    }

    public Group(String idAdmin, String idGroup, String replaceDayGroup, String SDeadline, String SquantityDayInSchedule) {
        this.idAdmin = idAdmin;
        this.idGroup = idGroup;
        this.replaceDayGroup = replaceDayGroup;
        this.SDeadline = SDeadline;
        this.SquantityDayInSchedule = SquantityDayInSchedule;
    }

    public Group(String idAdmin, String idGroup, String replaceDayGroup,
                 String duties, String SDeadline, String squantityDayInSchedule) {
        this.idAdmin = idAdmin;
        this.idGroup = idGroup;
        this.replaceDayGroup = replaceDayGroup;
        this.duties = duties;
        this.SDeadline = SDeadline;
        SquantityDayInSchedule = squantityDayInSchedule;
    }

    public String getDuties() {
        return duties;
    }

    public void setDuties(String duties) {
        this.duties = duties;
    }

    public String getSDeadline() {
        return SDeadline;
    }

    public void setSDeadline(String SDeadline) {
        this.SDeadline = SDeadline;
    }

    public String getSquantityDayInSchedule() {
        return SquantityDayInSchedule;
    }

    public void setSquantityDayInSchedule(String squantityDayInSchedule) {
        SquantityDayInSchedule = squantityDayInSchedule;
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

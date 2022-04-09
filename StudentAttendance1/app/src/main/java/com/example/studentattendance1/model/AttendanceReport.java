package com.example.studentattendance1.model;

import java.io.Serializable;

public class AttendanceReport implements Serializable {
    String room;
    String date;
    String time;
    String teacher;
    boolean present;

    public AttendanceReport() {
    }

    public AttendanceReport(String room, String date, String time, String teacher, boolean present) {
        this.room = room;
        this.date = date;
        this.time = time;
        this.teacher = teacher;
        this.present = present;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}

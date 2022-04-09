package com.example.teacherapp1.model;

import java.io.Serializable;
import java.util.List;

public class AttendanceReport implements Serializable {
    String key;
    String room;
    String date;
    String time;
    String teacher_id;
    String teacher;
    List<StudentModel> attendance;

    public AttendanceReport() {
    }

    public AttendanceReport(String room, String date, String time, String teacher_id, String teacher, List<StudentModel> attendance) {
        this.room = room;
        this.date = date;
        this.time = time;
        this.teacher_id = teacher_id;
        this.teacher = teacher;
        this.attendance = attendance;
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

    public List<StudentModel> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<StudentModel> attendance) {
        this.attendance = attendance;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

package com.example.admin.model;

import java.util.List;

public class CourseModel {
    String id;
    String name;
    String credit_hours;
    String semester;
    List<StudentModel> students;
    TeacherModel teacher;
    private int viewType;

    public CourseModel() {
    }

    public CourseModel(String id, String name, String semester, TeacherModel teacher, List<StudentModel> students) {
        this.id = id;
        this.name = name;
        this.semester = semester;
        this.teacher = teacher;
        this.students = students;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredit_hours() {
        return credit_hours;
    }

    public void setCredit_hours(String credit_hours) {
        this.credit_hours = credit_hours;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public TeacherModel getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherModel teacher) {
        this.teacher = teacher;
    }

    public List<StudentModel> getStudents() {
        return students;
    }

    public void setStudents(List<StudentModel> students) {
        this.students = students;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}

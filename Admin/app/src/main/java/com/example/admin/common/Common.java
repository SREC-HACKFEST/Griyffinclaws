package com.example.admin.common;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.admin.model.CourseModel;
import com.example.admin.model.StudentModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Common {
    private static final String TAG = "Common";
    public static final String COL_ADMINS = "Admins";
    public static final String KEY_ID = "admin_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_BELONGS_TO = "belongs_to";
    public static final String KEY_DEPT = "department";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String SHARED_NAME = "Admin_AMS";
    public static final String KEY_TEACHERS = "Teachers";
    public static final String KEY_STUDENTS = "Students";
    public static final String KEY_COURSES = "Courses";
    public static final int VIEWTYPE_SEMESTER = 0;
    public static final int VIEWTYPE_STUDENT = 1;

    public static boolean isEmpty(TextInputEditText editText, TextInputLayout layout) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            layout.setError("Field can't be Empty");
            return true;
        } else {
            layout.setError(null);
            return false;
        }
    }

    public static String getString(TextInputEditText editText){
        return editText.getText().toString();
    }

    public static List<StudentModel> sortList(List<StudentModel> studentsList) {
        Collections.sort(studentsList, new Comparator<StudentModel>() {
            @Override
            public int compare(StudentModel s1, StudentModel s2) {
                Log.d(TAG, "compare: s1: " + s1.getName() + ": " + s1.getSemester()
                                +" s2: " + s2.getName() + ": " + s2.getSemester());
                return Integer.compare(Integer.parseInt(s1.getSemester()), Integer.parseInt(s2.getSemester()));
            }
        });
        return studentsList;
    }

    public static List<CourseModel> sortCourseList(List<CourseModel> courseList) {
        Collections.sort(courseList, new Comparator<CourseModel>() {
            @Override
            public int compare(CourseModel c1, CourseModel c2) {
                return Integer.compare(Integer.parseInt(c1.getSemester()), Integer.parseInt(c2.getSemester()));
            }
        });
        return courseList;
    }

    public static List<StudentModel> addTimePeriod(List<StudentModel> studentsList) {

        int i, todaysCount = 0;

        List<StudentModel> newList = new ArrayList<>();

        StudentModel firstStudent = new StudentModel();
        firstStudent.setSemester(studentsList.get(0).getSemester());
        firstStudent.setViewType(VIEWTYPE_SEMESTER);

        newList.add(firstStudent);

        for (i = todaysCount; i < studentsList.size() - 1; i++) {
            StudentModel newSem = new StudentModel();

            String sem1 = studentsList.get(i).getSemester();
            String sem2 = studentsList.get(i + 1).getSemester();

            if (sem1.equals(sem2)) {
                studentsList.get(i).setViewType(VIEWTYPE_STUDENT);
                newList.add(studentsList.get(i));
            } else {
                studentsList.get(i).setViewType(VIEWTYPE_STUDENT);
                newList.add(studentsList.get(i));
                newSem.setSemester(studentsList.get(i+1).getSemester());
                newSem.setViewType(VIEWTYPE_SEMESTER);
                newList.add(newSem);
            }
        }

        studentsList.get(i).setViewType(VIEWTYPE_STUDENT);
        newList.add(studentsList.get(i));
        return newList;
    }

    public static List<CourseModel> addCourseTimePeriod(List<CourseModel> coursesList) {

        int i, todaysCount = 0;

        List<CourseModel> newList = new ArrayList<>();

        CourseModel firstCourse = new CourseModel();
        firstCourse.setSemester(coursesList.get(0).getSemester());
        firstCourse.setViewType(VIEWTYPE_SEMESTER);

        newList.add(firstCourse);

        for (i = todaysCount; i < coursesList.size() - 1; i++) {
            CourseModel newSem = new CourseModel();

            String sem1 = coursesList.get(i).getSemester();
            String sem2 = coursesList.get(i + 1).getSemester();

            Log.d(TAG, "addTimePeriod: " + coursesList.get(i).getName() + ": " + sem1 + ", " + coursesList.get(i+1).getName() + ": " + sem2);

            if (sem1.equals(sem2)) {
                coursesList.get(i).setViewType(VIEWTYPE_STUDENT);
                newList.add(coursesList.get(i));
            } else {
                coursesList.get(i).setViewType(VIEWTYPE_STUDENT);
                newList.add(coursesList.get(i));
                newSem.setSemester(coursesList.get(i+1).getSemester());
                newSem.setViewType(VIEWTYPE_SEMESTER);
                newList.add(newSem);
            }
        }

        coursesList.get(i).setViewType(VIEWTYPE_STUDENT);
        newList.add(coursesList.get(i));
        return newList;
    }
}

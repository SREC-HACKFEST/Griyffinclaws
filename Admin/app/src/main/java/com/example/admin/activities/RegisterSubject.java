package com.example.admin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.adapter.StudentListAdapter;
import com.example.admin.adapter.TeachersSpinnerAdapter;
import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityRegisterSubjectBinding;
import com.example.admin.model.CourseModel;
import com.example.admin.model.StudentModel;
import com.example.admin.model.TeacherModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterSubject extends AppCompatActivity {
    public static final int LIST_OF_STUDENTS = 55;

    private Context context = RegisterSubject.this;

    private ActivityRegisterSubjectBinding binding;

    private DatabaseReference mDatabaseReference, coursesRefrence;
    private CourseModel courseData;

    private String semester;
    private TeacherModel teacher;

    private ProgressDialog progressDialog;

    private ArrayList<TeacherModel> teachers;
    private List<StudentModel> selectedStudents;
    public static List<StudentModel> studentsList;

    Map<String, String> course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterSubjectBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        studentsList = new ArrayList<>();
        selectedStudents = new ArrayList<>();
        teachers = new ArrayList<>();
        course = new HashMap<>();
        initSpinner();
        getTeachersList();

        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Fetching Students...");
        progressDialog.show();

        new FetchStudents().execute();

        binding.selectStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentsList.class);
                intent.putExtra("activity", "register");
                startActivityForResult(intent, LIST_OF_STUDENTS);
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isEmpty(binding.id, binding.textInputId) || Common.isEmpty(binding.name, binding.textInputName)){
                } else if (TextUtils.isEmpty(semester)){
                    Toast.makeText(context, "Choose a Semester", Toast.LENGTH_SHORT).show();
                    return;
                } else if (teacher == null){
                    Toast.makeText(context, "Choose a Teacher", Toast.LENGTH_SHORT).show();
                    return;
                } else if (selectedStudents.size() < 5){
                    Toast.makeText(context, "Choose at least 5 students", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    progressDialog.setMessage("Registering a Course...");
                    progressDialog.show();

                    courseData = new CourseModel(Common.getString(binding.id),
                            Common.getString(binding.name),
                            semester,
                            teacher,
                            StudentListAdapter.selectedStudents);

                    if (!TextUtils.isEmpty(Common.getString(binding.creditHours))) {
                        courseData.setCredit_hours(Common.getString(binding.creditHours));
                    }

                    course.put("name", courseData.getName());
                    course.put("semester", courseData.getSemester());
                    course.put("credit_hours", courseData.getCredit_hours());

                    registerCourseNow();
                }
            }
        });
    }

    private void registerCourseNow() {
        coursesRefrence = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_COURSES)
                .child(Common.getString(binding.id));

        coursesRefrence.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    progressDialog.dismiss();
                    binding.textInputId.setError("Course with this id already exists");
                } else {
                    coursesRefrence.setValue(courseData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    setTeacherCourse();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTeacherCourse() {
        DatabaseReference teacherRef = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_TEACHERS)
                .child(teacher.getId())
                .child("courses")
                .child(courseData.getId());

        teacherRef.setValue(course)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setStudentsCourse();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setStudentsCourse() {
        DatabaseReference studentRef = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_STUDENTS);

        course.put("teacher", teacher.getName());

        for (StudentModel student : StudentListAdapter.selectedStudents){
            studentRef.child(student.getSemester())
                    .child(student.getId())
                    .child("courses")
                    .child(courseData.getId())
                    .setValue(course)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        progressDialog.dismiss();
        Toast.makeText(context, "Course Registered Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == LIST_OF_STUDENTS){
                selectedStudents = StudentListAdapter.selectedStudents;
                updateStudentsList();
            }
        }
    }

    private void updateStudentsList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        selectedStudents = Common.sortList(selectedStudents);
        selectedStudents = Common.addTimePeriod(selectedStudents);

        binding.studentLists.setLayoutManager(linearLayoutManager);
        StudentListAdapter adapter = new StudentListAdapter(context, selectedStudents, true);
        binding.studentLists.setAdapter(adapter);
    }

    private void getTeachersList() {

        teachers.add(new TeacherModel("Choose Teacher", null, null, "education", "qualification", "experience", null));

        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_TEACHERS);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    teachers.add(snapshot.getValue(TeacherModel.class));
                }

                initTeacherSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class FetchStudents extends AsyncTask<Void, Void, Void>{

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_STUDENTS);

        @Override
        protected Void doInBackground(Void... voids) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                studentsList.add(dataSnapshot2.getValue(StudentModel.class));
                            }

                        }

                    } else {
                        Toast.makeText(context, "No Student exits.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    private void initTeacherSpinner() {
        TeachersSpinnerAdapter adapter = new TeachersSpinnerAdapter(context, teachers);
        binding.selectTeacher.setAdapter(adapter);

        binding.selectTeacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                teacher = (TeacherModel) parent.getItemAtPosition(position);
                if (teacher.getId().equals("Choose Teacher")){
                    teacher = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinner() {
        ArrayAdapter<String> spinnerArray = new ArrayAdapter<String>(context, R.layout.spinner_item, getResources().getStringArray(R.array.semesters)) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(getResources().getColor(R.color.default_color));
                } else {
                    tv.setTextColor(getResources().getColor(R.color.black));
                }
                return view;
            }
        };

        spinnerArray.setDropDownViewResource(R.layout.spinner_item);
        binding.semester.setAdapter(spinnerArray);

        binding.semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                semester = (String) adapterView.getItemAtPosition(i);
                if (semester.equals("Choose here")) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(getResources().getColor(R.color.default_color));
                    semester = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

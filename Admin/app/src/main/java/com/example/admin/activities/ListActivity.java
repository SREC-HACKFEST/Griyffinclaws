package com.example.admin.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.adapter.CourseListAdapter;
import com.example.admin.adapter.StudentListAdapter;
import com.example.admin.adapter.TeacherListAdapter;
import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityListBinding;
import com.example.admin.model.CourseModel;
import com.example.admin.model.StudentModel;
import com.example.admin.model.TeacherModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = "ListActivity";
    private Context context = ListActivity.this;

    private ActivityListBinding binding;

    String type;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        progressDialog = new ProgressDialog(ListActivity.this, R.style.AppCompatAlertDialogStyle);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        setToolbarTitle();

        progressDialog.setMessage("Fetching " + type + "...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(type);

        if (type.equals(Common.KEY_STUDENTS)) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    List<StudentModel> studentsList = new ArrayList<>();

                    if (dataSnapshot.exists()) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                studentsList.add(dataSnapshot2.getValue(StudentModel.class));
                            }

                        }

                        updateStudentsList(studentsList);

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ListActivity.this, "No " + type + " exits.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(ListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (type.equals(Common.KEY_TEACHERS)) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    List<TeacherModel> teachersList = new ArrayList<>();

                    if (dataSnapshot.exists()) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            teachersList.add(dataSnapshot1.getValue(TeacherModel.class));
                        }

                        updateTeachersList(teachersList);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ListActivity.this, "No " + type + " exits.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(ListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    List<CourseModel> coursesList = new ArrayList<>();

                    if (dataSnapshot.exists()) {



                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

//                            String id = dataSnapshot1.getKey();
//                            String name = dataSnapshot1.child("name").getValue(String.class);
//                            String credit_hours = dataSnapshot1.child("credit_hours").getValue(String.class);
//                            String semester = dataSnapshot1.child("semester").getValue(String.class);



                            Log.d(TAG, "onDataChange: " + dataSnapshot1.getKey());

                            CourseModel course = dataSnapshot1.getValue(CourseModel.class);



                            Log.d(TAG, "onDataChange: " + course.getName() + " size: " + course.getStudents().size());

                            coursesList.add(course);

                        }
                        
                        updateCoursesList(coursesList);
                        
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ListActivity.this, "No " + type + " exits.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(ListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void updateCoursesList(List<CourseModel> coursesList) {
        progressDialog.dismiss();

        coursesList = Common.sortCourseList(coursesList);
        coursesList = Common.addCourseTimePeriod(coursesList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.list.setLayoutManager(linearLayoutManager);
        CourseListAdapter adapter = new CourseListAdapter(ListActivity.this, coursesList);
        binding.list.setAdapter(adapter);
    }

    private void updateStudentsList(List<StudentModel> studentsList) {
        progressDialog.dismiss();

        studentsList = Common.sortList(studentsList);
        studentsList = Common.addTimePeriod(studentsList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.list.setLayoutManager(linearLayoutManager);
        StudentListAdapter adapter = new StudentListAdapter(context, studentsList, true);
        binding.list.setAdapter(adapter);
    }

    private void updateTeachersList(List<TeacherModel> teachersList) {
        progressDialog.dismiss();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListActivity.this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(binding.list.getContext(), linearLayoutManager.getOrientation());
        binding.list.setLayoutManager(linearLayoutManager);
        binding.list.addItemDecoration(itemDecoration);
        TeacherListAdapter adapter = new TeacherListAdapter(ListActivity.this, teachersList);
        binding.list.setAdapter(adapter);
    }

    private void setToolbarTitle() {
        if (type.equals(Common.KEY_TEACHERS))
            binding.toolbar.setTitle("Choose a Teacher");
        else if (type.equals(Common.KEY_STUDENTS))
            binding.toolbar.setTitle("Choose a Student");
        else
            binding.toolbar.setTitle("Choose a Subject");

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.admin.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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
import com.example.admin.databinding.ActivityModifySubjectBinding;
import com.example.admin.model.CourseModel;
import com.example.admin.model.StudentModel;
import com.example.admin.model.TeacherModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class ModifySubject extends AppCompatActivity {
    private static final String TAG = "ModifySubject";
    private Context context = ModifySubject.this;
    public static final int LIST_OF_STUDENTS = 56;

    private ActivityModifySubjectBinding binding;

    private CourseModel courseData, updatedCourseData;

    public static String id;
    private String semester;

    private DatabaseReference mDatabaseReference;

    private ProgressDialog progressDialog;

    private ArrayList<TeacherModel> teachers;
    private TeacherModel teacher, selectedTeacher;
    private List<StudentModel> selectedStudents;
    public static List<StudentModel> studentsList;

    Map<String, String> course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifySubjectBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        studentsList = new ArrayList<>();
        selectedStudents = new ArrayList<>();
        courseData = new CourseModel();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        course = new HashMap<>();

        new FetchStudents().execute();

        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Fetching Course Data...");
        progressDialog.show();

        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_COURSES)
                .child(id);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    courseData.setId(dataSnapshot.child("id").getValue(String.class));
                    courseData.setName(dataSnapshot.child("name").getValue(String.class));
                    courseData.setSemester(dataSnapshot.child("semester").getValue(String.class));
                    courseData.setCredit_hours(dataSnapshot.child("credit_hours").getValue(String.class));
                    courseData.setTeacher(dataSnapshot.child("teacher").getValue(TeacherModel.class));

                    List<StudentModel> studentModels = new ArrayList<>();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("students").getChildren()){
                        studentModels.add(dataSnapshot1.getValue(StudentModel.class));
                    }

                    courseData.setStudents(studentModels);

                    Log.d(TAG, "onDataChange: " + dataSnapshot.child("students").getChildrenCount());
                    Log.d(TAG, "onDataChange: " + studentModels.size());
                    Log.d(TAG, "onDataChange: " + courseData.getStudents().size());

                    updateUI(courseData);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "No " + id + " exits.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.selectStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!studentsList.isEmpty()) {
                    Intent intent = new Intent(context, StudentsList.class);
                    intent.putExtra("activity", "modify");
                    startActivityForResult(intent, LIST_OF_STUDENTS);
                } else {
                    Toast.makeText(context, "Please Wait, Fetching Students List...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isEmpty(binding.id, binding.textInputId) || Common.isEmpty(binding.name, binding.textInputName)) {
                } else if (TextUtils.isEmpty(semester)) {
                    Toast.makeText(context, "Choose a Semester", Toast.LENGTH_SHORT).show();
                } else if (selectedTeacher == null) {
                    Toast.makeText(context, "Choose a Teacher", Toast.LENGTH_SHORT).show();
                } else if (selectedStudents.size() < 5) {
                    Toast.makeText(context, "Choose at least 5 students", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Updating a Course...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    updatedCourseData = new CourseModel(Common.getString(binding.id),
                            Common.getString(binding.name),
                            semester,
                            selectedTeacher,
                            StudentListAdapter.selectedStudents);

                    if (!TextUtils.isEmpty(Common.getString(binding.creditHours))) {
                        updatedCourseData.setCredit_hours(Common.getString(binding.creditHours));
                    }

                    course.put("name", updatedCourseData.getName());
                    course.put("semester", updatedCourseData.getSemester());
                    course.put("credit_hours", updatedCourseData.getCredit_hours());

                    registerCourseNow();
                }
            }
        });
    }

    private void registerCourseNow() {
        if (teacher != null) {
            if (!selectedTeacher.getId().equals(teacher.getId())) {
                DatabaseReference teacherRef = FirebaseDatabase.getInstance()
                        .getReference(MainActivity.mAdminData.getBelongs_to())
                        .child(MainActivity.mAdminData.getDepartment())
                        .child(Common.KEY_TEACHERS)
                        .child(teacher.getId())
                        .child("courses")
                        .child(courseData.getId());

                teacherRef.removeValue();
            }
        }

        final DatabaseReference coursesRefrence = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_COURSES)
                .child(Common.getString(binding.id));

        coursesRefrence.setValue(updatedCourseData)
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

    private void setTeacherCourse() {
        DatabaseReference teacherRef = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_TEACHERS)
                .child(selectedTeacher.getId())
                .child("courses")
                .child(updatedCourseData.getId());

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

        course.put("teacher", selectedTeacher.getName());

        for (StudentModel student : StudentListAdapter.selectedStudents) {
            studentRef.child(student.getSemester())
                    .child(student.getId())
                    .child("courses")
                    .child(updatedCourseData.getId())
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
        Toast.makeText(context, "Course Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateUI(CourseModel courseData) {
        binding.id.setText(courseData.getId());
        binding.name.setText(courseData.getName());

        if (!TextUtils.isEmpty(courseData.getCredit_hours()))
            binding.creditHours.setText(courseData.getCredit_hours());

        semester = courseData.getSemester();
        teacher = courseData.getTeacher();
        selectedTeacher = teacher;
        selectedStudents = courseData.getStudents();

        Log.d(TAG, "updateUI: " + selectedStudents.size());

        for (StudentModel studentModel: selectedStudents){
            Log.d(TAG, "compare: student: " + studentModel.getName() + ": " + studentModel.getSemester());
        }

        StudentListAdapter.selectedStudents.clear();
        StudentListAdapter.selectedStudents = selectedStudents;

        setupSpinner();
        updateStudentsList();

        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LIST_OF_STUDENTS) {
                selectedStudents.clear();
                selectedStudents = StudentListAdapter.selectedStudents;
                Log.d(TAG, "onActivityResult: size: " + selectedStudents.size());
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

    private void setupSpinner() {
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

        int pos = spinnerArray.getPosition(semester);
        binding.semester.setSelection(pos);

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

        setupTeacherSpinner();
    }

    private void setupTeacherSpinner() {
        teachers = new ArrayList<>();

        teachers.add(new TeacherModel("Choose Teacher", null, null, "education", "qualification", "experience", null));

        DatabaseReference teacherReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_TEACHERS);

        teacherReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
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

    private void initTeacherSpinner() {
        TeachersSpinnerAdapter adapter = new TeachersSpinnerAdapter(context, teachers);
        binding.selectTeacher.setAdapter(adapter);

        if (teacher != null) {
            int pos = adapter.getPosition(teacher);
            binding.selectTeacher.setSelection(pos);
        }

        binding.selectTeacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTeacher = (TeacherModel) parent.getItemAtPosition(position);

                if (selectedTeacher.getId().equals("Choose Teacher")) {
                    selectedTeacher = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure to Delete this Subject?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteSubject();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteSubject() {
        progressDialog.setMessage("Deleting Course...");
        progressDialog.show();

        Log.d(TAG, "deleteSubject: " + StudentListAdapter.selectedStudents.size());

        for (StudentModel student: StudentListAdapter.selectedStudents){

            Log.d(TAG, "deleteSubject: student: " + student.getSemester() + " id: " + student.getId());

            DatabaseReference studentReference = FirebaseDatabase.getInstance()
                    .getReference(MainActivity.mAdminData.getBelongs_to())
                    .child(MainActivity.mAdminData.getDepartment())
                    .child(Common.KEY_STUDENTS)
                    .child(student.getSemester())
                    .child(student.getId())
                    .child("courses")
                    .child(id);

            studentReference.removeValue();
        }

        if (teacher != null) {
            DatabaseReference teacherRef = FirebaseDatabase.getInstance()
                    .getReference(MainActivity.mAdminData.getBelongs_to())
                    .child(MainActivity.mAdminData.getDepartment())
                    .child(Common.KEY_TEACHERS)
                    .child(teacher.getId())
                    .child("courses")
                    .child(courseData.getId());

            teacherRef.removeValue();
        }

        mDatabaseReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Course Deleted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
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

    private class FetchStudents extends AsyncTask<Void, Void, Void> {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.delete) {
            showDialogBox();
        }
        return super.onOptionsItemSelected(item);
    }
}

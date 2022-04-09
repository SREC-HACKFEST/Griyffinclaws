package com.example.teacherapp1.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.teacherapp1.MainActivity;
import com.example.teacherapp1.R;
import com.example.teacherapp1.adapters.GenerateReportsAdapter;
import com.example.teacherapp1.databinding.ActivityGenerateReportsBinding;
import com.example.teacherapp1.model.AttendanceReport;
import com.example.teacherapp1.model.StudentModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

public class GenerateReports extends AppCompatActivity {
    private static final String TAG = "GenerateReports";
    private Context context = GenerateReports.this;

    private ActivityGenerateReportsBinding binding;

    private List<StudentModel> students;

    private GenerateReportsAdapter adapter;

    String courseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenerateReportsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        students = (List<StudentModel>) intent.getSerializableExtra("student_list");
        courseID = intent.getStringExtra("id");

        adapter = new GenerateReportsAdapter(context, students);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.listOfStudents.setLayoutManager(linearLayoutManager);
        binding.listOfStudents.setAdapter(adapter);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();

        binding.date.setText(dateFormat.format(calendar.getTime()));
        binding.time.setText(timeFormat.format(calendar.getTime()));

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.room.getText().toString())){
                    binding.textInputRoom.setError("Please Enter Room!");
                } else {

                    String date = binding.date.getText().toString();
                    String time = binding.time.getText().toString();

                    ProgressDialog progressDialog = new ProgressDialog(context, R.style.Base_Theme_AppCompat_Dialog);
                    progressDialog.setMessage("Submitting Attendance Report");
                    progressDialog.show();

                    AttendanceReport report = new AttendanceReport(binding.room.getText().toString(),
                            date,
                            time,
                            MainActivity.mTeacherModel.getId(),
                            MainActivity.mTeacherModel.getName(),
                            students);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.uni)
                            .child(MainActivity.dept)
                            .child("Attendance")
                            .child(courseID)
                            .child(date + " " + time);

                    databaseReference.setValue(report)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Attendance Saved Successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}
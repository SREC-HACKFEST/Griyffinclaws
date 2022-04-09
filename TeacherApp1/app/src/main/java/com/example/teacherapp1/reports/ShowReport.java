package com.example.teacherapp1.reports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.teacherapp1.R;
import com.example.teacherapp1.adapters.DatesAdapter;
import com.example.teacherapp1.adapters.GenerateReportsAdapter;
import com.example.teacherapp1.adapters.ShowReportsAdapter;
import com.example.teacherapp1.databinding.ActivityShowReportBinding;
import com.example.teacherapp1.model.AttendanceReport;
import com.example.teacherapp1.model.StudentModel;

import java.util.List;

public class ShowReport extends AppCompatActivity {
    private static final String TAG = "ShowReport";
    private Context context = ShowReport.this;

    private ActivityShowReportBinding binding;

    private List<StudentModel> studentsPresent;
    private ShowReportsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowReportBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        AttendanceReport report = (AttendanceReport) intent.getSerializableExtra("attendance_list");

        studentsPresent = report.getAttendance();

        adapter = new ShowReportsAdapter(context, studentsPresent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.listOfStudents.setLayoutManager(linearLayoutManager);
        binding.listOfStudents.setAdapter(adapter);

        binding.date.setText(report.getDate());
        binding.time.setText(report.getTime());
        binding.room.setText(report.getRoom());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.studentattendance1.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentattendance1.MainActivity;
import com.example.studentattendance1.R;
import com.example.studentattendance1.adapters.ReportAdapter;
import com.example.studentattendance1.databinding.ActivityCheckReportsBinding;
import com.example.studentattendance1.model.AttendanceReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckReports extends AppCompatActivity {

    private static final String TAG = "CheckReports";
    private Context context = CheckReports.this;

    private ActivityCheckReportsBinding binding;

    private DatabaseReference mDatabaseReference;

    private String courseID;

    private ProgressDialog progressDialog;

    private  List<AttendanceReport> attendanceReport;
    private ReportAdapter adapter;

    private int taken, absent, total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckReportsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        courseID = intent.getStringExtra("id");

        taken = absent = total = 0;

        attendanceReport = new ArrayList<>();
        adapter = new ReportAdapter(context, attendanceReport);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.reports.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, linearLayoutManager.getOrientation());
        binding.reports.addItemDecoration(dividerItemDecoration);
        binding.reports.setAdapter(adapter);

        progressDialog = new ProgressDialog(context, R.style.Theme_AppCompat_Dialog);
        progressDialog.setMessage("Fetching Attendance Report...");
        progressDialog.show();

        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.uni)
                .child(MainActivity.dept)
                .child("Attendance")
                .child(courseID);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    progressDialog.dismiss();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        total++;

                        String room = dataSnapshot.child("room").getValue(String.class);
                        String date = dataSnapshot.child("date").getValue(String.class);
                        String time = dataSnapshot.child("time").getValue(String.class);
                        String teacher = dataSnapshot.child("teacher").getValue(String.class);

                        for (DataSnapshot isPresent : dataSnapshot.child("attendance").getChildren()){

                            if (isPresent.child("id").getValue(String.class).equals(MainActivity.ID)){

                                boolean present = isPresent.child("present").getValue(boolean.class);

                                if (present){
                                    taken++;
                                } else {
                                    absent++;
                                }

                                AttendanceReport report = new AttendanceReport(room, date, time, teacher, present);

                                attendanceReport.add(report);
                            }
                        }
                    }

                    float percentage = ((float)taken/(float) total)*100;

                    binding.percentage.setText((int)percentage+"%");
                    binding.taken.setText(taken + " out of " + total + " classes are taken");
                    adapter.notifyDataSetChanged();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "No Attendance Marked for this subject", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
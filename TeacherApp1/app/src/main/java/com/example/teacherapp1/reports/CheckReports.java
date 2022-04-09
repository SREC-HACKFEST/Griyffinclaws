package com.example.teacherapp1.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.teacherapp1.MainActivity;
import com.example.teacherapp1.R;
import com.example.teacherapp1.adapters.ChooseSubjectAdapter;
import com.example.teacherapp1.adapters.DatesAdapter;
import com.example.teacherapp1.common.Common;
import com.example.teacherapp1.databinding.ActivityCheckReportsBinding;
import com.example.teacherapp1.model.AttendanceReport;
import com.example.teacherapp1.model.StudentModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CheckReports extends AppCompatActivity {
    private static final String TAG = "CheckReports";
    private Context context = CheckReports.this;

    private ActivityCheckReportsBinding binding;

    String id;

    private DatabaseReference mDatabaseReference;
    private List<AttendanceReport> attendances;

    private DatesAdapter adapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckReportsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.setTitle("Select Date");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        attendances = new ArrayList<>();
        adapter = new DatesAdapter(context, attendances);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, linearLayoutManager.getOrientation());
        binding.dates.setLayoutManager(linearLayoutManager);
        binding.dates.addItemDecoration(itemDecoration);
        binding.dates.setAdapter(adapter);


        progressDialog = new ProgressDialog(context, R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setMessage("Fetching Attendances...");
        progressDialog.show();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.uni)
                .child(MainActivity.dept)
                .child("Attendance")
                .child(id);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressDialog.dismiss();

                if (dataSnapshot.exists()){

                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        String key = dataSnapshot1.getKey();
                        AttendanceReport report = dataSnapshot1.getValue(AttendanceReport.class);
                        report.setKey(key);

                        attendances.add(report);
                    }

                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(context, "No Attendance Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.teacherapp1.reports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.teacherapp1.common.Common;
import com.example.teacherapp1.databinding.ActivityChooseSubjectBinding;
import com.example.teacherapp1.model.CourseItem;

import java.util.ArrayList;
import java.util.List;

public class ChooseSubject extends AppCompatActivity {
    private static final String TAG = "ChooseSubject";
    private Context context = ChooseSubject.this;

    private ActivityChooseSubjectBinding binding;

    private List<CourseItem> courses;
    private ChooseSubjectAdapter adapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseSubjectBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Log.d(TAG, "onCreate: ");

        courses = new ArrayList<>();
        adapter = new ChooseSubjectAdapter(courses, context);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.courses.setLayoutManager(linearLayoutManager);
        binding.courses.setAdapter(adapter);

        binding.toolbar.setTitle("Choose a Course");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(context, R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setMessage("Fetching Courses...");
        progressDialog.show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.uni)
                .child(MainActivity.dept)
                .child(Common.KEY_TEACHERS)
                .child(MainActivity.ID)
                .child("courses");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    progressDialog.dismiss();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        CourseItem item = snapshot.getValue(CourseItem.class);

                        Log.d(TAG, "onDataChange: " + item.getCredit_hours());

                        item.setId(snapshot.getKey());
                        courses.add(item);

                    }

                    adapter.notifyDataSetChanged();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "No Course Assigned", Toast.LENGTH_SHORT).show();
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
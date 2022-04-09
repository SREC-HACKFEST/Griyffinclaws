package com.example.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.adapter.StudentListAdapter;
import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityStudentsListBinding;
import com.example.admin.model.StudentModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StudentsList extends AppCompatActivity {
    private static final String TAG = "StudentsList";
    private Context context = StudentsList.this;

    private ActivityStudentsListBinding binding;

    private List<StudentModel> studentsList;

    StudentListAdapter adapter;

    public static String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentsListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.toolbar.setTitle("Select Students");
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        studentsList = new ArrayList<>();

        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        Log.d(TAG, "onCreate: " + activity);

        studentsList.clear();

        if (activity.equals("register")){
            studentsList = RegisterSubject.studentsList;
        } else {
            studentsList = ModifySubject.studentsList;
        }

        updateRecyclerView();

        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void updateRecyclerView() {

        Log.d(TAG, "updateRecyclerView: " + studentsList.size());

        if (studentsList.size() > 0){
            studentsList = Common.sortList(studentsList);
            studentsList = Common.addTimePeriod(studentsList);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.studentsList.setLayoutManager(linearLayoutManager);
        adapter = new StudentListAdapter(context, studentsList, false);
        binding.studentsList.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

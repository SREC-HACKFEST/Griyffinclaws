package com.example.teacherapp1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.teacherapp1.attendance.MarkAttendance;
import com.example.teacherapp1.common.Common;
import com.example.teacherapp1.databinding.ActivityMainBinding;
import com.example.teacherapp1.model.TeacherModel;
import com.example.teacherapp1.reports.CheckReports;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.teacherapp1.reports.ChooseSubject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;

    private SharedPreferences preferences;
    public static String ID, uni, dept;
    public static TeacherModel mTeacherModel;

    private DatabaseReference mDatabaseReference;

    private ActivityMainBinding binding;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        preferences = getSharedPreferences(Common.SHARED_NAME, MODE_PRIVATE);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (preferences.contains("teacher_id")) {
            ID = preferences.getString("teacher_id", "asd");
            uni = preferences.getString("uni", "uni");
            dept = preferences.getString("dept", "dept");
            getTeacherData(ID, uni, dept);
        } else {
            Toast.makeText(context, "Login to Continue", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        binding.markAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MarkAttendance.class);
                startActivity(intent);
            }
        });

        binding.checkReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                Intent intent = new Intent(context, ChooseSubject.class);
                startActivity(intent);
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().remove("teacher_id").apply();
                preferences.edit().remove("uni").apply();
                preferences.edit().remove("dept").apply();

                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void getTeacherData(String ID, String uni, String dept) {
        progressDialog = new ProgressDialog(context, R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setMessage("Fetching Teacher Data...");
        progressDialog.show();

        mDatabaseReference.child(uni)
                .child(dept)
                .child(Common.KEY_TEACHERS)
                .child(ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            mTeacherModel = dataSnapshot.getValue(TeacherModel.class);
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(context, "Login to Continue", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

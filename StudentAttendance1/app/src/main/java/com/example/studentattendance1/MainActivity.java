package com.example.studentattendance1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.studentattendance1.Common.Common;
import com.example.studentattendance1.activities.ChooseSubject;
import com.example.studentattendance1.databinding.ActivityMainBinding;
import com.example.studentattendance1.model.StudentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;

    private SharedPreferences preferences;
    public static String ID, uni, dept, sem;
    public static StudentModel mStudentModel;

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

        if (preferences.contains("student_id")) {
            ID = preferences.getString("student_id", "asd");
            uni = preferences.getString("uni", "uni");
            dept = preferences.getString("dept", "dept");
            sem = preferences.getString("sem", "sem");
            getStudentData(ID, uni, dept, sem);
        } else {
            Toast.makeText(context, "Login to Continue", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

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

                preferences.edit().remove("student_id").apply();
                preferences.edit().remove("uni").apply();
                preferences.edit().remove("dept").apply();
                preferences.edit().remove("sem").apply();

                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void getStudentData(String id, String uni, String dept, String sem) {
        progressDialog = new ProgressDialog(context, R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setMessage("Fetching Student Data...");
        progressDialog.show();

        mDatabaseReference.child(uni)
                .child(dept)
                .child(Common.KEY_STUDENTS)
                .child(sem)
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            mStudentModel = dataSnapshot.getValue(StudentModel.class);
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
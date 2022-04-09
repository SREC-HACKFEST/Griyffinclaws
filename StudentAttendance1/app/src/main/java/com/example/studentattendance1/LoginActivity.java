package com.example.studentattendance1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentattendance1.Common.Common;
import com.example.studentattendance1.databinding.ActivityLoginBinding;
import com.example.studentattendance1.model.StudentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ActivityLoginBinding binding;

    private Context context = LoginActivity.this;

    private DatabaseReference mDatabaseReference;
    private ProgressDialog progressDialog;

    private String university, department;

    private StudentModel mStudentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        initSpinner();

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ID = binding.adminId.getText().toString();
                final String Pass = binding.adminPass.getText().toString();

                if (TextUtils.isEmpty(ID) || TextUtils.isEmpty(Pass) || TextUtils.isEmpty(university) || TextUtils.isEmpty(department)) {
                    Toast.makeText(context, "All Fields required", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(context, R.style.Base_Theme_AppCompat_Dialog_Alert);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    mDatabaseReference.child(university)
                            .child(department)
                            .child(Common.KEY_STUDENTS)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {

                                        for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                                            for (DataSnapshot dataSnapshot2: dataSnapshot1.getChildren()){

                                                if (dataSnapshot2.getKey().equals(ID)){

                                                    mStudentModel = dataSnapshot2.getValue(StudentModel.class);
                                                    Log.d(TAG, "onDataChange: database: " + dataSnapshot2.child("password").getValue(String.class));
                                                    Log.d(TAG, "onDataChange: " + mStudentModel.getPassword());

                                                    if (mStudentModel.getPassword().equals(Pass)) {

                                                        progressDialog.dismiss();
                                                        Toast.makeText(context, "Welcome, " + mStudentModel.getName(), Toast.LENGTH_SHORT).show();

                                                        SharedPreferences.Editor editor = getSharedPreferences(Common.SHARED_NAME, MODE_PRIVATE).edit();

                                                        editor.putString("student_id", ID);
                                                        editor.putString("uni", university);
                                                        editor.putString("dept", department);
                                                        editor.putString("sem", mStudentModel.getSemester());

                                                        editor.apply();

                                                        startMainActivity();

                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(context, "The Password does not match", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }
                                        }

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "No Teacher Exists", Toast.LENGTH_SHORT).show();
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
        });

    }

    private void startMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void initSpinner() {
        ArrayAdapter<String> UniversitiesArray = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.universities)) {
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

        UniversitiesArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.chooseUni.setAdapter(UniversitiesArray);

        binding.chooseUni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                university = (String) parent.getItemAtPosition(position);
                if (university.equals("Choose University")) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(getResources().getColor(R.color.default_color));
                    university = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> DepartmentsArray = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.departments)) {
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

        DepartmentsArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.chooseDept.setAdapter(DepartmentsArray);

        binding.chooseDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department = (String) parent.getItemAtPosition(position);
                if (department.equals("Choose Department")) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(getResources().getColor(R.color.default_color));
                    department = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
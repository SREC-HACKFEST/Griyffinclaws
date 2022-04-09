package com.example.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.admin.common.Common;
import com.example.admin.model.AdminData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Context context = LoginActivity.this;

    private EditText id, pass;
    private Button login;

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private String university, department;
    private Spinner uni, dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Common.COL_ADMINS);
        mAuth = FirebaseAuth.getInstance();

        id = findViewById(R.id.admin_id);
        pass = findViewById(R.id.admin_pass);
        login = findViewById(R.id.login);
        uni = findViewById(R.id.choose_uni);
        dept = findViewById(R.id.choose_dept);

        initSpinner();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String ID = id.getText().toString();
                final String Pass = pass.getText().toString();

                if (TextUtils.isEmpty(ID) || TextUtils.isEmpty(Pass) || TextUtils.isEmpty(university) || TextUtils.isEmpty(department)) {
                    Toast.makeText(context, "All Fields required", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    mDatabaseReference.child(university)
                            .child(department)
                            .child(ID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        final AdminData adminData = dataSnapshot.getValue(AdminData.class);
                                        mAuth.signInWithEmailAndPassword(adminData.getEmail(), Pass)
                                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                    @Override
                                                    public void onSuccess(AuthResult authResult) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(context, "Welcome, " + adminData.getName(), Toast.LENGTH_SHORT).show();

                                                        SharedPreferences.Editor editor = getSharedPreferences(Common.SHARED_NAME, MODE_PRIVATE).edit();

                                                        editor.putString("admin_id", ID);
                                                        editor.putString("uni", university);
                                                        editor.putString("dept", department);

                                                        editor.apply();

                                                        startMainActivity();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "No Admin Registered", Toast.LENGTH_SHORT).show();
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
        uni.setAdapter(UniversitiesArray);

        uni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                university = (String) parent.getItemAtPosition(position);
                if (university.equals("Choose University")){
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
        dept.setAdapter(DepartmentsArray);

        dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                department = (String) parent.getItemAtPosition(position);
                if (department.equals("Choose Department")){
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

    private void startMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

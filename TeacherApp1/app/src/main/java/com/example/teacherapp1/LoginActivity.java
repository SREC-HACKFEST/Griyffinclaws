package com.example.teacherapp1;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.teacherapp1.common.Common;
import com.example.teacherapp1.databinding.ActivityLoginBinding;
import com.example.teacherapp1.model.TeacherModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private Context context = LoginActivity.this;

    private DatabaseReference mDatabaseReference;
    private ProgressDialog progressDialog;

    private String university, department;

    private TeacherModel mTeacherModel;

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
                    progressDialog = new ProgressDialog(context, R.style.Base_Theme_AppCompat_Dialog);
                    progressDialog.setMessage("Logging in...");
                    progressDialog.show();

                    mDatabaseReference.child(university)
                            .child(department)
                            .child(Common.KEY_TEACHERS)
                            .child(ID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        mTeacherModel = dataSnapshot.getValue(TeacherModel.class);

                                        if (mTeacherModel.getPassword().equals(Pass)){

                                            progressDialog.dismiss();
                                            Toast.makeText(context, "Welcome, " + mTeacherModel.getName(), Toast.LENGTH_SHORT).show();

                                            SharedPreferences.Editor editor = getSharedPreferences(Common.SHARED_NAME, MODE_PRIVATE).edit();

                                            editor.putString("teacher_id", ID);
                                            editor.putString("uni", university);
                                            editor.putString("dept", department);

                                            editor.apply();

                                            startMainActivity();

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(context, "The Password does not match", Toast.LENGTH_SHORT).show();
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
        binding.chooseDept.setAdapter(DepartmentsArray);

        binding.chooseDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

//    String id;
//
//    private DatabaseReference mDatabaseReference;
//    public static List<StudentModel> students;
//    public static List<String> ids;
//
//    private Button done;
//
//    Intent intent = getIntent();
//    id = intent.getStringExtra("id");
//
//    students = new ArrayList<>();
//    ids = new ArrayList<>();
//
//    mDatabaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.uni)
//                .child(MainActivity.dept)
//                .child(Common.KEY_COURSES)
//                .child(id)
//                .child("students");
//
//        new fetchStudents().execute();
//
//
//    done = findViewById(R.id.done);
//        done.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            for (StudentModel student : students){
//                Log.d(TAG, "onClick: id: " + student.getId() + " name: " + student.getName() + " present: " + student.isPresent());
//            }
//
////                Intent intent1 = new Intent(context, GenerateReports.class);
////                startActivity(intent1);
//        }
//    });
//
//    public class fetchStudents extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            ids.clear();
//
//            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()){
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                            StudentModel student = snapshot.getValue(StudentModel.class);
//
//                            student.setPresent(false);
//                            students.add(student);
//
//                            ids.add(student.getId());
//
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    ScannerActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//            return null;
//        }
//    }

}



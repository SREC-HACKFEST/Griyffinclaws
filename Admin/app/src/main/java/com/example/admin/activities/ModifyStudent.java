package com.example.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityModifyStudentBinding;
import com.example.admin.model.StudentModel;
import com.example.admin.model.TeacherModel;

public class ModifyStudent extends AppCompatActivity {
    private static final String TAG = "ModifyStudent";
    private Context context = ModifyStudent.this;

    String semester, id;

    private ActivityModifyStudentBinding binding;

    private DatabaseReference mDatabaseReference;

    private ProgressDialog progressDialog;

    private StudentModel studentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyStudentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        studentData = new StudentModel();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        semester = intent.getStringExtra("semester");

        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Fetching Student Data...");
        progressDialog.show();

        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_STUDENTS)
                .child(semester)
                .child(id);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    studentData = dataSnapshot.getValue(StudentModel.class);
                    updateUI(studentData);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "No " + id + " exits.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isEmpty(binding.id, binding.textInputId) || Common.isEmpty(binding.name, binding.textInputName)
                        || Common.isEmpty(binding.cgpa, binding.textInputCgpa)){
                } else if (TextUtils.isEmpty(semester)){
                    Toast.makeText(context, "Please choose semester", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Updating Student...");
                    progressDialog.show();

                    studentData = new StudentModel(
                            Common.getString(binding.id),
                            studentData.getPassword(),
                            Common.getString(binding.name),
                            semester,
                            Common.getString(binding.cgpa)
                    );

                    if (!TextUtils.isEmpty(Common.getString(binding.number)))
                        studentData.setPhone_number(Common.getString(binding.number));

                    if (!TextUtils.isEmpty(Common.getString(binding.email)))
                        studentData.setEmail(Common.getString(binding.email));

                    updateStudent();
                }
            }
        });

    }

    private void updateStudent() {
        mDatabaseReference.setValue(studentData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Student Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure to Delete this Student?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteStudent();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteStudent() {
        progressDialog.setMessage("Deleting Student...");
        progressDialog.show();

        StorageReference deleteImages = FirebaseStorage.getInstance().getReference("Students_Images/" + id);

        deleteImages.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference reference : listResult.getItems()){
                            reference.delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        mDatabaseReference.child("courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Log.d(TAG, "onDataChange: ");
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                Log.d(TAG, "onDataChange: subject " + snapshot.getKey());

                                final DatabaseReference coursesRef = FirebaseDatabase.getInstance()
                                        .getReference(MainActivity.mAdminData.getBelongs_to())
                                        .child(MainActivity.mAdminData.getDepartment())
                                        .child(Common.KEY_COURSES)
                                        .child(snapshot.getKey())
                                        .child("students");

                                coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                                                    if (dataSnapshot1.child("id").getValue(String.class).equals(studentData.getId())){

                                                        Log.d(TAG, "onDataChange: student " + dataSnapshot1.getKey());

                                                        coursesRef.child(dataSnapshot1.getKey())
                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "onSuccess: ");
                                                            }
                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                                                    }
                                                                });

                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        mDatabaseReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Student Deleted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(StudentModel studentData) {
        binding.id.setText(studentData.getId());
        binding.name.setText(studentData.getName());
        binding.cgpa.setText(studentData.getCgpa());

        if (!TextUtils.isEmpty(studentData.getPhone_number()))
            binding.number.setText(studentData.getPhone_number());

        if (!TextUtils.isEmpty(studentData.getEmail()))
            binding.email.setText(studentData.getEmail());

        setupSpinner();

        progressDialog.dismiss();
    }

    private void setupSpinner() {
        ArrayAdapter<String> spinnerArray = new ArrayAdapter<String>(context, R.layout.spinner_item, getResources().getStringArray(R.array.semesters)) {
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

        spinnerArray.setDropDownViewResource(R.layout.spinner_item);
        binding.semester.setAdapter(spinnerArray);

        int pos = spinnerArray.getPosition(semester);
        binding.semester.setSelection(pos);

        binding.semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                semester = (String) adapterView.getItemAtPosition(i);
                if (semester.equals("Choose here")) {
                    TextView tv = (TextView) view;
                    tv.setTextColor(getResources().getColor(R.color.default_color));
                    semester = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        } else if (item.getItemId() == R.id.delete){
            showDialogBox();
        }
        return super.onOptionsItemSelected(item);
    }
}

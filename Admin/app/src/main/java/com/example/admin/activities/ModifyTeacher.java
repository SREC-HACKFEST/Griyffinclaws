package com.example.admin.activities;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityModifyTeacherBinding;
import com.example.admin.model.TeacherModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ModifyTeacher extends AppCompatActivity {
    private static final String TAG = "ModifyTeacher";
    private Context context = ModifyTeacher.this;

    private String id;

    private ActivityModifyTeacherBinding binding;

    private DatabaseReference mDatabaseReference;

    private ProgressDialog progressDialog;

    private TeacherModel teacherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyTeacherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        teacherData = new TeacherModel();
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Fetching Teacher Data...");
        progressDialog.show();

        mDatabaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_TEACHERS)
                .child(id);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    teacherData = dataSnapshot.getValue(TeacherModel.class);
                    updateUI(teacherData);
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

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isEmpty(binding.id, binding.textInputId) || Common.isEmpty(binding.name, binding.textInputName)
                        || Common.isEmpty(binding.edu, binding.textInputEdu) || Common.isEmpty(binding.qua, binding.textInputQua)
                        || Common.isEmpty(binding.exp, binding.textInputExp) || Common.isEmpty(binding.pos, binding.textInputPos)) {

                } else {
                    progressDialog.setMessage("Updating Teacher");
                    progressDialog.show();

                    teacherData = new TeacherModel(
                            Common.getString(binding.id),
                            teacherData.getPassword(),
                            Common.getString(binding.name),
                            Common.getString(binding.edu),
                            Common.getString(binding.qua),
                            Common.getString(binding.exp),
                            Common.getString(binding.pos)
                    );

                    if (!TextUtils.isEmpty(Common.getString(binding.number)))
                        teacherData.setPhone_number(Common.getString(binding.number));

                    if (!TextUtils.isEmpty(Common.getString(binding.email)))
                        teacherData.setEmail(Common.getString(binding.email));

                    registerTeacherNow();
                }
            }
        });

    }

    private void registerTeacherNow() {

        mDatabaseReference.setValue(teacherData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Teacher Updated Successfully", Toast.LENGTH_SHORT).show();
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

    private void updateUI(TeacherModel teacherData) {
        binding.id.setText(teacherData.getId());
        binding.name.setText(teacherData.getName());
        binding.edu.setText(teacherData.getEducation());
        binding.qua.setText(teacherData.getQualification());
        binding.exp.setText(teacherData.getExperience());
        binding.pos.setText(teacherData.getPosition());

        if (!TextUtils.isEmpty(teacherData.getPhone_number()))
            binding.number.setText(teacherData.getPhone_number());

        if (!TextUtils.isEmpty(teacherData.getEmail()))
            binding.email.setText(teacherData.getEmail());

        progressDialog.dismiss();
    }

    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure to Delete this Teacher?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteTeacher();
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

    private void deleteTeacher() {
        progressDialog.setMessage("Deleting Teacher...");
        progressDialog.show();

        mDatabaseReference.child("courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d(TAG, "onDataChange: ");
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: subject " + snapshot.getKey());

                                final DatabaseReference coursesRef = FirebaseDatabase.getInstance()
                                        .getReference(MainActivity.mAdminData.getBelongs_to())
                                        .child(MainActivity.mAdminData.getDepartment())
                                        .child(Common.KEY_COURSES)
                                        .child(snapshot.getKey())
                                        .child("teacher");

                                coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            coursesRef.removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
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
                        Toast.makeText(context, "Teacher Deleted Successfully", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.delete) {
            showDialogBox();
        }
        return super.onOptionsItemSelected(item);
    }
}

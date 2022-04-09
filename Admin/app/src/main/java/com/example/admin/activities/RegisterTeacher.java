package com.example.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityRegisterTeacherBinding;
import com.example.admin.model.TeacherModel;

public class RegisterTeacher extends AppCompatActivity {
    private static final String TAG = "RegisterTeacher";
    private Context context = RegisterTeacher.this;

    private ActivityRegisterTeacherBinding binding;

    private DatabaseReference teacherReference;
    private TeacherModel teacherData;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterTeacherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isEmpty(binding.id, binding.textInputId) || Common.isEmpty(binding.name, binding.textInputName)
                    || Common.isEmpty(binding.edu, binding.textInputEdu) || Common.isEmpty(binding.qua, binding.textInputQua)
                    || Common.isEmpty(binding.exp, binding.textInputExp) || Common.isEmpty(binding.pos, binding.textInputPos)){

                } else {
                    progressDialog.setMessage("Registering Teacher...");
                    progressDialog.show();

                    teacherData = new TeacherModel(
                            Common.getString(binding.id),
                            "123456",
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
        teacherReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_TEACHERS)
                .child(Common.getString(binding.id));

        teacherReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    progressDialog.dismiss();
                    binding.textInputId.setError("Teacher with this id already exists");
                } else {
                    teacherReference.setValue(teacherData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Teacher Registered Successfully", Toast.LENGTH_SHORT).show();
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
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

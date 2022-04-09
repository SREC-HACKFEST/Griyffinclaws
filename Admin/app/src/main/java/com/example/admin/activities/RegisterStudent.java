package com.example.admin.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityRegisterStudentBinding;
import com.example.admin.model.StudentModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class RegisterStudent extends AppCompatActivity {
    private Context context = RegisterStudent.this;

    private ActivityRegisterStudentBinding binding;

    private int clickedimage;
    private List<Uri> images;

    private String semester;
    private StudentModel studentData;

    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterStudentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);

        images = new ArrayList<>();
        initSpinner();

        binding.choose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedimage = 1;
                chooseImage();
            }
        });

        binding.choose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedimage = 2;
                chooseImage();
            }
        });

        binding.choose3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedimage = 3;
                chooseImage();
            }
        });

        binding.chooseMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedimage = 0;
                chooseImage();
            }
        });

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (images.size() < 3){
                    Toast.makeText(context, "at least 3 images required", Toast.LENGTH_SHORT).show();
                }else
                    if (Common.isEmpty(binding.id, binding.textInputId) || Common.isEmpty(binding.name, binding.textInputName)
                        || Common.isEmpty(binding.cgpa, binding.textInputCgpa)){
                } else if (TextUtils.isEmpty(semester)){
                    Toast.makeText(context, "Please choose semester", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Registering Student...");
                    progressDialog.show();

                    studentData = new StudentModel(
                            Common.getString(binding.id),
                            "123456",
                            Common.getString(binding.name),
                            semester,
                            Common.getString(binding.cgpa)
                    );

                    if (!TextUtils.isEmpty(Common.getString(binding.number)))
                        studentData.setPhone_number(Common.getString(binding.number));

                    if (!TextUtils.isEmpty(Common.getString(binding.email)))
                        studentData.setEmail(Common.getString(binding.email));

                    uploadImagesNow();
                }
            }
        });
    }

    private void uploadImagesNow() {

        storageReference = FirebaseStorage.getInstance().getReference().child("Students_Images/" + studentData.getId());

        for (int i=0; i<images.size(); i++){
            storageReference.child(i+"")
                    .putFile(images.get(i));
        }

        uploadToDatabase();
    }

    private void uploadToDatabase() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_STUDENTS)
                .child(studentData.getSemester())
                .child(Common.getString(binding.id));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    progressDialog.dismiss();
                    binding.textInputId.setError("Student with this id already exists");
                } else {
                    databaseReference.setValue(studentData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Student Registered Successfully", Toast.LENGTH_SHORT).show();
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

    private void chooseImage() {
        CropImage.startPickImageActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri filePath = CropImage.getPickImageResultUri(this, data);

            CropImage.activity(filePath)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .setRequestedSize(800, 800)
                    .setInitialCropWindowPaddingRatio(0)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                images.add(result.getUri());

                if (clickedimage == 1){
                    binding.pic1.setImageURI(result.getUri());
                } else if (clickedimage == 2){
                    binding.pic2.setImageURI(result.getUri());
                } else if (clickedimage == 3) {
                    binding.pic3.setImageURI(result.getUri());
                }
            }
        }
    }

    private void initSpinner() {
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

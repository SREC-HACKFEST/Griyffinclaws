package com.example.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.admin.common.Common;

public class ChangePassword extends AppCompatActivity {
    private static final String TAG = "ChangePassword";
    private Context context = ChangePassword.this;

    private Toolbar toolbar;
    private EditText oldPass, newPass;
    private Button changePass;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference mDatabaseReference;

    private String adminID, uni, dept;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Common.COL_ADMINS);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        AuthCredential credential = EmailAuthProvider
                .getCredential(MainActivity.mAdminData.getEmail(), MainActivity.mAdminData.getPassword());

        user.reauthenticate(credential);

        Intent intent = getIntent();
        adminID = intent.getStringExtra("ID");
        uni = intent.getStringExtra("uni");
        dept = intent.getStringExtra("dept");

        oldPass = findViewById(R.id.old);
        newPass = findViewById(R.id.new_pass);
        changePass = findViewById(R.id.change_password);

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Old = oldPass.getText().toString();
                String New = newPass.getText().toString();

                if (TextUtils.isEmpty(Old) || TextUtils.isEmpty(New)){
                    Toast.makeText(context, "Please Enter Required Fields", Toast.LENGTH_SHORT).show();
                } else {

                    if (Old.equals(MainActivity.mAdminData.getPassword())){
                        changePassword(Old, New);
                    } else {
                        Toast.makeText(context, "Old Password does not matched", Toast.LENGTH_SHORT).show();
                        oldPass.requestFocus();
                    }
                }
            }
        });
    }

    private void changePassword(String old, final String aNew) {

        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Changing Password...");
        progressDialog.show();

        user.updatePassword(aNew)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Password Updated Successfully, login to continue", Toast.LENGTH_SHORT).show();

                        MainActivity.mAdminData.setPassword(aNew);
                        MainActivity.mAdminData.setId(adminID);

                        mDatabaseReference.child(uni)
                                .child(dept)
                                .child(adminID)
                                .setValue(MainActivity.mAdminData);

                        mAuth.signOut();

                        SharedPreferences.Editor editor = getSharedPreferences(Common.SHARED_NAME, MODE_PRIVATE).edit();

                        editor.remove("admin_id");
                        editor.remove("uni");
                        editor.remove("dept");

                        editor.apply();

                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return false;
    }
}

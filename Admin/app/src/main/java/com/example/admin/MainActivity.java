package com.example.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.admin.activities.Modifications;
import com.example.admin.activities.RegisterStudent;
import com.example.admin.activities.RegisterSubject;
import com.example.admin.activities.RegisterTeacher;
import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityMainBinding;
import com.example.admin.model.AdminData;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;

    private SharedPreferences preferences;
    private String ID, uni, dept;
    public static AdminData mAdminData;

    private DatabaseReference mDatabaseReference;

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nav;
    private TextView name;

    private ActivityMainBinding binding;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        preferences = getSharedPreferences(Common.SHARED_NAME, MODE_PRIVATE);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(Common.COL_ADMINS);
        dl = findViewById(R.id.drawer);
        nav = findViewById(R.id.nav);

        View headerView = nav.getHeaderView(0);
        name = headerView.findViewById(R.id.name);

        if (preferences.contains("admin_id")) {
            ID = preferences.getString("admin_id", "asd");
            uni = preferences.getString("uni", "uni");
            dept = preferences.getString("dept", "dept");
            getAdminData(ID, uni, dept);
        } else {
            Toast.makeText(context, "Login to Continue", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        binding.registerTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterTeacher.class);
                startActivity(intent);
            }
        });

        binding.registerStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterStudent.class);
                startActivity(intent);
            }
        });

        binding.registerSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterSubject.class);
                startActivity(intent);
            }
        });

        binding.modifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Modifications.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(binding.toolbar);

        t = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.profile:
                        if (mAdminData != null) {
                            Intent intent = new Intent(context, ProfileActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    case R.id.change_password:
                        if (mAdminData != null) {
                            Intent intent = new Intent(context, ChangePassword.class);
                            intent.putExtra("ID", ID);
                            intent.putExtra("uni", uni);
                            intent.putExtra("dept", dept);
                            startActivity(intent);
                        }
                        return true;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();

                        preferences.edit().remove("admin_id").apply();
                        preferences.edit().remove("uni").apply();
                        preferences.edit().remove("dept").apply();

                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void getAdminData(String ID, String uni, String dept) {

        progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Fetching Admin Data...");
        progressDialog.show();

        mDatabaseReference.child(uni)
                .child(dept)
                .child(ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            mAdminData = dataSnapshot.getValue(AdminData.class);
                            name.setText(mAdminData.getName());
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }
}

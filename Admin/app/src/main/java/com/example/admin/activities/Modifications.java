package com.example.admin.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.admin.common.Common;
import com.example.admin.databinding.ActivityModificationsBinding;

public class Modifications extends AppCompatActivity {
    private Context context = Modifications.this;

    private ActivityModificationsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModificationsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.modifyTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity(Common.KEY_TEACHERS);
            }
        });

        binding.modifySubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity(Common.KEY_COURSES);
            }
        });

        binding.modifyStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListActivity(Common.KEY_STUDENTS);
            }
        });
    }

    private void startListActivity(String type) {
        Intent intent = new Intent(context, ListActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

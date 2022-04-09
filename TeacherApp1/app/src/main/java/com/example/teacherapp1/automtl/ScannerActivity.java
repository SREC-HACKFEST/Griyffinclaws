package com.example.teacherapp1.automtl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teacherapp1.automtl.common.GraphicOverlay;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.teacherapp1.MainActivity;
import com.example.teacherapp1.R;
import com.example.teacherapp1.automtl.common.CameraSource;
import com.example.teacherapp1.automtl.common.CameraSourcePreview;
import com.example.teacherapp1.automtl.common.GraphicOverlay;
import com.google.firebase.ml.common.FirebaseMLException;
import com.example.teacherapp1.common.Common;
import com.example.teacherapp1.model.StudentModel;
import com.example.teacherapp1.reports.GenerateReports;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScannerActivity extends AppCompatActivity {
    private static final String TAG = "ScannerActivity";
    public static final String KEY_CAMERA_PERMISSION_GRANTED="CAMERA_PERMISSION_GRANTED";
    private Context context = ScannerActivity.this;

    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private CameraSource cameraSource = null;

    String id;

    private DatabaseReference mDatabaseReference;
    public static List<StudentModel> students;
    public static List<String> ids;

    private Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        if (getWindow() != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            Log.e(TAG, "Barcode scanner could not go into fullscreen mode!");
        }

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        students = new ArrayList<>();
        ids = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.uni)
                .child(MainActivity.dept)
                .child(Common.KEY_COURSES)
                .child(id)
                .child("students");

        new fetchStudents().execute();

        preview = findViewById(R.id.cameraSourcePReview);
        graphicOverlay = findViewById(R.id.graphicOverlay);

//        if (preview != null){
//            if (preview.isPermissionGranted(true, mMessageSender))
//                new Thread(mMessageSender).start();
//        }

        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (StudentModel student : students){
                    Log.d(TAG, "onClick: id: " + student.getId() + " name: " + student.getName() + " present: " + student.isPresent());
                }

                Intent intent1 = new Intent(context, GenerateReports.class);
                intent1.putExtra("student_list", (Serializable) students);
                intent1.putExtra("id", id);
                startActivity(intent1);
            }
        });
    }

    private void createCameraSource() {

        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
        }

        try {
            Log.d(TAG, "createCameraSource: ");
            cameraSource.setMachineLearningFrameProcessor(new AutoMLImageLabelerProcessor(this, AutoMLImageLabelerProcessor.Mode.LIVE_PREVIEW));
        } catch (FirebaseMLException e){
            e.printStackTrace();
        }

        Log.d(TAG, "createCameraSource: " + cameraSource);

        startCameraSource();
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        Log.d(TAG, "startCameraSource: " + cameraSource
        + " :" + preview + " :" + graphicOverlay);

        if (cameraSource != null && preview != null && graphicOverlay != null) {
            Log.d(TAG, "startCameraSource: true");
            try {
                Log.d(TAG, "startCameraSource: start");
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        } else {
            Log.d(TAG, "startCameraSource: unable to start camera");
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Log.d(TAG, "handleMessage: ");

            if (preview != null)
                createCameraSource();

        }
    };

    private final Runnable mMessageSender = () -> {
        Log.d(TAG, "mMessageSender: ");
        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_CAMERA_PERMISSION_GRANTED, false);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (preview.isPermissionGranted(true, mMessageSender))
            new Thread(mMessageSender).start();
    }


    public class fetchStudents extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            ids.clear();

            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            StudentModel student = snapshot.getValue(StudentModel.class);

                            student.setPresent(false);
                            students.add(student);

                            ids.add(student.getId());

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    ScannerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            return null;
        }
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}

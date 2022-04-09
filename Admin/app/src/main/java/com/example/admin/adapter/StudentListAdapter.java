package com.example.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.activities.ListActivity;
import com.example.admin.activities.ModifyStudent;
import com.example.admin.activities.ModifySubject;
import com.example.admin.activities.StudentsList;
import com.example.admin.common.Common;
import com.example.admin.model.StudentModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "StudentListAdapter";

    private Context context;
    private List<StudentModel> studentsList;
    public static List<StudentModel> selectedStudents = new ArrayList<>();
    private Boolean selected;

    public StudentListAdapter(Context context, List<StudentModel> studentsList, Boolean selected) {
        this.context = context;
        this.studentsList = studentsList;
        this.selected = selected;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == Common.VIEWTYPE_SEMESTER) {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.item_semester, parent, false);
            return new SemesterViewHolder(group);
        } else if (viewType == Common.VIEWTYPE_STUDENT) {
            ViewGroup day = (ViewGroup) inflater.inflate(R.layout.item_student, parent, false);
            return new StudentViewHolder(day);
        } else {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.item_semester, parent, false);
            return new SemesterViewHolder(group);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return studentsList.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SemesterViewHolder){
            ((SemesterViewHolder) holder).semester.setText("Semester " + studentsList.get(position).getSemester());
        } else if (holder instanceof StudentViewHolder){
            StudentViewHolder viewHolder = (StudentViewHolder) holder;
            final StudentModel student = studentsList.get(position);

            viewHolder.setIsRecyclable(false);

            if (selected){

                viewHolder.add.setVisibility(View.GONE);

            } else {

                viewHolder.add.setVisibility(View.VISIBLE);
                if (contains(student)){
                    viewHolder.add.setChecked(true);
                } else {
                    viewHolder.add.setChecked(false);
                }

                viewHolder.add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){

                            if (!contains(student)){
                                selectedStudents.add(student);
                            }

                            Log.d(TAG, "onCheckedChanged: students size: " + selectedStudents.size());

                        } else {

                            removeStudentFromList(student);
                            Log.d(TAG, "onCheckedChanged: students size: " + selectedStudents.size());

                            if (StudentsList.activity.equals("modify")){
                                Log.d(TAG, "onCheckedChanged: ");
                                removeSubjectFromStudent(student);
                            }
                        }
                    }
                });
            }

            viewHolder.id.setText(student.getId());
            viewHolder.name.setText(student.getName());
            viewHolder.cgpa.setText(student.getCgpa() + " CGPA");

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof ListActivity){
                        Intent intent = new Intent(context, ModifyStudent.class);
                        intent.putExtra("semester", student.getSemester());
                        intent.putExtra("id", student.getId());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    private boolean contains(StudentModel student){
        for (int i=0; i<selectedStudents.size(); i++){
            if (selectedStudents.get(i).getId().equals(student.getId())){
                return true;
            }
        }
        return false;
    }

    private void removeStudentFromList(StudentModel student) {
        for (int i=0; i<selectedStudents.size(); i++){
            StudentModel student1 = selectedStudents.get(i);
            if (student1.getId().equals(student.getId())){
                selectedStudents.remove(student1);
            }
        }
    }

    private void removeSubjectFromStudent(final StudentModel student) {
        final DatabaseReference courseReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_COURSES)
                .child(ModifySubject.id)
                .child("students");

        courseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: ");
                    if (dataSnapshot1.child("id").getValue(String.class).equals(student.getId())){
                        Log.d(TAG, "onDataChange: " + dataSnapshot1.getKey());

                        courseReference.child(dataSnapshot1.getKey())
                                .removeValue();

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        DatabaseReference studentReference = FirebaseDatabase.getInstance()
                .getReference(MainActivity.mAdminData.getBelongs_to())
                .child(MainActivity.mAdminData.getDepartment())
                .child(Common.KEY_STUDENTS)
                .child(student.getSemester())
                .child(student.getId())
                .child("courses")
                .child(ModifySubject.id);

        studentReference.removeValue();

        Log.d(TAG, "removeSubjectFromStudent: ");
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView id, name, cgpa;
        CheckBox add;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.item_id);
            name = itemView.findViewById(R.id.name);
            cgpa = itemView.findViewById(R.id.cgpa);
            add = itemView.findViewById(R.id.add);
        }
    }

    public class SemesterViewHolder extends RecyclerView.ViewHolder {

        TextView semester;

        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);

            semester = itemView.findViewById(R.id.txt_group_title);

        }
    }
}

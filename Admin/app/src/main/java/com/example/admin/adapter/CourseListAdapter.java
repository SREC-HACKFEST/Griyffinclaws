package com.example.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.R;
import com.example.admin.activities.ModifySubject;
import com.example.admin.common.Common;
import com.example.admin.model.CourseModel;
import com.example.admin.model.TeacherModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<CourseModel> coursesList;

    public CourseListAdapter(Context context, List<CourseModel> coursesList) {
        this.context = context;
        this.coursesList = coursesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == Common.VIEWTYPE_SEMESTER) {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.item_semester, parent, false);
            return new SemesterViewHolder(group);
        } else if (viewType == Common.VIEWTYPE_STUDENT) {
            ViewGroup day = (ViewGroup) inflater.inflate(R.layout.item_course, parent, false);
            return new ViewHolder(day);
        } else {
            ViewGroup group = (ViewGroup) inflater.inflate(R.layout.item_semester, parent, false);
            return new SemesterViewHolder(group);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return coursesList.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SemesterViewHolder){
            ((SemesterViewHolder) viewHolder).semester.setText("Semester " + coursesList.get(position).getSemester());
        } else if (viewHolder instanceof ViewHolder){

            ViewHolder holder = (ViewHolder) viewHolder;

            final CourseModel course = coursesList.get(position);

            holder.id.setText(course.getId());
            holder.name.setText(course.getName());

            if (course.getTeacher() != null){
                holder.teacher.setText(course.getTeacher().getName());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ModifySubject.class);
                    intent.putExtra("id", course.getId());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView id, name, teacher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            teacher = itemView.findViewById(R.id.teacher);
        }
    }

    public static class SemesterViewHolder extends RecyclerView.ViewHolder {

        TextView semester;

        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);

            semester = itemView.findViewById(R.id.txt_group_title);

        }
    }
}

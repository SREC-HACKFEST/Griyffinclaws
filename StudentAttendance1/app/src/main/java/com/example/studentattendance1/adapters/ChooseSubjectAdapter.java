package com.example.studentattendance1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentattendance1.R;
import com.example.studentattendance1.activities.CheckReports;
import com.example.studentattendance1.model.CourseItem;

import java.util.List;

public class ChooseSubjectAdapter extends RecyclerView.Adapter<ChooseSubjectAdapter.CourseItemHolder> {

    private List<CourseItem> courses;
    private Context context;

    public ChooseSubjectAdapter(List<CourseItem> courses, Context context) {
        this.courses = courses;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CourseItemHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.item_courses, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CourseItemHolder holder, int position) {
        final CourseItem item = courses.get(position);

        holder.id.setText(item.getId());
        holder.hours.setText(item.getCredit_hours()+" Credit Hours");
        holder.teacher.setText(item.getTeacher());
        holder.name.setText(item.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CheckReports.class);
                intent.putExtra("id", item.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class CourseItemHolder extends RecyclerView.ViewHolder {

        TextView id, name, hours, teacher;

        public CourseItemHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            hours = itemView.findViewById(R.id.credit_hours);
            teacher = itemView.findViewById(R.id.teacher);
        }
    }
}
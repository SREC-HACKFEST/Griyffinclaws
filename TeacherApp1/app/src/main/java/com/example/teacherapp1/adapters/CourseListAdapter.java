package com.example.teacherapp1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.teacherapp1.R;
import com.example.teacherapp1.automtl.ScannerActivity;
import com.example.teacherapp1.model.CourseItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseItemHolder> {

    private List<CourseItem> courses;
    private Context context;

    public CourseListAdapter(List<CourseItem> courses, Context context) {
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
        CourseItem item = courses.get(position);

        holder.id.setText(item.getId());
        holder.hours.setText(item.getCredit_hours()+" Credit Hours");
        holder.semester.setText(item.getSemester()+" Semester");
        holder.name.setText(item.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScannerActivity.class);
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

        TextView id, name, hours, semester;

        public CourseItemHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            hours = itemView.findViewById(R.id.credit_hours);
            semester = itemView.findViewById(R.id.semester);
        }
    }
}

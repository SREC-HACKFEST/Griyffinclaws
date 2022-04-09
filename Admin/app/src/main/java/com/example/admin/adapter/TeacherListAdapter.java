package com.example.admin.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.R;
import com.example.admin.activities.ModifySubject;
import com.example.admin.activities.ModifyTeacher;
import com.example.admin.model.TeacherModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TeacherListAdapter extends RecyclerView.Adapter<TeacherListAdapter.ViewHolder> {

    private Context context;
    private List<TeacherModel> teacherList;

    public TeacherListAdapter(Context context, List<TeacherModel> list) {
        this.context = context;
        this.teacherList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TeacherModel teacher = teacherList.get(position);

        holder.id.setText(teacher.getId());
        holder.name.setText(teacher.getName());
        holder.position.setText(teacher.getPosition());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyTeacher.class);
                intent.putExtra("id", teacher.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView id, name, position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.item_id);
            name = itemView.findViewById(R.id.name);
            position = itemView.findViewById(R.id.position);

        }
    }
}

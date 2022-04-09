package com.example.teacherapp1.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.teacherapp1.R;
import com.example.teacherapp1.model.StudentModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShowReportsAdapter extends RecyclerView.Adapter<ShowReportsAdapter.ReportViewHolder> {

    private Context context;
    private List<StudentModel> students;

    public ShowReportsAdapter(Context context, List<StudentModel> students) {
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReportViewHolder(
                LayoutInflater.from(context)
                .inflate(R.layout.item_report, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        StudentModel student = students.get(position);

        holder.setIsRecyclable(false);

        holder.sap_id.setText(student.getId());
        holder.name.setText(student.getName());
        if (student.isPresent()){
            holder.present.setChecked(true);
        } else {
            holder.absent.setChecked(true);
        }

        holder.present.setEnabled(false);
        holder.absent.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {

        private TextView sap_id, name;
        private RadioGroup buttons;
        private RadioButton present, absent;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            sap_id = itemView.findViewById(R.id.sap_id);
            name = itemView.findViewById(R.id.name);
            buttons = itemView.findViewById(R.id.buttons);
            present = itemView.findViewById(R.id.present);
            absent = itemView.findViewById(R.id.absent);
        }
    }
}

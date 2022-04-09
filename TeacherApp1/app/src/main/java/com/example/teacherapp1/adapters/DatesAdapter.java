package com.example.teacherapp1.adapters;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.teacherapp1.R;
import com.example.teacherapp1.model.AttendanceReport;
import com.example.teacherapp1.reports.GenerateReports;
import com.example.teacherapp1.reports.ShowReport;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.DateHolder> {

    private Context context;
    private List<AttendanceReport> reports;

    public DatesAdapter(Context context, List<AttendanceReport> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public DateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DateHolder(
                LayoutInflater.from(context)
                .inflate(R.layout.item_date, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DateHolder holder, int position) {
        AttendanceReport report = reports.get(position);

        holder.date.setText(report.getKey());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(context, ShowReport.class);
                intent1.putExtra("attendance_list", report);
                context.startActivity(intent1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class DateHolder extends RecyclerView.ViewHolder {

        TextView date;

        public DateHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
        }
    }
}

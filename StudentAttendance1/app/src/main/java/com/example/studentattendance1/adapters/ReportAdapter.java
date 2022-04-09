package com.example.studentattendance1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import com.example.studentattendance1.R;
import com.example.studentattendance1.model.AttendanceReport;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class  ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ItemViewHolder> {

private Context context;
private List<AttendanceReport> mReports;

public ReportAdapter(Context context, List<AttendanceReport> reports) {
        this.context = context;
        mReports = reports;
        }

@NonNull
@Override
public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(
        LayoutInflater.from(context)
        .inflate(R.layout.item_report, parent, false)
        );
        }



    @Override
public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        AttendanceReport report = mReports.get(position);

        holder.setIsRecyclable(false);

        holder.date.setText(report.getDate());
        holder.time.setText(report.getTime());
        holder.room.setText(report.getRoom());
        holder.teacher.setText(report.getTeacher());
        holder.present.setChecked(report.isPresent());

        holder.present.setEnabled(false);

        }

@Override
public int getItemCount() {
        return mReports.size();
        }

public class ItemViewHolder extends RecyclerView.ViewHolder {

    TextView date, time, room, teacher;
    RadioButton present;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        date = itemView.findViewById(R.id.date);
        time = itemView.findViewById(R.id.time);
        room = itemView.findViewById(R.id.room);
        teacher = itemView.findViewById(R.id.teacher);
        present = itemView.findViewById(R.id.present);
    }
}
}
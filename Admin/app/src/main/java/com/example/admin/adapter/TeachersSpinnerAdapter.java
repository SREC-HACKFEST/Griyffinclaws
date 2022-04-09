package com.example.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.admin.R;
import com.example.admin.model.TeacherModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TeachersSpinnerAdapter extends ArrayAdapter<TeacherModel> {

    private ArrayList<TeacherModel> teacherList;

    public TeachersSpinnerAdapter(Context context, ArrayList<TeacherModel> teachersList){
        super(context, 0, teachersList);
        this.teacherList = teachersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public int getPosition(@Nullable TeacherModel item) {
        for (int i=0; i<teacherList.size(); i++){
            if (getItem(i).getId().equals(item.getId())){
                return i;
            }
        }
        return super.getPosition(item);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_teacher_spinner,
                    parent,
                    false
            );
        }

        TextView id, name, pos;

        id = convertView.findViewById(R.id.item_id);
        name = convertView.findViewById(R.id.name);
        pos = convertView.findViewById(R.id.position);

        TeacherModel teacher = getItem(position);

        id.setText(teacher.getId());
        name.setText(teacher.getName());
        pos.setText(teacher.getPosition());

        return convertView;
    }
}

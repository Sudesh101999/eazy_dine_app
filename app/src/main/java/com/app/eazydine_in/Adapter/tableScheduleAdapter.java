package com.app.eazydine_in.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.eazydine_in.Models.ModelHome;
import com.app.eazydine_in.Models.tableScheduleModel;
import com.app.eazydine_in.R;

import java.util.ArrayList;

public class tableScheduleAdapter extends RecyclerView.Adapter<tableScheduleAdapter.ViewHolder> {

    private ArrayList<tableScheduleModel> list;

    public tableScheduleAdapter(ArrayList<tableScheduleModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_schedule_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        tableScheduleModel timeList = list.get(position);

        holder.startTv.setText(timeList.getStartTime());
        holder.endTv.setText(timeList.getEndTime());

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView startTv,endTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startTv = itemView.findViewById(R.id.home_dialog_time_start);
            endTv = itemView.findViewById(R.id.home_dialog_time_end);
        }
    }
}

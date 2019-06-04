package com.example.saloonapp.Adapters.Parlour;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

public class ParlourScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ParlourScheduleRecyclerViewAdapter.ItemParlourScheduleViewHolder> {

    private Activity activity;
    private List<String> dayNames;

    public ParlourScheduleRecyclerViewAdapter(Activity activity, List<String> dayNames) {
        this.activity = activity;
        this.dayNames = dayNames;
    }

    @NonNull
    @Override
    public ItemParlourScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_parlour_schedule, viewGroup, false);
        return new ItemParlourScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemParlourScheduleViewHolder itemParlourScheduleViewHolder, int position) {
        String dayName = dayNames.get(position);
        if (dayName != null) {

            List<Integer> time12HourFormat = new ArrayList<>();
            time12HourFormat.add(1);
            time12HourFormat.add(2);
            time12HourFormat.add(3);
            time12HourFormat.add(4);
            time12HourFormat.add(5);
            time12HourFormat.add(6);
            time12HourFormat.add(7);
            time12HourFormat.add(8);
            time12HourFormat.add(9);
            time12HourFormat.add(10);
            time12HourFormat.add(11);
            time12HourFormat.add(12);

            List<String> am_pmFormat = new ArrayList<>();
            am_pmFormat.add("AM");
            am_pmFormat.add("PM");

            itemParlourScheduleViewHolder.dayCB.setText(dayName);

            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, time12HourFormat);
            itemParlourScheduleViewHolder.toSP.setAdapter(adapter);
            itemParlourScheduleViewHolder.fromSP.setAdapter(adapter);

            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, am_pmFormat);
            itemParlourScheduleViewHolder.toFormatSP.setAdapter(adapter2);
            itemParlourScheduleViewHolder.fromFormatSP.setAdapter(adapter2);
        }
    }

    @Override
    public int getItemCount() {
        return dayNames.size();
    }

    public class ItemParlourScheduleViewHolder extends RecyclerView.ViewHolder {

        private AppCompatSpinner toFormatSP, fromFormatSP, fromSP, toSP;
        private CheckBox dayCB;

        public ItemParlourScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            fromFormatSP = itemView.findViewById(R.id.parlour_schedule_fromFormatSP);
            fromSP = itemView.findViewById(R.id.parlour_schedule_fromSP);
            toFormatSP = itemView.findViewById(R.id.parlour_schedule_toFormatSP);
            toSP = itemView.findViewById(R.id.parlour_schedule_toSP);
            dayCB = itemView.findViewById(R.id.parlour_schedule_dayCB);
        }
    }
}

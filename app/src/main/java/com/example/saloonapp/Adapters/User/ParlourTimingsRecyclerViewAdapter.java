package com.example.saloonapp.Adapters.User;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saloonapp.Models.ParlourTimingsModel;
import com.example.saloonapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParlourTimingsRecyclerViewAdapter extends RecyclerView.Adapter<ParlourTimingsRecyclerViewAdapter.ItemParlourTimingViewHolder> {

    private Activity activity;
    private List<ParlourTimingsModel> parlourTimingsModelList;
    private Map<String, String> dayNamesList;
    private String TAG = "PARLOUR_TIMING_RV_ADAPTER";

    public ParlourTimingsRecyclerViewAdapter(Activity activity, List<ParlourTimingsModel> parlourTimingsModelList) {
        this.activity = activity;
        this.parlourTimingsModelList = parlourTimingsModelList;
    }

    @NonNull
    @Override
    public ItemParlourTimingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_parlour_timings, viewGroup, false);
        setUpDaysList();
        return new ItemParlourTimingViewHolder(view);
    }

    private void setUpDaysList() {
        dayNamesList = new HashMap<>();
        dayNamesList.put("1", "SUN");
        dayNamesList.put("2", "MON");
        dayNamesList.put("3", "TUE");
        dayNamesList.put("4", "WED");
        dayNamesList.put("5", "THU");
        dayNamesList.put("6", "FRI");
        dayNamesList.put("7", "SAT");
    }

    @Override
    public void onBindViewHolder(@NonNull ItemParlourTimingViewHolder itemParlourTimingViewHolder, int position) {
        ParlourTimingsModel item = parlourTimingsModelList.get(position);
        if (item != null) {
            try {
                itemParlourTimingViewHolder.dayNameTV.setText(dayNamesList.get(item.getParlourTimingDay()));

                SimpleDateFormat _24HFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat _12HFormat = new SimpleDateFormat("hh:mm a");

                String[] splitingOpenDateTime = item.getParlourTimingOpenTime().split("T");
                String[] splitingCloseDateTime = item.getParlourTimingCloseTime().split("T");


                Date openTime = _24HFormat.parse(splitingOpenDateTime[1]);
                String finalOpenTime = _12HFormat.format(openTime);

                Date closeTime = _24HFormat.parse(splitingCloseDateTime[1]);
                String finalCloseTime = _12HFormat.format(closeTime);

                itemParlourTimingViewHolder.dayTimeTV.setText(finalOpenTime + " - " + finalCloseTime);
            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: " + e);
            }

        }
    }

    @Override
    public int getItemCount() {
        return parlourTimingsModelList.size();
    }

    public class ItemParlourTimingViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView dayNameTV, dayTimeTV;

        public ItemParlourTimingViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNameTV = itemView.findViewById(R.id.item_dayNameTV);
            dayTimeTV = itemView.findViewById(R.id.item_dayTimeTV);
        }
    }
}

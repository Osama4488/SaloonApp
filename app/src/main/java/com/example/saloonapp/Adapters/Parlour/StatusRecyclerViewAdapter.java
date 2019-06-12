package com.example.saloonapp.Adapters.Parlour;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class StatusRecyclerViewAdapter extends RecyclerView.Adapter<StatusRecyclerViewAdapter.ItemStatusViewHolder> {

    private Activity activity;
    private List<BookingOrAppointmentModel> modelList;
    private String status, TAG = "STATUS_RV_ADAPTER";

    public StatusRecyclerViewAdapter(Activity activity, String status, List<BookingOrAppointmentModel> modelList) {
        this.activity = activity;
        this.status = status;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ItemStatusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_status, viewGroup,false);
        return new ItemStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemStatusViewHolder itemStatusViewHolder, int position) {
        BookingOrAppointmentModel item = modelList.get(position);
        if (item != null) {
            try {
                String[] splitingDateTime = item.getDate().split("T");
                //date
                SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date currentDate = ymdFormat.parse(splitingDateTime[0]);
                SimpleDateFormat dmyFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String finalDate = dmyFormat.format(currentDate);

                //time
                SimpleDateFormat _24HFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat _12HFormat = new SimpleDateFormat("hh:mm a");
                Date currentTime = _24HFormat.parse(splitingDateTime[1]);
                String finalTime = _12HFormat.format(currentTime);

                //day
                DateFormat dayFormat = new SimpleDateFormat("EEEE");
                String finalDay = dayFormat.format(currentDate);

                itemStatusViewHolder.userNameTV.setText(item.getParlourOrUserName());
                itemStatusViewHolder.dayNameTV.setText(finalDay);
                itemStatusViewHolder.dateTimeTV.setText(finalDate + " | " + finalTime);

                if (!(status.equals("Pending"))) {
                    itemStatusViewHolder.acceptRejectLL.setVisibility(View.GONE);
                    itemStatusViewHolder.view.setVisibility(View.GONE);
                }

                itemStatusViewHolder.bookingCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                itemStatusViewHolder.acceptLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(activity, "Accepted.", Toast.LENGTH_SHORT).show();
                    }
                });
                itemStatusViewHolder.rejectLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(activity, "Rejected.", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: " + e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ItemStatusViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView bookingCV;
        private AppCompatTextView userNameTV, dayNameTV, dateTimeTV;
        private LinearLayout acceptLL, rejectLL, acceptRejectLL;
        private View view;

        public ItemStatusViewHolder(@NonNull View itemView) {
            super(itemView);

            bookingCV = itemView.findViewById(R.id.item_bookingCV);
            userNameTV = itemView.findViewById(R.id.item_userNameTV);
            dayNameTV = itemView.findViewById(R.id.item_dayNameTV);
            dateTimeTV = itemView.findViewById(R.id.item_dateTimeTV);
            acceptLL = itemView.findViewById(R.id.acceptLL);
            rejectLL = itemView.findViewById(R.id.rejecttLL);
            acceptRejectLL = itemView.findViewById(R.id.acceptRejectLL);
            view = itemView.findViewById(R.id.view);
        }
    }
}

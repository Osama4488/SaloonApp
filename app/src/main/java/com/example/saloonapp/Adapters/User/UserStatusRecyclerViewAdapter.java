package com.example.saloonapp.Adapters.User;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.AppCompatImageButton;
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

public class UserStatusRecyclerViewAdapter extends RecyclerView.Adapter<UserStatusRecyclerViewAdapter.ItemUserStatusViewHolder> {

    private Activity activity;
    private List<BookingOrAppointmentModel> bookingOrAppointmentModelList;
    private String status, TAG = "USER_STATUS_RV_ADAPTER";

    public UserStatusRecyclerViewAdapter(Activity activity, String status, List<BookingOrAppointmentModel> bookingOrAppointmentModelList) {
        this.activity = activity;
        this.status = status;
        this.bookingOrAppointmentModelList = bookingOrAppointmentModelList;
    }

    @NonNull
    @Override
    public ItemUserStatusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_user_status, viewGroup, false);
        return new ItemUserStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemUserStatusViewHolder itemUserStatusViewHolder, int position) {
        BookingOrAppointmentModel item = bookingOrAppointmentModelList.get(position);
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

                itemUserStatusViewHolder.parlourNameTV.setText(item.getParlourOrUserName());
                itemUserStatusViewHolder.dayNameTV.setText(finalDay);
                itemUserStatusViewHolder.dateTimeTV.setText(finalDate + " | " + finalTime);

                if (!(status.equals("Scheduled"))) {
                    itemUserStatusViewHolder.cancelLL.setVisibility(View.GONE);
                    itemUserStatusViewHolder.view.setVisibility(View.GONE);
                }

                itemUserStatusViewHolder.cancelIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(activity, "Canceled.", Toast.LENGTH_SHORT).show();
                    }
                });

                itemUserStatusViewHolder.appointmentCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: " + e);
            }
        }
    }

    @Override
    public int getItemCount() {
        return bookingOrAppointmentModelList.size();
    }

    public class ItemUserStatusViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView appointmentCV;
        private AppCompatTextView parlourNameTV, dayNameTV, dateTimeTV;
        private AppCompatImageButton cancelIB;
        private LinearLayout cancelLL;
        private View view;

        public ItemUserStatusViewHolder(@NonNull View itemView) {
            super(itemView);

            appointmentCV = itemView.findViewById(R.id.item_appointmentCV);
            parlourNameTV = itemView.findViewById(R.id.item_parlourNameTV);
            dayNameTV = itemView.findViewById(R.id.item_dayNameTV);
            dateTimeTV = itemView.findViewById(R.id.item_dateTimeTV);
            cancelLL = itemView.findViewById(R.id.cancelLL);
            cancelIB = itemView.findViewById(R.id.item_cancelIB);
            view = itemView.findViewById(R.id.view);
        }
    }
}

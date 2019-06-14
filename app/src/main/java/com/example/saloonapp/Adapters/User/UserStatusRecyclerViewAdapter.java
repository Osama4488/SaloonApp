package com.example.saloonapp.Adapters.User;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.saloonapp.Activities.Parlour.BookingDetailActivity;
import com.example.saloonapp.Activities.User.AppointmentDetailActivity;
import com.example.saloonapp.Activities.User.UserDrawerActivity;
import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class    UserStatusRecyclerViewAdapter extends RecyclerView.Adapter<UserStatusRecyclerViewAdapter.ItemUserStatusViewHolder> {

    private Activity activity;
    private List<BookingOrAppointmentModel> modelList;

    //Api Strings
    private String url, status, TAG = "USER_STATUS_RV_ADAPTER";
    private OkHttpClient client;
    private Request request;

    public UserStatusRecyclerViewAdapter(Activity activity, String status, List<BookingOrAppointmentModel> modelList) {
        this.activity = activity;
        this.status = status;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ItemUserStatusViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_user_status, viewGroup, false);
        return new ItemUserStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemUserStatusViewHolder itemUserStatusViewHolder, final int position) {
        final BookingOrAppointmentModel item = modelList.get(position);
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

                if (!(item.getStatus().equalsIgnoreCase("pending"))) {
                    itemUserStatusViewHolder.cancelLL.setVisibility(View.GONE);
                    itemUserStatusViewHolder.view.setVisibility(View.GONE);
                }

                itemUserStatusViewHolder.cancelIB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog(item);
                    }
                });

                itemUserStatusViewHolder.appointmentCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, AppointmentDetailActivity.class);
                        intent.putExtra("bookingId", modelList.get(position).getBookingOrAppointmentId());
                        intent.putExtra("parlourId", modelList.get(position).getParlourOrUserId()); // passing parlour id
                        activity.startActivity(intent);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "onBindViewHolder: " + e);
            }
        }
    }

    public void cancelDialog(final BookingOrAppointmentModel item) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("CANCEL");
        alertDialogBuilder.setMessage("Are you sure, you want to cancel this appointment?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitApiChangeStatus(item);
                dialog.dismiss();
            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String getToken(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiChangeStatus(BookingOrAppointmentModel item) {
        url = activity.getString(R.string.url) + "bookings/status?id=" + item.getBookingOrAppointmentId() + "&parlourid=" + item.getParlourOrUserId() + "&statusname=canceled";

        client = new OkHttpClient.Builder()
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create( JSON, "" );

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .put(body)
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiChangeStatus: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Appointment has been canceled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    activity.finish();
                    Intent intent = new Intent(activity, UserDrawerActivity.class);
                    intent.putExtra("refresh", true);
                    activity.startActivity(intent);
                } else {
                    Log.e(TAG, "hitApiChangeStatus: onResponse: " + response.code());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    @Override
    public int getItemCount() {
        return modelList.size();
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

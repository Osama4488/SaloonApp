package com.example.saloonapp.Activities.Parlour;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.saloonapp.Activities.User.AppointmentDetailActivity;
import com.example.saloonapp.Activities.User.UserDrawerActivity;
import com.example.saloonapp.Adapters.Common.DetailsRecyclerViewAdapter;
import com.example.saloonapp.Models.DetailsModel;
import com.example.saloonapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BookingDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatTextView statusTV, dayTV, dateTimeTV, userNameTV, userNumberTV, userEmailTV, totalTV;
    private RecyclerView bookingDetailRV;
    private LinearLayout acceptLL, rejectLL, acceptRejectLL;
    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;
    private List<DetailsModel> detailsModelList;
    private String bookingId;

    //Api Strings
    private String url, TAG = "BOOKING_DETAILS_ACTIVITY";
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        getValuesOnStart();
        bindControls();
        bindListeners();
        toolbarSetting();
        hitApiGetUserDetails();
    }

    private void getValuesOnStart() {
        bookingId = getIntent().getExtras().getString("bookingId", null);
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);
        statusTV = findViewById(R.id.booking_bookingStatusTV);
        dayTV = findViewById(R.id.booking_bookingDayTV);
        dateTimeTV = findViewById(R.id.booking_bookingDateTimeTV);
        userNameTV = findViewById(R.id.booking_userNameTV);
        userNumberTV = findViewById(R.id.booking_userNumberTV);
        userEmailTV = findViewById(R.id.booking_userEmailTV);
        totalTV = findViewById(R.id.booking_bookingTotalTV);
        bookingDetailRV = findViewById(R.id.booking_bookingDetailsRV);
        acceptLL = findViewById(R.id.booking_acceptLL);
        rejectLL = findViewById(R.id.booking_rejectLL);
        acceptRejectLL = findViewById(R.id.booking_acceptRejectLL);
    }

    private void bindListeners() {
        acceptLL.setOnClickListener(this);
        rejectLL.setOnClickListener(this);
    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitleTV.setText("DETAILS");
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiGetUserDetails() {
        url = getString(R.string.url) + "bookings/clientdetail?id=" + bookingId;

        client = new OkHttpClient.Builder()
                .build();

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .get()
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookingDetailActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetUserDetails: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    castBookingDetailsData(response);
                } else {
                    Log.e(TAG, "hitApiGetUserDetails: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BookingDetailActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castBookingDetailsData(Response response) {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            String date = jsonObject.getString("DateTime");
            final String name = jsonObject.getString("Name");
            final String email = jsonObject.getString("Email");
            final String number = jsonObject.getString("PhoneNumber");
            final String status = jsonObject.getString("Status");
            Integer total = 0;
            JSONArray bookingDetailsArray = jsonObject.getJSONArray("BookingServices");
            detailsModelList = new ArrayList<>();
            for (int i = 0; i < bookingDetailsArray.length(); i++) {
                detailsModelList.add(new DetailsModel(
                        bookingDetailsArray.getJSONObject(i).getString("SubServiceName"),
                        bookingDetailsArray.getJSONObject(i).getString("Price"),
                        bookingDetailsArray.getJSONObject(i).getString("Quantity")
                ));
                Double valueToDouble = Double.parseDouble(detailsModelList.get(i).getSubServicePrice());
                Integer price = valueToDouble.intValue();
                Integer qty = Integer.valueOf(detailsModelList.get(i).getSubServiceQty());
                total = total + (price * qty);
            }

            String[] splitingDateTime = date.split("T");
            //date
            SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = ymdFormat.parse(splitingDateTime[0]);
            SimpleDateFormat dmyFormat = new SimpleDateFormat("dd-MMM-yyyy");
            final String finalDate = dmyFormat.format(currentDate);

            //time
            SimpleDateFormat _24HFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HFormat = new SimpleDateFormat("hh:mm a");
            Date currentTime = _24HFormat.parse(splitingDateTime[1]);
            final String finalTime = _12HFormat.format(currentTime);

            //day
            DateFormat dayFormat = new SimpleDateFormat("EEEE");
            final String finalDay = dayFormat.format(currentDate);

            final Integer finalTotal = total;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTV.setText("Status: " + status);
                    dayTV.setText("Day: " + finalDay);
                    dateTimeTV.setText("Date and Time: " + finalDate + " | " + finalTime);
                    userNameTV.setText(name);
                    userEmailTV.setText(email);
                    userNumberTV.setText(number);
                    totalTV.setText("RS. " + finalTotal.toString());
                    if (!(status.equalsIgnoreCase("pending"))) {
                        acceptRejectLL.setVisibility(View.GONE);
                    } else {
                        acceptRejectLL.setVisibility(View.VISIBLE);
                    }
                    setUpList();
                }
            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BookingDetailActivity.this, "Network error, try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "castBookingDetailsData: " + e);
        }
    }

    private void setUpList() {
        DetailsRecyclerViewAdapter adapter = new DetailsRecyclerViewAdapter(BookingDetailActivity.this,  detailsModelList);
        bookingDetailRV.setHasFixedSize(true);
        bookingDetailRV.setLayoutManager(new LinearLayoutManager(this));
        bookingDetailRV.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == acceptLL) {
            acceptDialog();
        } else if (v == rejectLL) {
            rejectDialog();
        }
    }

    public void acceptDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookingDetailActivity.this);
        alertDialogBuilder.setTitle("REJECT");
        alertDialogBuilder.setMessage("Are you sure, you want to reject this booking?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitApiChangeStatus("accepted");
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

    public void rejectDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookingDetailActivity.this);
        alertDialogBuilder.setTitle("ACCEPT");
        alertDialogBuilder.setMessage("Are you sure, you want to accept this booking?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitApiChangeStatus("rejected");
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

    private void hitApiChangeStatus(final String status) {
        url = getString(R.string.url) + "bookings/status?id=" + bookingId + "&parlourid=&statusname=" + status;

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BookingDetailActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiChangeStatus: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BookingDetailActivity.this, "Booking has been " + status, Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                    Intent intent = new Intent(BookingDetailActivity.this, ParlourDrawerActivity.class);
                    intent.putExtra("refresh", true);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "hitApiChangeStatus: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BookingDetailActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}

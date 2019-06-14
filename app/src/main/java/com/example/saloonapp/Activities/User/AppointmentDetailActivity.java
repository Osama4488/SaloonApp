package com.example.saloonapp.Activities.User;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

public class AppointmentDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatTextView statusTV, dayTV, dateTimeTV, parlourNameTV, parlourNumberTV, parlourEmailTV, totalTV;
    private AppCompatRatingBar parlourRB;
    private RecyclerView appointmentDetailRV;
    private AppCompatButton completeBN;
    private RelativeLayout completeRL;
    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;
    private List<DetailsModel> detailsModelList;
    private String bookingId, parlourId, userEmail;

    //Api Strings
    private String url, TAG = "APPOINTMENT_DETAIL_ACTIVITY";
    private OkHttpClient client;
    private Request request;

    // Feedback dilaog Controls
    private AlertDialog feedbackDialog;
    private AppCompatButton dialog_feedbackDoneBN;
    private AppCompatTextView dialog_feedbackQuestionTV;
    private AppCompatRatingBar dialog_feedbackRB;
    private AppCompatEditText dialog_feebackCommentET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        getValuesOnStart();
        bindControls();
        bindListeners();
        toolbarSetting();
        hitApiGetParlourDetails();
    }

    private void getValuesOnStart() {
        bookingId = getIntent().getExtras().getString("bookingId", null);
        parlourId = getIntent().getExtras().getString("parlourId", null);
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);
        statusTV = findViewById(R.id.appointment_appointmnetStatusTV);
        dayTV = findViewById(R.id.appointment_appointmnetDayTV);
        dateTimeTV = findViewById(R.id.appointment_appointmnetDateTimeTV);
        parlourNameTV = findViewById(R.id.appointment_parlourNameTV);
        parlourNumberTV = findViewById(R.id.appointment_parlourNumberTV);
        parlourEmailTV = findViewById(R.id.appointment_parlourEmailTV);
        totalTV = findViewById(R.id.appointment_appointmentTotalTV);
        parlourRB = findViewById(R.id.appointment_parlourRB);
        appointmentDetailRV = findViewById(R.id.appointment_appointmentDetailsRV);
        completeBN = findViewById(R.id.appointment_completeBN);
        completeRL = findViewById(R.id.appointment_completeRL);
    }

    private void bindListeners() {
        completeBN.setOnClickListener(this);
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
        userEmail = sharedPreferences.getString("userName", null);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiGetParlourDetails() {
        url = getString(R.string.url) + "bookings/parlourdetail?id=" + bookingId + "&parlourid=" + parlourId;

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
                        Toast.makeText(AppointmentDetailActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetParlourDetails: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    castAppointmentDetailsData(response);
                } else {
                    Log.e(TAG, "hitApiGetParlourDetails: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppointmentDetailActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castAppointmentDetailsData(Response response) {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            String date = jsonObject.getString("DateTime");
            final String name = jsonObject.getString("Name");
            final String email = jsonObject.getString("Email");
            final String number = jsonObject.getString("PhoneNumber");
            final String status = jsonObject.getString("Status");
            final String rating = jsonObject.getString("Rating");
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
                    parlourNameTV.setText(name);
                    parlourEmailTV.setText(email);
                    parlourNumberTV.setText(number);
                    parlourRB.setRating(Float.valueOf(rating));
                    totalTV.setText("RS. " + finalTotal.toString());
                    if (status.equalsIgnoreCase("accepted") || status.equalsIgnoreCase("pending")) {
                        completeRL.setVisibility(View.VISIBLE);
                        if (status.equalsIgnoreCase("accepted")) {
                            completeBN.setText("Complete");
                        } else if (status.equalsIgnoreCase("pending")) {
                            completeBN.setText("Cancel");
                        }
                    } else {
                        completeRL.setVisibility(View.GONE);
                    }
                    setUpList();
                }
            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AppointmentDetailActivity.this, "Network error, try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "castAppointmentDetailsData: " + e);
        }
    }

    private void setUpList() {
        DetailsRecyclerViewAdapter adapter = new DetailsRecyclerViewAdapter(AppointmentDetailActivity.this,  detailsModelList);
        appointmentDetailRV.setHasFixedSize(true);
        appointmentDetailRV.setLayoutManager(new LinearLayoutManager(this));
        appointmentDetailRV.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == completeBN) {
            if (completeBN.getText().toString().equalsIgnoreCase("complete")) {
                completeDialog();
            } else if (completeBN.getText().toString().equalsIgnoreCase("cancel")) {
                cancelDialog();
            }
        } else if (v == dialog_feedbackDoneBN) {
                hitApiFeedback();
        }

    }

    private void hitApiFeedback() {
        url = getString(R.string.url) + "feedbacks";

        client = new OkHttpClient.Builder()
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("parlourid", parlourId);
            jsonObject.put("bookingid", bookingId);
            jsonObject.put("remarks", dialog_feebackCommentET.getText().toString());
            jsonObject.put("rating", dialog_feedbackRB.getRating());
        } catch (Exception e) {
            Log.e(TAG, "hitApiFeedback: " + e);
        }

        RequestBody body = RequestBody.create( JSON, jsonObject.toString() );

        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .post(body)
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AppointmentDetailActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiFeedback: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppointmentDetailActivity.this, "Thank you for your feedback.", Toast.LENGTH_LONG).show();
                            feedbackDialog.dismiss();
                        }
                    });
                    SharedPreferences.Editor editor = getSharedPreferences("feedbackDialog_" + userEmail, MODE_PRIVATE).edit();
                    editor.clear().apply();
                    finish();
                    Intent intent = new Intent(AppointmentDetailActivity.this, UserDrawerActivity.class);
                    intent.putExtra("refresh", true);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "hitApiFeedback: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppointmentDetailActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    public void cancelDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppointmentDetailActivity.this);
        alertDialogBuilder.setTitle("Cancel");
        alertDialogBuilder.setMessage("Are you sure, you want to cancel this appointment?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitApiChangeStatus("canceled");
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

    public void completeDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppointmentDetailActivity.this);
        alertDialogBuilder.setTitle("COMPLETE");
        alertDialogBuilder.setMessage("Are you sure, you want to complete this appointment?");
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hitApiChangeStatus("completed");
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
        url = getString(R.string.url) + "bookings/status?id=" + bookingId + "&parlourid=" + parlourId + "&statusname=" + status;

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
                        Toast.makeText(AppointmentDetailActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiChangeStatus: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    SharedPreferences.Editor editor = getSharedPreferences("feedbackDialog_" + userEmail, MODE_PRIVATE).edit();
                    editor.putString("parlourId", parlourId);
                    editor.putString("parlourName", parlourNameTV.getText().toString());
                    editor.putString("bookingId", bookingId);
                    editor.putString("userEmail", userEmail);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppointmentDetailActivity.this, "Appointment has been " + status, Toast.LENGTH_SHORT).show();
                            feedBackDialog();
                        }
                    });

                } else {
                    Log.e(TAG, "hitApiChangeStatus: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AppointmentDetailActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void feedBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentDetailActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_feedback, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView);
        bindDialogListeners();
        setTextView();

        feedbackDialog = builder.create();
        feedbackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feedbackDialog.setCanceledOnTouchOutside(false);
        feedbackDialog.setCancelable(false);
        feedbackDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        feedbackDialog.show();
    }

    private void setTextView() {
        dialog_feedbackQuestionTV.setText("How was your experience with " + parlourNameTV.getText().toString() + "?");
    }

    private void bindDialogListeners() {
        dialog_feedbackDoneBN.setOnClickListener(this);
    }

    private void bindDialogControls(View dialogView) {
        dialog_feedbackDoneBN = dialogView.findViewById(R.id.dialog_doneBN);
        dialog_feedbackRB = dialogView.findViewById(R.id.dialog_parlourRB);
        dialog_feedbackQuestionTV = dialogView.findViewById(R.id.dialog_questionTV);
        dialog_feebackCommentET = dialogView.findViewById(R.id.dialog_commentET);
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

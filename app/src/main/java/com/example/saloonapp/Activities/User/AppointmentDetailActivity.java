package com.example.saloonapp.Activities.User;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.saloonapp.Adapters.User.UserStatusRecyclerViewAdapter;
import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AppointmentDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatTextView statusTV, dayTV, dateTimeTV, parlourNameTV, parlourNumberTV, parlourEmailTV, totalTV;
    private AppCompatRatingBar parlourRB;
    private RecyclerView appointmentDetailRV;
    private AppCompatButton completeBN;
    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;
    private List<BookingOrAppointmentModel> bookingOrAppointmentModelList;

    //Api Strings
    private String url, TAG = "APPOINTMENT_DETAILS_ACTIVITY";
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        bindControls();
        bindListeners();
        toolbarSetting();
        dummyList();
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

    private void dummyList() {
        bookingOrAppointmentModelList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            bookingOrAppointmentModelList.add(new BookingOrAppointmentModel(
                    String.valueOf(i),
                    String.valueOf(i),
                    "Parlour Name " + i,
                    "2019-06-03T17:15:00"
            ));
        }
        setUpList();
    }

    private void setUpList() {
        UserStatusRecyclerViewAdapter adapter = new UserStatusRecyclerViewAdapter(AppointmentDetailActivity.this, "a", bookingOrAppointmentModelList);
        appointmentDetailRV.setHasFixedSize(true);
        appointmentDetailRV.setLayoutManager(new LinearLayoutManager(this));
        appointmentDetailRV.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == completeBN) {

        }
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

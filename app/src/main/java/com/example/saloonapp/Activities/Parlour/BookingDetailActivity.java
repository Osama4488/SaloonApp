package com.example.saloonapp.Activities.Parlour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toolbar;

import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class BookingDetailActivity extends AppCompatActivity {

    private AppCompatTextView statusTV, dayTV, dateTimeTV, userNameTV, userNumberTV, userEmailTV, totalTV;
    private RecyclerView bookingDetailRV;
    private AppCompatButton completeBN;
    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;
    private List<BookingOrAppointmentModel> bookingOrAppointmentModelList;

    //Api Strings
    private String url, TAG = "BOOKING_DETAILS_ACTIVITY";
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
    }
}

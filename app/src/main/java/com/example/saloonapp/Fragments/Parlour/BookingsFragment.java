package com.example.saloonapp.Fragments.Parlour;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.saloonapp.Adapters.Parlour.BookingViewPagerAdapter;
import com.example.saloonapp.Models.BookingOrAppointmentModel;
import com.example.saloonapp.R;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BookingsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<BookingOrAppointmentModel> acceptedList, rejectedList, completedList, pendingList;

    //Api Strings
    private String url, TAG = "BOOKING_FRAG";
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);

        bindControls(view);
        hitApiGetAllBookings();
        return view;
    }

    private void bindControls(View view) {
        tabLayout = view.findViewById(R.id.booking_frag_TL);
        viewPager = view.findViewById(R.id.booking_frag_VP);
    }


    private String getToken(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiGetAllBookings() {
        url = getString(R.string.url) + "bookings/clients";

        JSON = MediaType.parse("application/json; charset=utf-8");

        client = new OkHttpClient.Builder()
                .build();

        RequestBody body = RequestBody.create( JSON, "" );
        request = new Request.Builder()
                .url( url )
                .header("Authorization", "Bearer " + getToken())
                .get()
                .build();
        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetAllBookings: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    castBookingsToList(response);
                } else {
                    Log.e(TAG, "hitApiGetAllBookings: onResponse: " + response.code());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castBookingsToList(Response response) {
        try {
            JSONArray jsonArray = new JSONArray(response.body().string());
            acceptedList = new ArrayList<>();
            rejectedList = new ArrayList<>();
            completedList = new ArrayList<>();
            pendingList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String status = jsonArray.getJSONObject(i).getString("Status");
                if (status.equalsIgnoreCase("pending")) {
                    pendingList.add(new BookingOrAppointmentModel(
                            jsonArray.getJSONObject(i).getString("BookingId"),
                            jsonArray.getJSONObject(i).getString("ClientId"),
                            jsonArray.getJSONObject(i).getString("ClientName"),
                            jsonArray.getJSONObject(i).getString("DateTime"),
                            jsonArray.getJSONObject(i).getString("Status")
                    ));
                } else if (status.equalsIgnoreCase("accepted")) {
                    acceptedList.add(new BookingOrAppointmentModel(
                            jsonArray.getJSONObject(i).getString("BookingId"),
                            jsonArray.getJSONObject(i).getString("ClientId"),
                            jsonArray.getJSONObject(i).getString("ClientName"),
                            jsonArray.getJSONObject(i).getString("DateTime"),
                            jsonArray.getJSONObject(i).getString("Status")
                    ));
                } else if (status.equalsIgnoreCase("rejected")) {
                    rejectedList.add(new BookingOrAppointmentModel(
                            jsonArray.getJSONObject(i).getString("BookingId"),
                            jsonArray.getJSONObject(i).getString("ClientId"),
                            jsonArray.getJSONObject(i).getString("ClientName"),
                            jsonArray.getJSONObject(i).getString("DateTime"),
                            jsonArray.getJSONObject(i).getString("Status")
                    ));
                } else if (status.equalsIgnoreCase("completed")) {
                    completedList.add(new BookingOrAppointmentModel(
                            jsonArray.getJSONObject(i).getString("BookingId"),
                            jsonArray.getJSONObject(i).getString("ClientId"),
                            jsonArray.getJSONObject(i).getString("ClientName"),
                            jsonArray.getJSONObject(i).getString("DateTime"),
                            jsonArray.getJSONObject(i).getString("Status")
                    ));
                }
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpTabs();
                }
            });

        } catch (Exception e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Network error, try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "castBookingsToList: " + e);
        }
    }

    private void setUpTabs() {
        BookingViewPagerAdapter adapter = new BookingViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.AddFragment(new StatusFragment(pendingList, "Pending"), "Pending");
        adapter.AddFragment(new StatusFragment(acceptedList, "Accepted"), "Accepted");
        adapter.AddFragment(new StatusFragment(rejectedList, "Rejected"), "Rejected");
        adapter.AddFragment(new StatusFragment(completedList, "Completed"), "Completed");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

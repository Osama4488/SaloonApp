package com.example.saloonapp.Fragments.User;

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

import com.example.saloonapp.Adapters.User.AppointmentViewPagerAdapter;
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

public class UserAppointmentFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<BookingOrAppointmentModel> scheduledList, historyList;

    //Api Strings
    private String url, TAG = "USER_APPOINTMENT_FRAG";
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        bindControls(view);
        hitApiGetAllAppointments();
        return view;
    }

    private void bindControls(View view) {
        tabLayout = view.findViewById(R.id.appointment_frag_TL);
        viewPager = view.findViewById(R.id.appointment_frag_VP);
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiGetAllAppointments() {
        url = getString(R.string.url) + "bookings/parlours";

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
                Log.e(TAG, "hitApiGetAllAppointments: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    castAppointmentsToList(response);
                } else {
                    Log.e(TAG, "hitApiGetAllAppointments: onResponse: " + response.code());
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

    private void castAppointmentsToList(Response response) {
        try {
            JSONArray jsonArray = new JSONArray(response.body().string());
            scheduledList = new ArrayList<>();
            historyList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String status = jsonArray.getJSONObject(i).getString("Status");
                if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("accepted")) {
                    scheduledList.add(new BookingOrAppointmentModel(
                            jsonArray.getJSONObject(i).getString("BookingId"),
                            jsonArray.getJSONObject(i).getString("ParlourId"),
                            jsonArray.getJSONObject(i).getString("ParlourName"),
                            jsonArray.getJSONObject(i).getString("DateTime"),
                            jsonArray.getJSONObject(i).getString("Status")
                    ));
                } else {
                    historyList.add(new BookingOrAppointmentModel(
                            jsonArray.getJSONObject(i).getString("BookingId"),
                            jsonArray.getJSONObject(i).getString("ParlourId"),
                            jsonArray.getJSONObject(i).getString("ParlourName"),
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
            Log.e(TAG, "castAppointmentsToList: " + e);
        }
    }

    private void setUpTabs() {
        AppointmentViewPagerAdapter adapter = new AppointmentViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.AddFragment(new UserStatusFragment(scheduledList, "Scheduled"), "Scheduled");
        adapter.AddFragment(new UserStatusFragment(historyList, "History"), "History");
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

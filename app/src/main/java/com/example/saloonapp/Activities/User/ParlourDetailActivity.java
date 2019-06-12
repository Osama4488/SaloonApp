package com.example.saloonapp.Activities.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.saloonapp.Adapters.User.ParlourDetailViewPagerAdapter;
import com.example.saloonapp.Fragments.User.ParlourExpertFragment;
import com.example.saloonapp.Fragments.User.ParlourInfoFragment;
import com.example.saloonapp.Fragments.User.ParlourServiceFragment;
import com.example.saloonapp.Models.ExpertsModel;
import com.example.saloonapp.Models.ParlourInformationModel;
import com.example.saloonapp.Models.ParlourServicesModel;
import com.example.saloonapp.Models.ParlourTimingsModel;
import com.example.saloonapp.Models.ServicesModel;
import com.example.saloonapp.Models.SubServicesModel;
import com.example.saloonapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ParlourDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppCompatTextView parlourNameTV;
    private AppCompatImageView backIV;

    private ParlourInformationModel parlourInformationModel;
    private List<ExpertsModel> expertsModelList;
    private List<ParlourServicesModel> parlourServicesModelList;
    private String parlourId;

    //Api Strings
    private String url, TAG = "PARLOUR_DETAIL_ACTIVITY";
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parlour_detail);

        getValuesOnStart();
        bindControls();
        bindListeners();
        hitApiGetParlourDetail();
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiGetParlourDetail() {
        url = getString(R.string.url) + "parlours/" + parlourId;

        client = new OkHttpClient.Builder()
                .build();

        request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + getToken())
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ParlourDetailActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetParlourDetail: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200) {
                    castParlourDetails(response);
                } else {
                    Log.e(TAG, "hitApiGetParlourDetail : onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ParlourDetailActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void castParlourDetails(Response response) {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());

            // Fetching timings of the parlour
            JSONArray parlourTimingsArray = jsonObject.getJSONArray("ParlourTimings");
            List<ParlourTimingsModel> parlourTimingsModelList = new ArrayList<>();
            for (int i = 0; i < parlourTimingsArray.length(); i++) {
                parlourTimingsModelList.add(new ParlourTimingsModel(
                        parlourTimingsArray.getJSONObject(i).getString("Id"),
                        parlourTimingsArray.getJSONObject(i).getString("Day"),
                        parlourTimingsArray.getJSONObject(i).getString("Open"),
                        parlourTimingsArray.getJSONObject(i).getString("Close")
                ));
            }

            // Fetching Information of the parlours
            parlourInformationModel = new ParlourInformationModel(
                    jsonObject.getString("Id"),
                    jsonObject.getString("FullName"),
                    jsonObject.getString("Email"),
                    jsonObject.getString("Latitude") + ", " + jsonObject.getString("Longitude"),
                    jsonObject.getString("Rating"),
                    jsonObject.getString("PhoneNumber"),
                    parlourTimingsModelList
            );

            // Fetching Experts of the parlour
            JSONArray parlourExpertsArray = jsonObject.getJSONArray("Experts");
            expertsModelList = new ArrayList<>();
            for (int i = 0; i < parlourExpertsArray.length(); i++) {
                expertsModelList.add(new ExpertsModel(
                        parlourExpertsArray.getJSONObject(i).getString("Id"),
                        parlourExpertsArray.getJSONObject(i).getString("Name"),
                        parlourExpertsArray.getJSONObject(i).getString("Expertise"),
                        parlourExpertsArray.getJSONObject(i).getString("Experience")
                ));
            }

            // Fetching services and subservices of the parlour
            JSONArray parlourServicesArray = jsonObject.getJSONArray("Services");
            parlourServicesModelList = new ArrayList<>();
            for (int i = 0; i < parlourServicesArray.length(); i++) {
                JSONArray parlourSubServicesArray = parlourServicesArray.getJSONObject(i).getJSONArray("SubServices");
                List<SubServicesModel> subServicesModelList = new ArrayList<>();
                for (int j = 0; j < parlourSubServicesArray.length(); j++) {
                    subServicesModelList.add(new SubServicesModel(
                            parlourSubServicesArray.getJSONObject(j).getString("Id"),
                            parlourSubServicesArray.getJSONObject(j).getString("Name"),
                            parlourSubServicesArray.getJSONObject(j).getString("Description"),
                            parlourInformationModel.getParlourId(),
                            parlourSubServicesArray.getJSONObject(j).getString("Price")
                    ));
                }
                parlourServicesModelList.add(new ParlourServicesModel(
                        new ServicesModel(
                                parlourServicesArray.getJSONObject(i).getString("Id"),
                                parlourServicesArray.getJSONObject(i).getString("Name")),
                        subServicesModelList
                ));
            }
            parlourServicesModelList = parlourServicesModelList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    parlourNameTV.setText(parlourInformationModel.getParlourName());
                    setUpTabs();
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ParlourDetailActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                }
            });
            Log.e(TAG, "castParlourDetails: " + e);
        }
    }

    private void getValuesOnStart() {
        parlourId = getIntent().getExtras().getString("parlourId", null);
    }

    private void bindControls() {
        tabLayout = findViewById(R.id.parlour_detail_TL);
        viewPager = findViewById(R.id.parlour_detail_VP);
        backIV = findViewById(R.id.parlour_detail_backIV);
        parlourNameTV = findViewById(R.id.parlour_detail_parlourNameTV);
    }

    private void bindListeners() {
        backIV.setOnClickListener(this);
    }

    private void setUpTabs() {
        ParlourDetailViewPagerAdapter adapter = new ParlourDetailViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new ParlourInfoFragment(parlourInformationModel), "Information");
        adapter.AddFragment(new ParlourServiceFragment(parlourServicesModelList), "Services");
        adapter.AddFragment(new ParlourExpertFragment(expertsModelList), "Experts");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        if (v == backIV) {
            onBackPressed();
        }
    }
}

package com.example.saloonapp.Activities.Parlour;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.saloonapp.Adapters.Parlour.SuggestionRecyclerViewAdapter;
import com.example.saloonapp.Models.NearestParlourModel;
import com.example.saloonapp.Models.NearestSuggestionModel;
import com.example.saloonapp.Models.SuggestionModel;
import com.example.saloonapp.Models.Vincinetyformula;
import com.example.saloonapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SuggestionActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;
    private RecyclerView suggestionRV;
    private List<NearestSuggestionModel> nearestSuggestionModelList;

    //Current location
    private FusedLocationProviderClient fusedLocationClient;
    private Double lat, lng;

    //Api Strings
    private String url, TAG = "SUGGESTION_ACTIVITY";
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        bindControls();
        toolbarSetting();
        getCurrentLocation();
        hitApiGetSuggestedParlours(2.5);
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);
        suggestionRV = findViewById(R.id.suggestion_RV);
    }


    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitleTV.setText("SUGGESTED PARLOURS");
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(SuggestionActivity.this);

        if (ActivityCompat.checkSelfPermission(SuggestionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SuggestionActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(SuggestionActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            Toast.makeText(SuggestionActivity.this, location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SuggestionActivity.this, "Can not access current location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void hitApiGetSuggestedParlours(Double ratingValue) {
        url = getString(R.string.url) + "parlours/byrating?parlourrating=" + ratingValue;

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
                        Toast.makeText(SuggestionActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetSuggestedParlours: onFailure:" + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    castSuggestedParloursToList(response);
                } else {
                    Log.e(TAG, "hitApiGetSuggestedParlours: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SuggestionActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castSuggestedParloursToList(Response response) {
        try {
            nearestSuggestionModelList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                nearestSuggestionModelList.add(new NearestSuggestionModel(
                        new SuggestionModel(
                                jsonArray.getJSONObject(i).getString("ParlourId"),
                                jsonArray.getJSONObject(i).getString("FullName"),
                                jsonArray.getJSONObject(i).getString("BookingsCompleted"),
                                jsonArray.getJSONObject(i).getString("AvailableExperts"),
                                jsonArray.getJSONObject(i).getString("Rating"),
                                jsonArray.getJSONObject(i).getString("Latitude"),
                                jsonArray.getJSONObject(i).getString("Longitude")
                        ),
                        new Vincinetyformula().getDistance(
                                lat,
                                lng,
                                jsonArray.getJSONObject(i).getDouble("Latitude"),
                                jsonArray.getJSONObject(i).getDouble("Longitude")
                        )
                ));
            }

            Collections.sort(nearestSuggestionModelList, new Comparator<NearestSuggestionModel>() {
                @Override
                public int compare(NearestSuggestionModel lhs, NearestSuggestionModel rhs) {
                    if (lhs.getParlourDistance() < rhs.getParlourDistance()) return -1;
                    if (lhs.getParlourDistance() > rhs.getParlourDistance()) return 1;
                    return 0;
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpList();
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SuggestionActivity.this, "Network error, try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "castSuggestedParloursToList: " + e);
        }
    }

    private void setUpList() {
        List<SuggestionModel> tempNearestList = new ArrayList<>();
        for (int i = 0; i < nearestSuggestionModelList.size(); i++) {
            tempNearestList.add(nearestSuggestionModelList.get(i).getParlourDetails());
        }
        Collections.sort(tempNearestList, new Comparator<SuggestionModel>() {
            @Override
            public int compare(SuggestionModel lhs, SuggestionModel rhs) {
                return lhs.getParlourName().compareTo(rhs.getParlourName());
            }
        });
        SuggestionRecyclerViewAdapter adapter = new SuggestionRecyclerViewAdapter(SuggestionActivity.this, tempNearestList);
        suggestionRV.setHasFixedSize(true);
        suggestionRV.setLayoutManager(new LinearLayoutManager(this));
        suggestionRV.setAdapter(adapter);
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    @Override
    public void onClick(View v) {

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

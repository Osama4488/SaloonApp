package com.example.saloonapp.Fragments.User;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.saloonapp.Activities.User.ListOfParloursActivity;
import com.example.saloonapp.Adapters.User.ParlourRecyclerViewAdapter;
import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.Models.NearestParlourModel;
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


public class UserHomeFragment extends Fragment implements View.OnClickListener {

    private AppCompatTextView nearestSeeAllTV, recommendedSeeAllTV, allSeeAllTV;
    private RecyclerView nearestRV, recommendedRV, allRV;
    private List<ParlourModel> reccomendedModelList, allModelList, tempNearestList;
    private List<NearestParlourModel> nearestModelList;

    //Api Strings
    private String url, TAG = "USER_HOME_FRAGMENT";
    private OkHttpClient client;
    private Request request;

    //Current location
    private FusedLocationProviderClient fusedLocationClient;
    private Double lat, lng;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        bindControls(view);
        bindListeners();
        getCurrentLocation();
        hitApiGetAllParlours();
        return view;
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                            Toast.makeText(getActivity(), location.getLatitude() + " : " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "Can not access current location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiGetAllParlours() {
        url = getString(R.string.url) + "parlours";

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetAllParlours: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    castParloursToList(response);
                } else {
                    Log.e(TAG, "hitApiGetAllParlours : onResponse: " + response.code());
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

    private void castParloursToList(Response response) {
        try {
            allModelList = new ArrayList<>();
            nearestModelList = new ArrayList<>();
            reccomendedModelList = new ArrayList<>();
            tempNearestList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                allModelList.add(new ParlourModel(
                        jsonArray.getJSONObject(i).getString("Id"),
                        jsonArray.getJSONObject(i).getString("FullName"),
                        jsonArray.getJSONObject(i).getDouble("Rating"),
                        jsonArray.getJSONObject(i).getString("PhoneNumber"),
                        jsonArray.getJSONObject(i).getDouble("Latitude"),
                        jsonArray.getJSONObject(i).getDouble("Longitude")
                ));
                nearestModelList.add(new NearestParlourModel(
                        new ParlourModel(
                                jsonArray.getJSONObject(i).getString("Id"),
                                jsonArray.getJSONObject(i).getString("FullName"),
                                jsonArray.getJSONObject(i).getDouble("Rating"),
                                jsonArray.getJSONObject(i).getString("PhoneNumber"),
                                jsonArray.getJSONObject(i).getDouble("Latitude"),
                                jsonArray.getJSONObject(i).getDouble("Longitude")
                        ),
                        new Vincinetyformula().getDistance(
                                lat,
                                lng,
                                jsonArray.getJSONObject(i).getDouble("Latitude"),
                                jsonArray.getJSONObject(i).getDouble("Longitude")
                        )

                ));
                reccomendedModelList.add(new ParlourModel(
                        jsonArray.getJSONObject(i).getString("Id"),
                        jsonArray.getJSONObject(i).getString("FullName"),
                        jsonArray.getJSONObject(i).getDouble("Rating"),
                        jsonArray.getJSONObject(i).getString("PhoneNumber"),
                        jsonArray.getJSONObject(i).getDouble("Latitude"),
                        jsonArray.getJSONObject(i).getDouble("Longitude")
                ));
            }

            //sort by location
            Collections.sort(nearestModelList, new Comparator<NearestParlourModel>() {
                @Override
                public int compare(NearestParlourModel lhs, NearestParlourModel rhs) {
                    if (lhs.getParlourDistance() < rhs.getParlourDistance()) return -1;
                    if (lhs.getParlourDistance() > rhs.getParlourDistance()) return 1;
                    return 0;
                }
            });
            for (int i = 0; i < nearestModelList.size(); i++) {
                tempNearestList.add(nearestModelList.get(i).getParlourDetails());
            }

            //Sort by name
            Collections.sort(allModelList, new Comparator<ParlourModel>() {
                @Override
                public int compare(ParlourModel lhs, ParlourModel rhs) {
                    return lhs.getParlourName().compareTo(rhs.getParlourName());
                }
            });

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpList(allRV, allModelList);
                    setUpList(recommendedRV, reccomendedModelList);
                    setUpList(nearestRV, tempNearestList);
                }
            });
        } catch (Exception e) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Network error, try again later.", Toast.LENGTH_LONG).show();
                }
            });
            Log.e(TAG, "castParloursToList: " + e);
        }
    }

    private void bindControls(View view) {
        nearestSeeAllTV = view.findViewById(R.id.user_home_nearestParlourSeeAllTV);
        recommendedSeeAllTV = view.findViewById(R.id.user_home_recommendedParlourSeeAllTV);
        allSeeAllTV = view.findViewById(R.id.user_home_allParlourSeeAllTV);

        nearestRV = view.findViewById(R.id.user_home_nearestParlourRV);
        recommendedRV = view.findViewById(R.id.user_home_recommendedParlourRV);
        allRV = view.findViewById(R.id.user_home_allParlourRV);
    }

    private void bindListeners() {
        nearestSeeAllTV.setOnClickListener(this);
        recommendedSeeAllTV.setOnClickListener(this);
        allSeeAllTV.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setUpList(RecyclerView recyclerView, List<ParlourModel> list) {
        ArrayList<ParlourModel> tempList = new ArrayList<>();
        if (list.size() == 0) {
            // No parlours in database
            Toast.makeText(getActivity(), "No Parlours found", Toast.LENGTH_SHORT).show();
        } else if (list.size() < 3) {
            for (int i = 0; i < list.size(); i++) {
                tempList.add(list.get(i));
            }
        } else {
            for (int i = 0; i < 3; i++) {
                tempList.add(list.get(i));
            }
        }

        ParlourRecyclerViewAdapter adapter = new ParlourRecyclerViewAdapter(getActivity(), tempList, R.layout.item_parlour_horizontal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if (v == nearestSeeAllTV) {
            startParlourDetailActivity("Nearest", tempNearestList);
        } else if (v == recommendedSeeAllTV) {
            startParlourDetailActivity("Recommended", reccomendedModelList);
        } else if (v == allSeeAllTV) {
            startParlourDetailActivity("All", allModelList);
        }
    }

    private void startParlourDetailActivity(String title, List<ParlourModel> list) {
        Intent intent = new Intent(getActivity(), ListOfParloursActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("list", (ArrayList<ParlourModel>) list);
        startActivity(intent);
    }
}

package com.example.saloonapp.Activities.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saloonapp.Adapters.User.ParlourRecyclerViewAdapter;
import com.example.saloonapp.Models.ParlourModel;
import com.example.saloonapp.R;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FiltersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV, titleTV;
    private AppCompatEditText searchET;
    private AppCompatSpinner serviceSP, subServiceSP, minSP, maxSP;
    private RecyclerView filterRV;
    private List<ParlourModel> parlourModelList;
    private ParlourRecyclerViewAdapter adapter;
    //Api Strings
    private String url, TAG = "FILTERS_ACTIVITY";
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        getValuesOnStart();
        bindControls();
        toolbarSetting();
        setUpList();
        hitApiGetAllService();
    }

    private void getValuesOnStart() {
        parlourModelList = (List<ParlourModel>) getIntent().getSerializableExtra("list");
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);
        searchET = findViewById(R.id.filters_searchET);
        serviceSP = findViewById(R.id.filters_serviceSP);
        subServiceSP = findViewById(R.id.filters_subServiceSP);
        minSP = findViewById(R.id.filters_minSP);
        maxSP = findViewById(R.id.filters_maxSP);
        filterRV = findViewById(R.id.filters_RV);
    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitleTV.setText("FILTERED SEARCH");
    }

    private void setUpList() {
        adapter = new ParlourRecyclerViewAdapter(FiltersActivity.this, parlourModelList, R.layout.item_parlour_vertical);
        filterRV.setHasFixedSize(true);
        filterRV.setLayoutManager(new LinearLayoutManager(FiltersActivity.this));
        filterRV.setAdapter(adapter);
        searchParlour(parlourModelList);
    }

    private void searchParlour(final List<ParlourModel> list) {
        try {
            searchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    filterParloursInAdapter(list, s.toString().trim());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "searchParlour: " + e);
        }
    }

    private void filterParloursInAdapter(List<ParlourModel> list, String text) {
        try {
            List<ParlourModel> filteredList = new ArrayList<>();
            for (ParlourModel item : list) {
                if (item.getParlourName().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
            adapter.filterList(filteredList);
        } catch (Exception e) {
            Log.e(TAG, "filterParloursInAdapter: " + e);
        }

    }

    private void hitApiGetAllService() {
        url = getString(R.string.url) + "services";

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
                        Toast.makeText(FiltersActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "setUpServiceSpinner: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    castServiceSpinner(response);
                } else {
                    Log.e(TAG, "setUpServiceSpinner : onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FiltersActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castServiceSpinner(Response response) {
        try {
            JSONArray jsonArray = new JSONArray(response.body().string());
            final List<String> spinnerItems = new ArrayList<>();
            spinnerItems.add("SERVICES");
            for (int i = 0; i < jsonArray.length(); i++) {
                spinnerItems.add(String.valueOf(jsonArray.get(i)));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FiltersActivity.this, android.R.layout.simple_list_item_1, spinnerItems);
                    serviceSP.setAdapter(adapter);
                    serviceSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            setSpinnerItems((TextView) view, position);
                            if (position == 0) {
                                subServiceSP.setVisibility(View.GONE);
                                setSearchFilterApi();
                            } else {
                                subServiceSP.setVisibility(View.VISIBLE);
                                hitApiGetSubService(serviceSP.getSelectedItem().toString());
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    hitApiGetMinMax();
                }
            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FiltersActivity.this, "Network error, try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "castServiceSpinner: " + e);
        }
    }

    private void hitApiGetSubService(String serviceName) {
        url = getString(R.string.url) + "subservices/byservicename?servicename=" + serviceName;

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
                        Toast.makeText(FiltersActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetSubService: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    castSubServiceSpinner(response);
                } else {
                    Log.e(TAG, "hitApiGetSubService : onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FiltersActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castSubServiceSpinner(Response response) {
        try {
            JSONArray jsonArray = new JSONArray(response.body().string());
            final List<String> spinnerItems = new ArrayList<>();
            spinnerItems.add("SUB SERVICE");
            for (int i = 0; i < jsonArray.length(); i++) {
                spinnerItems.add(String.valueOf(jsonArray.get(i)));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FiltersActivity.this, android.R.layout.simple_list_item_1, spinnerItems);
                    subServiceSP.setAdapter(adapter);
                    subServiceSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            setSpinnerItems((TextView) view, position);
                            setSearchFilterApi();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    hitApiGetMinMax();
                }
            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FiltersActivity.this, "Network error, try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "castServiceSpinner: " + e);
        }
    }

    private void hitApiGetMinMax() {
        url = getString(R.string.url) + "subservices/pricerange";

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
                        Toast.makeText(FiltersActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetMinMax: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    castMinMaxSpinner(response);
                } else {
                    Log.e(TAG, "hitApiGetMinMax : onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FiltersActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castMinMaxSpinner(Response response) {
        try {
            JSONArray jsonArray = new JSONArray(response.body().string());
            List<Integer> spinnerItems = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Double converToInt = Double.valueOf(jsonArray.get(i).toString());
                spinnerItems.add(converToInt.intValue());
            }
            final List<String> minItems = new ArrayList<>();
            minItems.add("MIN");
            final List<String> maxItems = new ArrayList<>();
            maxItems.add("MAX");
            for (int i = spinnerItems.get(0); i <= spinnerItems.get(1); i = i + 100) {
                if (i <= spinnerItems.get(1)) {
                    minItems.add(String.valueOf(i));
                    maxItems.add(String.valueOf(i));
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FiltersActivity.this, android.R.layout.simple_list_item_1, minItems);
                    minSP.setAdapter(adapter);
                    adapter = new ArrayAdapter<>(FiltersActivity.this, android.R.layout.simple_list_item_1, maxItems);
                    maxSP.setAdapter(adapter);

                    minSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            setSpinnerItems((TextView) view, position);
                            setSearchFilterApi();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    maxSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            setSpinnerItems((TextView) view, position);
                            setSearchFilterApi();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            });

        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FiltersActivity.this, "Network error, try again later.", Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "castMinMaxSpinner: " + e);
        }
    }

    private void hitApiGetFilteredParlours(String serviceName, String subServiceName, String minPrice, String maxPrice) {
        url = getString(R.string.url) + "filters/result?servicename=" + serviceName + "&subservicename=" + subServiceName + "&minprice=" + minPrice + "&maxprice=" + maxPrice;

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
                        Toast.makeText(FiltersActivity.this,"Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetFilteredParlours: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code() == 200){
                    castFileredParloursToList(response);
                } else {
                    Log.e(TAG, "hitApiGetFilteredParlours : onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FiltersActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void castFileredParloursToList(Response response) {
        try {
            parlourModelList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                parlourModelList.add(new ParlourModel(
                        jsonArray.getJSONObject(i).getString("ParlourId"),
                        jsonArray.getJSONObject(i).getString("FullName"),
                        jsonArray.getJSONObject(i).getDouble("Rating"),
                        jsonArray.getJSONObject(i).getString("PhoneNumber"),
                        jsonArray.getJSONObject(i).getDouble("Latitude"),
                        jsonArray.getJSONObject(i).getDouble("Longitude")
                ));
            }
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
                    Toast.makeText(FiltersActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                }
            });
            Log.e(TAG, "castFileredParloursToList: " + e);
        }
    }

    private void setSpinnerItems(TextView view, int position) {
        if (position == 0) {
            view.setTextColor(ContextCompat.getColor(FiltersActivity.this, R.color.colorPrimary));
            view.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            view.setTextSize(14);
        } else {
            view.setTextColor(ContextCompat.getColor(FiltersActivity.this, R.color.colorBlack));
            view.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            view.setTextSize(14);
        }
    }

    private String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void setSearchFilterApi() {
        if (serviceSP.getSelectedItemPosition() != 0 && subServiceSP.getSelectedItemPosition() == 0 && minSP.getSelectedItemPosition() == 0 && maxSP.getSelectedItemPosition() == 0) {
            hitApiGetFilteredParlours(serviceSP.getSelectedItem().toString(), "null", "0", "0");
        } else if (serviceSP.getSelectedItemPosition() != 0 && subServiceSP.getSelectedItemPosition() == 0 && minSP.getSelectedItemPosition() == 0 && maxSP.getSelectedItemPosition() != 0) {
            hitApiGetFilteredParlours(serviceSP.getSelectedItem().toString(), "null", "0", maxSP.getSelectedItem().toString());
        }  else if (serviceSP.getSelectedItemPosition() != 0 && subServiceSP.getSelectedItemPosition() == 0 && minSP.getSelectedItemPosition() != 0 && maxSP.getSelectedItemPosition() == 0) {
            hitApiGetFilteredParlours(serviceSP.getSelectedItem().toString(), "null", minSP.getSelectedItem().toString(), "0");
        } else if (serviceSP.getSelectedItemPosition() != 0 && subServiceSP.getSelectedItemPosition() == 0 && minSP.getSelectedItemPosition() != 0 && maxSP.getSelectedItemPosition() != 0) {
            hitApiGetFilteredParlours(serviceSP.getSelectedItem().toString(), "null", minSP.getSelectedItem().toString(), maxSP.getSelectedItem().toString());
        }
        else if (serviceSP.getSelectedItemPosition() != 0 && subServiceSP.getSelectedItemPosition() != 0 && minSP.getSelectedItemPosition() == 0 && maxSP.getSelectedItemPosition() == 0) {
            hitApiGetFilteredParlours(serviceSP.getSelectedItem().toString(), subServiceSP.getSelectedItem().toString(), "0", "0");
        } else if (serviceSP.getSelectedItemPosition() != 0 && subServiceSP.getSelectedItemPosition() != 0 && minSP.getSelectedItemPosition() == 0 && maxSP.getSelectedItemPosition() != 0) {
            hitApiGetFilteredParlours(serviceSP.getSelectedItem().toString(), subServiceSP.getSelectedItem().toString(), "0", maxSP.getSelectedItem().toString());
        }  else if (serviceSP.getSelectedItemPosition() != 0 && subServiceSP.getSelectedItemPosition() != 0 && minSP.getSelectedItemPosition() != 0 && maxSP.getSelectedItemPosition() == 0) {
            hitApiGetFilteredParlours(serviceSP.getSelectedItem().toString(), subServiceSP.getSelectedItem().toString(), minSP.getSelectedItem().toString(), "0");
        } else if (serviceSP.getSelectedItemPosition() != 0 && subServiceSP.getSelectedItemPosition() != 0 && minSP.getSelectedItemPosition() != 0 && maxSP.getSelectedItemPosition() != 0) {
            hitApiGetFilteredParlours(serviceSP.getSelectedItem().toString(), subServiceSP.getSelectedItem().toString(), minSP.getSelectedItem().toString(), maxSP.getSelectedItem().toString());
        }
        else if (serviceSP.getSelectedItemPosition() == 0 && subServiceSP.getSelectedItemPosition() == 0 && minSP.getSelectedItemPosition() == 0 && maxSP.getSelectedItemPosition() != 0) {
            hitApiGetFilteredParlours("null", "null", "0", maxSP.getSelectedItem().toString());
        }  else if (serviceSP.getSelectedItemPosition() == 0 && subServiceSP.getSelectedItemPosition() == 0 && minSP.getSelectedItemPosition() != 0 && maxSP.getSelectedItemPosition() == 0) {
            hitApiGetFilteredParlours("null", "null", minSP.getSelectedItem().toString(), "0");
        } else if (serviceSP.getSelectedItemPosition() == 0 && subServiceSP.getSelectedItemPosition() == 0 && minSP.getSelectedItemPosition() != 0 && maxSP.getSelectedItemPosition() != 0) {
            hitApiGetFilteredParlours("null", "null", minSP.getSelectedItem().toString(), maxSP.getSelectedItem().toString());
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

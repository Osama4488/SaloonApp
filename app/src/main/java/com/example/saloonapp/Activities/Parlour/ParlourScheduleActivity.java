package com.example.saloonapp.Activities.Parlour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saloonapp.Activities.Common.LoginActivity;
import com.example.saloonapp.Adapters.Parlour.ParlourScheduleRecyclerViewAdapter;
import com.example.saloonapp.Adapters.Parlour.ParlourScheduleRecyclerViewAdapter.ItemParlourScheduleViewHolder;
import com.example.saloonapp.Models.ScheduleModel;
import com.example.saloonapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParlourScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;
    private RecyclerView scheduleRV;
    private AppCompatButton doneBN;
    private List<String> dayNames;
    private List<ScheduleModel> scheduleModelList;

    private String name, email, contact, location, pass, confirmPass;

    //API strings
    private String url, TAG = "PARLOUR_SCHEDULE_ACTIVITY";
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parlour_schedule);

        getVaulesOnStart();
        bindControls();
        bindListeners();
        toolbarSetting();
        setUpList();
    }

    private void bindListeners() {
        doneBN.setOnClickListener(this);
    }

    private void getVaulesOnStart() {
        name = getIntent().getExtras().getString("name", "not found");
        email = getIntent().getExtras().getString("email", "not found");
        contact = getIntent().getExtras().getString("contact", "not found");
        location = getIntent().getExtras().getString("location", "not found");
        pass = getIntent().getExtras().getString("pass", "not found");
        confirmPass = getIntent().getExtras().getString("confirmPass", "not found");
    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitleTV.setText("Choose Working Days");
    }

    private void setUpList() {
        dayNames = new ArrayList<>();
        dayNames.add("Monday");
        dayNames.add("Tuesday");
        dayNames.add("Wednesday");
        dayNames.add("Thursday");
        dayNames.add("Friday");
        dayNames.add("Saturday");
        dayNames.add("Sunday");

        ParlourScheduleRecyclerViewAdapter adapter = new ParlourScheduleRecyclerViewAdapter(ParlourScheduleActivity.this, dayNames);
        scheduleRV.setHasFixedSize(true);
        scheduleRV.setLayoutManager(new LinearLayoutManager(this));
        scheduleRV.setAdapter(adapter);

    }

    private void bindControls() {
        scheduleRV = findViewById(R.id.parlour_schedule_RV);
        doneBN = findViewById(R.id.parlour_schedule_doneBN);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);
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

    @Override
    public void onClick(View v) {
        if (v == doneBN) {
            getRecyclerViewValues();
        }
    }

    private void getRecyclerViewValues() {
        scheduleModelList = new ArrayList<>();
        for (int i = 0; i < dayNames.size(); i++) {
            ItemParlourScheduleViewHolder holder = (ItemParlourScheduleViewHolder) scheduleRV.findViewHolderForAdapterPosition(i);
            CheckBox dayCB = holder.itemView.findViewById(R.id.parlour_schedule_dayCB);
            Spinner fromFormatSP = holder.itemView.findViewById(R.id.parlour_schedule_fromFormatSP);
            Spinner fromSP = holder.itemView.findViewById(R.id.parlour_schedule_fromSP);
            Spinner toFormatSP = holder.itemView.findViewById(R.id.parlour_schedule_toFormatSP);
            Spinner toSP = holder.itemView.findViewById(R.id.parlour_schedule_toSP);
            if (dayCB.isChecked()){
                String openTime = fromSP.getSelectedItem().toString() + ":00 " + fromFormatSP.getSelectedItem().toString();
                String closeTime = toSP.getSelectedItem().toString() + ":00 " + toFormatSP.getSelectedItem().toString();
                scheduleModelList.add(new ScheduleModel(
                        dayCB.getText().toString(),
                        openTime,
                        closeTime
                ));
            }
        }
        if (scheduleModelList.size() != 0) {
            hitApiRegisterParlour();
        } else {
            Toast.makeText(this, "Choose a working day", Toast.LENGTH_LONG).show();
        }
    }

    private void hitApiRegisterParlour() {
        url = getString(R.string.url) + "account/registerparlour";

        JSON = MediaType.parse( "application/json; charset=utf-8" );

        client = new OkHttpClient.Builder()
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < scheduleModelList.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("Day", scheduleModelList.get(i).getDayName());
                object.put("Open", scheduleModelList.get(i).getOpenTime());
                object.put("Close", scheduleModelList.get(i).getCloseTime());
                jsonArray.put(object);
            }
            String[] arr = location.split(",");
            jsonObject.put("FullName", name);
            jsonObject.put("Email", email);
            jsonObject.put("Phone", contact);
            jsonObject.put("Latitude", arr[0]);
            jsonObject.put("Longitude", arr[1]);
            jsonObject.put("Password", pass);
            jsonObject.put("ConfirmPassword", confirmPass);
            jsonObject.put("ParlourTimings", jsonArray);
        } catch (JSONException e) {
            Log.e(TAG, "hitApiRegisterParlour: " + e);
        }

        RequestBody body = RequestBody.create( JSON, jsonObject.toString() );
        request = new Request.Builder()
                .url( url )
                .post( body )
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ParlourScheduleActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(TAG, "hitApiRegisterParlour: onFailure: "+e);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (response.code() == 200){
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    Toast.makeText( ParlourScheduleActivity.this, "Parlour account successfully created. Login to continue", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        Log.e(TAG, "hitApiRegisterParlour: onResponse: " + response.code() );
                        JSONObject serverResponse = new JSONObject(response.body().string());
                        final JSONArray errorMsg = serverResponse.getJSONObject("ModelState").getJSONArray("");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(ParlourScheduleActivity.this, errorMsg.get(1).toString(), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ParlourScheduleActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                            }
                        });
                        Log.e(TAG, "hitApiRegisterParlour: onResponse: " + e);
                    }
                }
            }
        } );
    }
}

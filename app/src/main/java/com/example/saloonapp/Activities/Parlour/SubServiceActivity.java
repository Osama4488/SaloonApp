package com.example.saloonapp.Activities.Parlour;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.Toast;

import com.example.saloonapp.Adapters.Parlour.SubServicesRecyclerViewAdapter;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SubServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private AppCompatTextView toolbarTitleTV;

    private FloatingActionButton addSubServiceFAB;
    private List<SubServicesModel> subServicesModelList;
    private RecyclerView subServicesRV;
    private AppCompatTextView titleTV;

    // Dialog Controls
    private AlertDialog addSubServiceDialog;
    private AppCompatTextView dialog_subServiceTitleTV;
    private AppCompatButton dialog_addSubServiceBN;
    private TextInputLayout dialog_subServiceNameTIL, dialog_subServicePriceTIL, dialog_subServiceDescriptionTIL;
    private AppCompatEditText dialog_subServiceNameET, dialog_subServicePriceET, dialog_subServiceDescriptionET;
    private AppCompatImageButton dialog_closeIB;

    private String title, serviceId, TAG = "SUB_SERVICE_ACTIVITY";

    //Api Strings
    private String url;
    private MediaType JSON;
    private OkHttpClient client;
    private Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_service);

        getValuesOnStart();
        bindControls();
        bindListeners();
        toolbarSetting();
        hitApiGetAllSubServices();
    }

    private void getValuesOnStart() {
        title = getIntent().getExtras().getString("serviceName" , "not found");
        serviceId = getIntent().getExtras().getString("serviceId" , null);
    }

    private void bindControls() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = toolbar.findViewById(R.id.toolbarTitleTV);

        addSubServiceFAB = findViewById(R.id.subService_addSubServiceFAB);
        subServicesRV = findViewById(R.id.subService_RV);
        titleTV = findViewById(R.id.subService_titleTV);

    }

    private void bindListeners() {
        addSubServiceFAB.setOnClickListener(this);
    }

//    private void dummyList() {
//        subServicesModelList = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            subServicesModelList.add(new SubServicesModel(
//                    i,
//                    "Sub Service " + i,
//                    "Description " + i,
//                    i * 100
//            ));
//        }
//        setUpList();
//    }

    private String getToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("access_token", null);
    }

    private void hitApiGetAllSubServices(){
        url = getString( R.string.url )+ "subservices/byserviceid?serviceid=" + serviceId;

        client = new OkHttpClient.Builder()
                .build();

        request = new Request.Builder()
                .url(url)
                .header( "Authorization","Bearer " + getToken() )
                .build();

        client.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SubServiceActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiGetAllSubServices: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    castSubServicesToList(response);
                }else{
                    Log.e( TAG, "hitApiGetAllSubServices: onResponse: " + response.code());
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText( SubServiceActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    } );
                }
            }
        } );
    }

    private void castSubServicesToList(Response response) {
        try {
            subServicesModelList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                subServicesModelList.add(new SubServicesModel(
                        jsonArray.getJSONObject(i).getString("Id"),
                        jsonArray.getJSONObject(i).getString("Name"),
                        jsonArray.getJSONObject(i).getString("Description"),
                        jsonArray.getJSONObject(i).getString("ServiceId"),
                        jsonArray.getJSONObject(i).getString("Price")
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
                    Toast.makeText(SubServiceActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                }
            });
            Log.e(TAG, "castSubServicesToList: " + e);
        }
    }

    private void setUpList() {
        if (subServicesModelList.size() == 0) {
            controlsVisibility(View.VISIBLE);
        } else {
            controlsVisibility(View.GONE);
            SubServicesRecyclerViewAdapter adapter = new SubServicesRecyclerViewAdapter(this, title, subServicesModelList, subServicesRV, titleTV);
            subServicesRV.setHasFixedSize(true);
            subServicesRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            subServicesRV.setAdapter(adapter);
        }
    }

    private void toolbarSetting() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitleTV.setText(title.toUpperCase());
    }

    private void controlsVisibility(int visibliltiy) {
        if (View.VISIBLE == visibliltiy) {
            titleTV.setVisibility(View.VISIBLE);
            titleTV.setText("'+' to add " + title + " service");
            subServicesRV.setVisibility(View.GONE);
        } else {
            titleTV.setVisibility(View.GONE);
            subServicesRV.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        if (v == addSubServiceFAB) {
            showAddSubServiceDialog();
        } else if (v == dialog_closeIB) {
            addSubServiceDialog.dismiss();
        } else if (v == dialog_addSubServiceBN) {
            addSubService();
        }
    }

    private void addSubService() {
        String subServiceName = dialog_subServiceNameET.getText().toString();
        String subServicePrice = dialog_subServicePriceET.getText().toString();
        String subServiceDescription = dialog_subServiceDescriptionET.getText().toString();
        if (checkIsNullOrEmpty(subServiceName) || checkIsNullOrEmpty(subServicePrice) || checkIsNullOrEmpty(subServiceDescription)) {
            if (checkIsNullOrEmpty(subServiceName)) {
                dialog_subServiceNameTIL.setErrorEnabled(true);
                dialog_subServiceNameTIL.setError("Can not be empty");
            }
            if (checkIsNullOrEmpty(subServicePrice)) {
                dialog_subServicePriceTIL.setErrorEnabled(true);
                dialog_subServicePriceTIL.setError("Can not be empty");
            }
            if (checkIsNullOrEmpty(subServiceDescription)) {
                dialog_subServiceDescriptionTIL.setErrorEnabled(true);
                dialog_subServiceDescriptionTIL.setError("Can not be empty");
            }
            animateDialog(addSubServiceDialog);
        } else if (!(dialog_subServiceNameTIL.isErrorEnabled() && dialog_subServicePriceTIL.isErrorEnabled() && dialog_subServiceDescriptionTIL.isErrorEnabled())) {
            hitApiAddSubServices(subServiceName, subServiceDescription, subServicePrice);
        }
    }

    private void hitApiAddSubServices(String subServiceName, String subServiceDescription, String subServicePrice) {
        url = getString(R.string.url) + "subservices";

        JSON = MediaType.parse("application/json; charset=utf-8");

        client = new OkHttpClient.Builder()
                .build();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", subServiceName);
            jsonObject.put("Price", subServicePrice);
            jsonObject.put("Description", subServiceDescription);
            jsonObject.put("ServiceId", serviceId);
        } catch (Exception e) {
            Log.e(TAG, "hitApiAddSubServices: " + e);
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
                        Toast.makeText(SubServiceActivity.this, "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e(TAG, "hitApiAddSubServices: onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    addSubServiceToList(response);
                } else {
                    Log.e(TAG, "hitApiAddSubServices: onResponse: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SubServiceActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        } );
    }

    private void addSubServiceToList(Response response) {
        try {
            JSONObject jsonObject = new JSONObject(response.body().string());
            subServicesModelList.add(new SubServicesModel(
                    jsonObject.getString("Id"),
                    jsonObject.getString("Name"),
                    jsonObject.getString("Description"),
                    jsonObject.getString("ServiceId"),
                    jsonObject.getString("Price")
            ));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpList();
                    addSubServiceDialog.dismiss();
                }
            });
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SubServiceActivity.this, "Network error, try again later.", Toast.LENGTH_LONG).show();
                }
            });
            Log.e(TAG, "addSubServiceToList: " + e);
        }
    }

    private void animateDialog(AlertDialog dialog) {
        dialog.getWindow()
                .getDecorView()
                .animate()
                .translationX(16f)
                .setInterpolator(new CycleInterpolator(7f));
    }

    private void showAddSubServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_sub_service, null);
        builder.setView(dialogView);

        bindDialogControls(dialogView);
        bindDialogListeners();
        setDialogEdittextError();

        addSubServiceDialog = builder.create();
        addSubServiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addSubServiceDialog.setCanceledOnTouchOutside(true);
        addSubServiceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addSubServiceDialog.show();
    }

    private void setDialogEdittextError() {
        dialog_subServiceNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String subServiceName = dialog_subServiceNameET.getText().toString();
                if (!(checkIsNullOrEmpty(subServiceName))) {
                    dialog_subServiceNameTIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_subServicePriceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String subServicePrice = dialog_subServicePriceET.getText().toString();
                if (subServicePrice.equals("0")) {
                    dialog_subServicePriceTIL.setErrorEnabled(true);
                    dialog_subServicePriceTIL.setError("Price can not be '0'");
                } else {
                    if (!(checkIsNullOrEmpty(subServicePrice))) {
                        dialog_subServicePriceTIL.setErrorEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog_subServiceDescriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String expertExperience = dialog_subServiceDescriptionET.getText().toString();
                if (!(checkIsNullOrEmpty(expertExperience))) {
                    dialog_subServiceDescriptionTIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean checkIsNullOrEmpty(String value) {
        if (value.isEmpty() || value.equals("")){
            return true;
        } else {
            return false;
        }
    }

    private void bindDialogListeners() {
        dialog_closeIB.setOnClickListener(this);
        dialog_addSubServiceBN.setOnClickListener(this);
    }

    private void bindDialogControls(View dialogView) {
        dialog_subServiceTitleTV = dialogView.findViewById(R.id.dialog_subServiceTitleTV);
        dialog_subServiceNameTIL = dialogView.findViewById(R.id.dialog_subServiceNameTIL);
        dialog_subServicePriceTIL = dialogView.findViewById(R.id.dialog_subServicePriceTIL);
        dialog_subServiceDescriptionTIL = dialogView.findViewById(R.id.dialog_subServiceDescriptionTIL);
        dialog_subServiceNameET = dialogView.findViewById(R.id.dialog_subServiceNameET);
        dialog_subServicePriceET = dialogView.findViewById(R.id.dialog_subServicePriceET);
        dialog_subServiceDescriptionET = dialogView.findViewById(R.id.dialog_subServiceDescriptionET);
        dialog_closeIB = dialogView.findViewById(R.id.dialog_closeIB);
        dialog_addSubServiceBN = dialogView.findViewById(R.id.dialog_addSubServiceBN);

        dialog_subServiceTitleTV.setText("Add " + title + " Service");
    }
}
